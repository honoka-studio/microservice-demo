import { useUserStoreHook } from '@/store/modules/user'
import { formatToken, getToken } from '@/utils/auth'
import { message } from '@/utils/message'
import Axios, { type AxiosInstance, type AxiosRequestConfig, type CustomParamsSerializer } from 'axios'
import { stringify } from 'qs'
import type { PureHttpError, PureHttpRequestConfig, PureHttpResponse, RequestMethods } from './types.d'

// 相关配置请参考：www.axios-js.com/zh-cn/docs/#axios-request-config-1
class PureHttp {

  /** 保存当前`Axios`实例对象 */
  private axiosInstance: AxiosInstance = Axios.create(defaultConfig)

  /** `token`过期后，暂存待执行的请求 */
  private requests = []

  /** 防止重复刷新`token` */
  private isRefreshing = false

  /** 初始化配置对象 */
  private initConfig: PureHttpRequestConfig = {}

  constructor() {
    this.httpInterceptorsRequest()
    this.httpInterceptorsResponse()
  }

  /** 请求拦截 */
  private httpInterceptorsRequest() {
    this.axiosInstance.interceptors.request.use(
      async (config: PureHttpRequestConfig): Promise<any> => {
        // 优先判断post/get等方法是否传入回调，否则执行初始化设置等回调
        if(typeof config.beforeRequestCallback === 'function') {
          config.beforeRequestCallback(config)
          return config
        }
        if(this.initConfig.beforeRequestCallback) {
          this.initConfig.beforeRequestCallback(config)
          return config
        }
        if(config.showMsgOnError == null) {
          config.showMsgOnError = true
        }
        if(config.headers['Authorization']) {
          return config
        }
        const token = getToken()
        if(token) {
          const now = new Date().getTime()
          const expired = parseInt(token.expires) - now <= 0
          if(expired && !config.noRefreshToken) {
            //noinspection ES6MissingAwait
            this.refreshTokenAndDoRequest(token.refreshToken)
            return this.retryOriginalRequest(config)
          } else {
            config.headers['Authorization'] = formatToken(token.accessToken)
            return config
          }
        } else {
          return config
        }
      },
      error => {
        return Promise.reject(error)
      }
    )
  }

  /** 响应拦截 */
  private httpInterceptorsResponse() {
    this.axiosInstance.interceptors.response.use(
      (response: PureHttpResponse) => {
        const request = response.config
        // 优先判断post/get等方法是否传入回调，否则执行初始化设置等回调
        if(typeof request.beforeResponseCallback === 'function') {
          request.beforeResponseCallback(response)
          return null
        }
        if(this.initConfig.beforeResponseCallback) {
          this.initConfig.beforeResponseCallback(response)
          return null
        }
        return response.data
      },
      async (error: PureHttpError) => {
        let request = error.config
        if(request.ignoreErrors) {
          return Promise.resolve()
        }
        let response = error.response
        let newResponse = await this.redoIf401(response)
        if(newResponse) {
          response = newResponse
        }
        const apiError = this.checkError(response)
        if(apiError) {
          return Promise.reject(apiError)
        }
        console.error(`Request ${request.url} error: `, error)
        message(error.message ?? `请求失败：${request.url}`, { type: 'error' })
        error.isCancelRequest = Axios.isCancel(error)
        // 所有的响应异常 区分来源为取消请求/非取消请求
        return Promise.reject(error)
      }
    )
  }

  private async refreshTokenAndDoRequest(refreshToken: string) {
    if(this.isRefreshing) return
    this.isRefreshing = true
    try {
      const res = await useUserStoreHook().handRefreshToken({ refreshToken })
      this.requests.forEach(cb => cb(res.accessToken))
    } catch {
      this.requests.forEach(cb => cb(null))
      await useUserStoreHook().logout()
    } finally {
      this.requests = []
      this.isRefreshing = false
    }
  }

  /** 重连原始请求 */
  private retryOriginalRequest(config: PureHttpRequestConfig) {
    return new Promise((resolve, reject) => {
      this.requests.push((token: string) => {
        if(token) {
          config.headers['Authorization'] = formatToken(token)
          resolve(config)
        } else {
          console.error(`Rejected request: ${config.url}`, config)
          reject()
        }
      })
    })
  }

  private async redoIf401(response: PureHttpResponse): Promise<any> {
    let request = response.config
    if(request.ignore401) return
    if(response.status !== 401) return
    let token = getToken()
    if(!token) return
    await this.refreshTokenAndDoRequest(token.refreshToken)
    token = getToken()
    if(!token || !token.accessToken) return
    request.ignore401 = true
    delete request.headers['Authorization']
    return await this.axiosInstance.request(request)
  }

  private checkError(response: PureHttpResponse): Error {
    if(!response) return null
    const body = response.data
    const failed = response.status !== 200 || body.success === false
    if(!failed) return null
    const path = response.config.url
    let error = new Error(`API ${path} error: ${body.msg}`)
    if(response.config.showMsgOnError) {
      console.error(error)
      message(body.msg ?? `API调用失败：${path}`, { type: 'error' })
    }
    return error
  }

  /** 通用请求工具函数 */
  public request<T>(method: RequestMethods, url: string, config?: PureHttpRequestConfig): Promise<T> {
    const realConfig = {
      method,
      url,
      ...config
    } as PureHttpRequestConfig

    // 单独处理自定义请求/响应回调
    return new Promise((resolve, reject) => {
      this.axiosInstance.request(realConfig).then((response: any) => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  }

  /** 单独抽离的`get`工具函数 */
  public get<T = any>(url: string, config?: PureHttpRequestConfig): Promise<T> {
    return this.request<T>('get', url, config)
  }

  /** 单独抽离的`post`工具函数 */
  public post<T = any>(url: string, config?: PureHttpRequestConfig): Promise<T> {
    return this.request<T>('post', url, config)
  }
}

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

export const http = new PureHttp()
