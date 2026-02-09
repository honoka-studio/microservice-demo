import type { FunctionalComponent } from 'vue'

export const routerArrays: Array<RouteConfig> = import.meta.env.VITE_HIDE_HOME === 'false' ? [
  {
    path: '/home',
    name: 'Home',
    meta: {
      title: '首页',
      icon: 'ep/home-filled'
    }
  }
] : []

export type RouteMeta = {
  title?: string
  icon?: string | FunctionalComponent
  showLink?: boolean
  savedPosition?: boolean
  auths?: Array<string>
}

export type RouteConfig = {
  path?: string
  query?: object
  params?: object
  meta?: RouteMeta
  children?: RouteConfig[]
  name?: string
}

export type TagsView = {
  icon: string | FunctionalComponent
  text: string
  divided: boolean
  disabled: boolean
  show: boolean
}

export interface LayoutSet {
  sidebar: {
    opened: boolean
    withoutAnimation: boolean
    isClickCollapse: boolean
  }

  device: string

  fixedHeader: boolean

  classes: {
    hideSidebar: boolean
    openSidebar: boolean
    withoutAnimation: boolean
    mobile: boolean
  }

  hideTabs: boolean
}

export type Menu = {
  id?: number
  name?: string
  path?: string
  noShowingChildren?: boolean
  children?: Menu[]
  value: unknown
  meta?: {
    icon?: string
    title?: string
    rank?: number
    showParent?: boolean
    extraIcon?: string
  }
  showTooltip?: boolean
  parentId?: number
  pathList?: number[]
  redirect?: string
}

export type ThemeColor = {
  color: string
  themeColor: string
}
