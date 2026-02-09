import { http } from '@/utils/http'

const routeApis = {
  asyncRoutes: () => http.get('/get-async-routes', { ignoreErrors: true }),
  routeAuthorities: () => http.get('/auth/auth/web/routeAuthorities')
}

export default routeApis
