import { http } from '@/utils/http'

const userApis = {
  login: (data: any): Promise<any> => http.post('/auth/login', { data }),
  authorize(loginId: string): Promise<any> {
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
  token: (authCode: string): Promise<any> => http.post('/auth/oauth2/token', {
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
  refreshToken: (refreshToken: string): Promise<any> => http.post('/auth/oauth2/token', {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': import.meta.env.VITE_OAUTH2_API_AUTH_HEADER
    },
    data: new URLSearchParams({
      'grant_type': 'refresh_token',
      'refresh_token': refreshToken
    }).toString()
  }),
  self(token?: string): Promise<any> {
    const headers = {}
    if(token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    return http.get('/user/user/self', { headers })
  }
}

export default userApis
