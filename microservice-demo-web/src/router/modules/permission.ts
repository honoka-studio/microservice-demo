export default {
  path: '/permission',
  redirect: '/permission/page',
  meta: {
    title: '权限管理',
    icon: 'ep:lollipop',
    rank: 10
  },
  children: [
    {
      path: '/permission/page',
      name: 'PermissionPage',
      component: () => import('@/views/permission/page/index.vue'),
      meta: {
        title: '页面权限'
      }
    },
    {
      path: '/permission/button',
      name: 'PermissionButton',
      redirect: '/permission/button/router',
      meta: {
        title: '按钮权限'
      },
      children: [
        {
          path: '/permission/button/router',
          name: 'PermissionButtonRouter',
          component: () => import('@/views/permission/button/index.vue'),
          meta: {
            title: '路由返回按钮权限',
            auths: [
              'permission:btn:add',
              'permission:btn:edit',
              'permission:btn:delete'
            ]
          }
        },
        {
          path: '/permission/button/login',
          name: 'PermissionButtonLogin',
          component: () => import('@/views/permission/button/perms.vue'),
          meta: {
            title: '登录接口返回按钮权限'
          }
        }
      ]
    }
  ]
} satisfies RouteConfigsTable
