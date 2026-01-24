import { http } from '@/utils/http'

export type UserResult = {

  success: boolean

  data: {

    /** 头像 */
    avatar: string

    /** 用户名 */
    username: string

    /** 昵称 */
    nickname: string

    /** 当前登录用户的角色 */
    roles: Array<string>

    /** 按钮级别权限 */
    permissions: Array<string>

    /** `token` */
    accessToken: string

    /** 用于调用刷新`accessToken`的接口时所需的`token` */
    refreshToken: string

    /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
    expires: Date
  }
}

export type RefreshTokenResult = {
  success: boolean
  data: {
    /** `token` */
    accessToken: string;
    /** 用于调用刷新`accessToken`的接口时所需的`token` */
    refreshToken: string;
    /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
    expires: Date;
  }
}

const userApis = {
  login: (data: any): Promise<any> => http.post('/user/login', { data }),
  authorize(loginId: string): Promise<Response> {
    const params = {
      'response_type': 'code',
      'client_id': 'microservice-demo-gateway',
      'scope': 'all',
      'redirect_uri': 'http://localhost:8080/fakePath/oauth2/callback'
    }
    const query = new URLSearchParams(params).toString()
    return fetch(`${import.meta.env.VITE_API_BASE_URL}/auth/oauth2/authorize?${query}`, {
      headers: {
        'X-Login-ID': loginId
      }
    })
  },
  refreshToken: (data: any): Promise<RefreshTokenResult> => http.post('/refresh-token', { data })
}

export default userApis
