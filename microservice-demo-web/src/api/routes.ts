import { http } from '@/utils/http'

type Result = {
  success: boolean;
  data: Array<any>;
};

export const getAsyncRoutes = () => {
  return http.get<Result>('http://localhost:5173/get-async-routes')
}
