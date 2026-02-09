import type { RouteRecordName } from 'vue-router'

export type Cache = {
  mode: string
  name?: RouteRecordName
}

export type Position = {
  startIndex?: number
  length?: number
}

export type App = {
  sidebar: {
    opened: boolean
    withoutAnimation: boolean
    // 判断是否手动点击Collapse
    isClickCollapse: boolean
  }
  layout: string
  device: string
  viewportSize: {
    width: number
    height: number
  }
}

export type Multi = {
  path: string
  name: string
  meta: any
  query?: object
  params?: object
}

export type StoreSet = {
  title: string
  fixedHeader: boolean
  hiddenSideBar: boolean
}

export type User = {
  avatar?: string
  username?: string
  nickname?: string
  roles?: Array<string>
  permissions?: Array<string>
  isRemembered?: boolean
  loginDay?: number
}
