export default {
  path: '/permission',
  meta: {
    title: '权限管理',
    icon: 'ep:lollipop',
    rank: 10
  },
  children: [
    {
      path: '/permission/page/index',
      name: 'PermissionPage',
      component: (): any => import('@/views/permission/page/index.vue'),
      meta: {
        title: '页面权限',
        roles: ['admin', 'common']
      }
    },
    {
      path: '/permission/button',
      meta: {
        title: '按钮权限',
        roles: ['admin', 'common']
      },
      children: [
        {
          path: '/permission/button/router',
          name: 'PermissionButtonRouter',
          component: (): any => import('@/views/permission/button/index.vue'),
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
          component: (): any => import('@/views/permission/button/perms.vue'),
          meta: {
            title: '登录接口返回按钮权限'
          }
        }
      ]
    }
  ]
} satisfies RouteConfigsTable
