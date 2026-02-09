import { getToken } from '@/utils/auth'
import { http } from '@/utils/http'

const userApis = {
  login: (data: any) => http.post('/auth/login', { data }),
  authorize(loginId: string) {
    const query = new URLSearchParams({
      'response_type': 'code',
      'client_id': 'microservice-demo-web',
      'scope': 'all',
      'redirect_uri': import.meta.env.VITE_OAUTH2_CALLBACK
    }).toString()
    return http.get(`/auth/oauth2/authorize?${query}`, {
      headers: {
        'X-Login-ID': loginId
      }
    })
  },
  token: (authCode: string) => http.post('/auth/oauth2/token', {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': import.meta.env.VITE_OAUTH2_API_AUTH_HEADER
    },
    data: new URLSearchParams({
      'grant_type': 'authorization_code',
      'code': authCode,
      'redirect_uri': import.meta.env.VITE_OAUTH2_CALLBACK
    }).toString()
  }),
  refreshToken: (refreshToken: string) => http.post('/auth/oauth2/token', {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': import.meta.env.VITE_OAUTH2_API_AUTH_HEADER
    },
    data: new URLSearchParams({
      'grant_type': 'refresh_token',
      'refresh_token': refreshToken
    }).toString()
  }),
  logout: () => http.get('/auth/logout', {
    ignoreErrors: true,
    noRefreshToken: true
  }),
  revokeRefreshToken: () => http.post('/auth/oauth2/revoke', {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': import.meta.env.VITE_OAUTH2_API_AUTH_HEADER
    },
    data: new URLSearchParams({
      'token_type_hint': 'refresh_token',
      'token': getToken().refreshToken
    }).toString(),
    ignoreErrors: true
  }),
  self(token?: string) {
    const headers = {}
    if(token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    return http.get('/user/user/self', { headers })
  }
}

export default userApis
