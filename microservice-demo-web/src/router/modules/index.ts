const Layout = (): any => import('@/layout/index.vue')

export default {
  path: '/',
  name: 'Index',
  component: Layout,
  redirect: '/home',
  meta: {
    icon: 'ep/home-filled',
    title: '首页',
    rank: 0
  },
  children: [
    {
      path: '/home',
      name: 'Home',
      component: (): any => import('@/views/home/index.vue'),
      meta: {
        title: '首页',
        showLink: import.meta.env.VITE_HIDE_HOME !== 'true'
      }
    }
  ]
} satisfies RouteConfigsTable
