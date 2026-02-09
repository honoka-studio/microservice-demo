const Layout = () => import('@/layout/index.vue')

export const indexRoute: RouteConfigsTable = {
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
      component: () => import('@/views/home/index.vue'),
      meta: {
        title: '首页',
        showLink: import.meta.env.VITE_HIDE_HOME !== 'true'
      }
    }
  ]
}

export const basicRoutes: Array<RouteConfigsTable> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      showLink: false
    }
  },
  {
    path: '/redirect',
    component: Layout,
    meta: {
      title: '加载中...',
      showLink: false
    },
    children: [
      {
        path: '/redirect/:path(.*)',
        name: 'Redirect',
        component: () => import('@/layout/redirect.vue')
      }
    ]
  },
  {
    path: '/error',
    component: Layout,
    redirect: '/error/403',
    meta: {
      title: '异常页面',
      showLink: false
    },
    children: [
      {
        path: '/error/403',
        name: '403',
        component: () => import('@/views/error/403.vue'),
        meta: {
          title: '403'
        }
      },
      {
        path: '/error/404',
        name: '404',
        component: () => import('@/views/error/404.vue'),
        meta: {
          title: '404'
        }
      },
      {
        path: '/error/500',
        name: '500',
        component: () => import('@/views/error/500.vue'),
        meta: {
          title: '500'
        }
      }
    ]
  }
]

export const pathMatchRoute: RouteConfigsTable = {
  path: '/:pathMatch(.*)*',
  name: 'PageNotFound',
  component: () => import('@/views/error/404.vue'),
  meta: {
    title: '404',
    showLink: false
  }
}
