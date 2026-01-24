import userApis, { type RefreshTokenResult } from '@/api/user'
import { type DataInfo, removeToken, setToken, userKey } from '@/utils/auth'
import { defineStore } from 'pinia'
import { resetRouter, router, routerArrays, storageLocal, store, type userType } from '../utils'
import { useMultiTagsStoreHook } from './multiTags'

export const useUserStore = defineStore('pure-user', {
  state: (): userType => ({
    // 头像
    avatar: storageLocal().getItem<DataInfo<number>>(userKey)?.avatar ?? '',
    // 用户名
    username: storageLocal().getItem<DataInfo<number>>(userKey)?.username ?? '',
    // 昵称
    nickname: storageLocal().getItem<DataInfo<number>>(userKey)?.nickname ?? '',
    // 页面级别权限
    roles: storageLocal().getItem<DataInfo<number>>(userKey)?.roles ?? [],
    // 按钮级别权限
    permissions: storageLocal().getItem<DataInfo<number>>(userKey)?.permissions ?? [],
    // 是否勾选了登录页的免登录
    isRemembered: false,
    // 登录页的免登录存储几天，默认7天
    loginDay: 7
  }),
  actions: {
    /** 存储头像 */
    SET_AVATAR(avatar: string) {
      this.avatar = avatar
    },
    /** 存储用户名 */
    SET_USERNAME(username: string) {
      this.username = username
    },
    /** 存储昵称 */
    SET_NICKNAME(nickname: string) {
      this.nickname = nickname
    },
    /** 存储角色 */
    SET_ROLES(roles: Array<string>) {
      this.roles = roles
    },
    /** 存储按钮级别权限 */
    SET_PERMS(permissions: Array<string>) {
      this.permissions = permissions
    },
    /** 存储是否勾选了登录页的免登录 */
    SET_ISREMEMBERED(bool: boolean) {
      this.isRemembered = bool
    },
    /** 设置登录页的免登录存储几天 */
    SET_LOGINDAY(value: number) {
      this.loginDay = Number(value)
    },
    /** 登入 */
    async loginByUsername(data: any) {
      let loginId = (await userApis.login(data)).data.loginId
      console.log(loginId)
      userApis.authorize(loginId).catch(error => {
        console.log(error)
      })
      throw new Error('1')
      //return res
    },
    /** 前端登出（不调用接口） */
    async logOut() {
      this.username = ''
      this.roles = []
      this.permissions = []
      removeToken()
      useMultiTagsStoreHook().handleTags('equal', [...routerArrays])
      resetRouter()
      await router.push('/login')
    },
    /** 刷新`token` */
    async handRefreshToken(data: any) {
      return new Promise<RefreshTokenResult>((resolve, reject) => {
        userApis.refreshToken(data)
          .then(data => {
            if(data) {
              setToken(data.data)
              resolve(data)
            }
          })
          .catch(error => {
            reject(error)
          })
      })
    }
  }
})

export function useUserStoreHook() {
  return useUserStore(store)
}
