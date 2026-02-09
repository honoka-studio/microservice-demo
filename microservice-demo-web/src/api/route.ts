import { http } from '@/utils/http'

const routeApis = {
  asyncRoutes: () => http.get('/get-async-routes', { ignoreErrors: true })
}

export default routeApis
