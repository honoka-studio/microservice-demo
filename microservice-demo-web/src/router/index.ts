import routeApis from '@/api/route'
import { getConfig } from '@/config'
import { basicRoutes, indexRoute, pathMatchRoute } from '@/router/default-routes'
import {
  ascending,
  findRouteByPath,
  formatFlatteningRoutes,
  formatTwoStageRoutes,
  getHistoryMode,
  getTopMenu,
  handleAliveRoute,
  handleAsyncRoutes,
  isOneOfArray
} from '@/router/utils'
import { useMultiTagsStoreHook } from '@/store/modules/multiTags'
import { usePermissionStoreHook } from '@/store/modules/permission'
import { getToken, multipleTabsKey, removeToken } from '@/utils/auth'
import NProgress from '@/utils/progress'
import { buildHierarchyTree } from '@/utils/tree'
import { cloneDeep, isAllEmpty, isUrl, openLink, storageLocal } from '@pureadmin/utils'
import Cookies from 'js-cookie'
import { createRouter, type Router, type RouteRecordRaw } from 'vue-router'

/**
 * 自动导入全部静态路由，无需再手动引入！匹配 src/router/modules 目录（任何嵌套级别）中
 * 具有 .ts 扩展名的所有文件
 *
 * 如何匹配所有文件请看：https://github.com/mrmlnc/fast-glob#basic-syntax
 * 如何排除文件请看：https://cn.vitejs.dev/guide/features.html#negative-patterns
 */
const modules: Record<string, any> = import.meta.glob('./modules/**/*.ts', { eager: true })

/** 原始静态路由（未做任何处理） */
const routes = [indexRoute]

Object.keys(modules).forEach(key => {
  routes.push(modules[key].default)
})

/** 导出处理后的静态路由（三级及以上的路由全部拍成二级） */
const constantRoutes: Array<RouteRecordRaw> = formatTwoStageRoutes(
  formatFlatteningRoutes(buildHierarchyTree(ascending(routes.flat(Infinity))))
)

/** 用于渲染菜单，保持原始层级 */
export const constantMenus: Array<RouteConfigsTable> =
  ascending(routes.flat(Infinity)).concat(...basicRoutes)

/** 不参与菜单的路由 */
export const remainingPaths = Object.keys(basicRoutes).map(v => basicRoutes[v].path)

const defaultRoutes = [indexRoute, ...basicRoutes, pathMatchRoute]

/** 创建路由实例 */
export const router: Router = createRouter({
  history: getHistoryMode(import.meta.env.VITE_ROUTER_HISTORY),
  routes: cloneDeep(defaultRoutes),
  strict: true,
  scrollBehavior(_, from, savedPosition) {
    return new Promise(resolve => {
      if(savedPosition) {
        return savedPosition
      } else {
        if(from.meta.saveSrollTop) {
          const top: number = document.documentElement.scrollTop || document.body.scrollTop
          resolve({ left: 0, top })
        }
      }
    })
  }
})

/** 记录已经加载的页面路径 */
const loadedPaths = new Set<string>()

/**
 * 初始化路由（`new Promise` 写法防止在异步请求中造成无限循环）
 */
export async function initRouter() {
  let optionRoutes = router.options.routes as RouteRecordRaw[]
  for(let route of cloneDeep(constantRoutes)) {
    router.addRoute(route)
    if(optionRoutes.findIndex(r => r.path === route.path) > -1) {
      if(route.path !== '/') continue
      optionRoutes[0] = route
    } else {
      optionRoutes.push(route)
    }
  }
  if(getConfig()?.CachingAsyncRoutes) {
    // 开启动态路由缓存本地localStorage
    const key = 'async-routes'
    const asyncRouteList = storageLocal().getItem(key) as any
    if(asyncRouteList && asyncRouteList?.length > 0) {
      handleAsyncRoutes(asyncRouteList)
    } else {
      let routes = (await routeApis.asyncRoutes())?.data ?? []
      handleAsyncRoutes(cloneDeep(routes))
      storageLocal().setItem(key, routes)
    }
  } else {
    let routes = (await routeApis.asyncRoutes())?.data ?? []
    handleAsyncRoutes(cloneDeep(routes))
  }
  return router
}

/** 重置已加载页面记录 */
export function resetLoadedPaths() {
  loadedPaths.clear()
}

/** 重置路由 */
export function resetRouter() {
  router.clearRoutes()
  router.options.routes = cloneDeep(defaultRoutes)
  for(const route of router.options.routes) {
    router.addRoute(route as any)
  }
  usePermissionStoreHook().clearAllCachePage()
  resetLoadedPaths()
}

router.beforeEach(async (to: ToRouteType, from, next) => {
  to.meta.loaded = loadedPaths.has(to.path)

  if(!to.meta.loaded) {
    NProgress.start()
  }

  if(to.meta?.keepAlive) {
    handleAliveRoute(to, 'add')
    // 页面整体刷新和点击标签页刷新
    if(from.name === undefined || from.name === 'Redirect') {
      handleAliveRoute(to)
    }
  }
  const userInfo = getToken()
  const externalLink = isUrl(to?.name as string)
  if(!externalLink) {
    to.matched.some(item => {
      if(!item.meta.title) return ''
      const Title = getConfig().Title
      if(Title) document.title = `${item.meta.title} - ${Title}`
      else document.title = item.meta.title as string
    })
  }

  if(Cookies.get(multipleTabsKey) && userInfo) {
    if(to.path === '/login') {
      next({ path: '/' })
      return
    }
    // 无权限跳转403页面
    if(to.meta?.roles && !isOneOfArray(to.meta?.roles, userInfo?.roles)) {
      next({ path: '/error/403' })
    }
    // 开启隐藏首页后在浏览器地址栏手动输入首页home路由则跳转到404页面
    if(import.meta.env.VITE_HIDE_HOME === 'true' && to.fullPath === '/home') {
      next({ path: '/error/404' })
    }
    if(from?.name) {
      // name为超链接
      if(externalLink) {
        openLink(to?.name as string)
        NProgress.done()
      } else {
        next()
      }
    } else {
      // 刷新
      if(usePermissionStoreHook().wholeMenus.length === 0 && to.path !== '/login') {
        await initRouter()
        if(!useMultiTagsStoreHook().getMultiTagsCache) {
          const { path } = to
          const route = findRouteByPath(path, router.getRoutes())
          getTopMenu(true)
          // query、params模式路由传参数的标签页不在此处处理
          if(route && route.meta?.title) {
            if(isAllEmpty(route.parentId) && route.meta?.backstage) {
              // 此处为动态顶级路由（目录）
              const { path, name, meta } = route.children[0]
              useMultiTagsStoreHook().handleTags('push', { path, name, meta })
            } else {
              const { path, name, meta } = route
              useMultiTagsStoreHook().handleTags('push', { path, name, meta })
            }
          }
        }
        await router.push(to.fullPath)
      }
      next()
    }
  } else {
    if(to.path !== '/login') {
      removeToken()
      next({ path: '/login' })
    } else {
      next()
    }
  }
})

router.afterEach(to => {
  loadedPaths.add(to.path)
  NProgress.done()
})

export default router
