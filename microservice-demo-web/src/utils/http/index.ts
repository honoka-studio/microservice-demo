import { useUserStoreHook } from '@/store/modules/user'
import { formatToken, getToken } from '@/utils/auth'
import { message } from '@/utils/message'
import Axios, { type AxiosInstance, type AxiosRequestConfig, type CustomParamsSerializer } from 'axios'
import { stringify } from 'qs'
import type { PureHttpError, PureHttpRequestConfig, PureHttpResponse, RequestMethods } from './types.d'

// 相关配置请参考：www.axios-js.com/zh-cn/docs/#axios-request-config-1
const defaultConfig: AxiosRequestConfig = {
  baseURL: import.meta.env.VITE_API_BASE_URL,
  // 请求超时时间
  timeout: 10000,
  headers: {
    'Accept': 'application/json, text/plain, */*',
    'Content-Type': 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  },
  // 数组格式参数序列化（https://github.com/axios/axios/issues/5142）
  paramsSerializer: {
    serialize: stringify as unknown as CustomParamsSerializer
  }
}

class PureHttp {

  constructor() {
    this.httpInterceptorsRequest()
    this.httpInterceptorsResponse()
  }

  /** `token`过期后，暂存待执行的请求 */
  private static requests = []

  /** 防止重复刷新`token` */
  private static isRefreshing = false

  /** 初始化配置对象 */
  private static initConfig: PureHttpRequestConfig = {}

  /** 保存当前`Axios`实例对象 */
  private static axiosInstance: AxiosInstance = Axios.create(defaultConfig)

  /** 重连原始请求 */
  private static retryOriginalRequest(config: PureHttpRequestConfig) {
    return new Promise(resolve => {
      PureHttp.requests.push((token: string) => {
        config.headers['Authorization'] = formatToken(token)
        resolve(config)
      })
    })
  }

  /** 请求拦截 */
  private httpInterceptorsRequest(): void {
    PureHttp.axiosInstance.interceptors.request.use(
      async(config: PureHttpRequestConfig): Promise<any> => {
        // 优先判断post/get等方法是否传入回调，否则执行初始化设置等回调
        if(typeof config.beforeRequestCallback === 'function') {
          config.beforeRequestCallback(config)
          return config
        }
        if(PureHttp.initConfig.beforeRequestCallback) {
          PureHttp.initConfig.beforeRequestCallback(config)
          return config
        }
        return config.headers['Authorization'] ? config : new Promise(resolve => {
          const data = getToken()
          if(data) {
            const now = new Date().getTime()
            const expired = parseInt(data.expires) - now <= 0
            if(expired) {
              if(!PureHttp.isRefreshing) {
                PureHttp.isRefreshing = true
                // token过期刷新
                useUserStoreHook().handRefreshToken({ refreshToken: data.refreshToken }).then(res => {
                  PureHttp.requests.forEach(cb => cb(res.data.accessToken))
                  PureHttp.requests = []
                }).catch(() => {
                  useUserStoreHook().logOut()
                }).finally(() => {
                  PureHttp.isRefreshing = false
                })
              }
              resolve(PureHttp.retryOriginalRequest(config))
            } else {
              config.headers['Authorization'] = formatToken(data.accessToken)
              resolve(config)
            }
          } else {
            resolve(config)
          }
        })
      },
      error => {
        return Promise.reject(error)
      }
    )
  }

  /** 响应拦截 */
  private httpInterceptorsResponse(): void {
    const instance = PureHttp.axiosInstance
    instance.interceptors.response.use(
      (response: PureHttpResponse) => {
        const $config = response.config
        // 优先判断post/get等方法是否传入回调，否则执行初始化设置等回调
        if(typeof $config.beforeResponseCallback === 'function') {
          $config.beforeResponseCallback(response)
          return null
        }
        if(PureHttp.initConfig.beforeResponseCallback) {
          PureHttp.initConfig.beforeResponseCallback(response)
          return null
        }
        const error = this.checkError(response)
        if(error) {
          return Promise.reject(error)
        }
        return response.data
      },
      (error: PureHttpError) => {
        const apiError = this.checkError(error.response)
        if(apiError) {
          return Promise.reject(apiError)
        }
        const path = error.config.url
        console.error(`Request ${path} error: `, error)
        message(error.message ?? `请求失败：${path}`, { type: 'error' })
        error.isCancelRequest = Axios.isCancel(error)
        // 所有的响应异常 区分来源为取消请求/非取消请求
        return Promise.reject(error)
      }
    )
  }

  private checkError(response?: PureHttpResponse): Error {
    if(!response) return null
    const body = response.data
    const failed = response.status !== 200 || body.success === false
    if(!failed) return null
    const path = response.config.url
    let error = new Error(`API ${path} error: ` + body.msg)
    console.error(error)
    message(body.msg ?? `API调用失败：${path}`, { type: 'error' })
    return error
  }

  /** 通用请求工具函数 */
  public request<T>(
    method: RequestMethods,
    url: string,
    param?: AxiosRequestConfig,
    axiosConfig?: PureHttpRequestConfig
  ): Promise<T> {
    const config = {
      method,
      url,
      ...param,
      ...axiosConfig
    } as PureHttpRequestConfig

    // 单独处理自定义请求/响应回调
    return new Promise((resolve, reject) => {
      PureHttp.axiosInstance
        .request(config)
        .then((response: undefined) => {
          resolve(response)
        })
        .catch(error => {
          reject(error)
        })
    })
  }

  /** 单独抽离的`post`工具函数 */
  public post<T, P = any>(
    url: string,
    axiosConfig?: AxiosRequestConfig<P>,
    config?: PureHttpRequestConfig
  ): Promise<T> {
    return this.request<T>('post', url, axiosConfig, config)
  }

  /** 单独抽离的`get`工具函数 */
  public get<T, P = any>(
    url: string,
    axiosConfig?: AxiosRequestConfig<P>,
    config?: PureHttpRequestConfig
  ): Promise<T> {
    return this.request<T>('get', url, axiosConfig, config)
  }
}

export const http = new PureHttp()
