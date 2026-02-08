import { http } from '@/utils/http'

const routeApis = {
  asyncRoutes: () => http.get('/get-async-routes', { showMsgOnError: false })
}

export default routeApis
