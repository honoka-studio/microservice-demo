import userApis from '@/api/user'
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
      let authCode = (await userApis.authorize(loginId)).data.code
      let token = (await userApis.token(authCode))
      let userInfo = (await userApis.self(token['access_token'])).data
      let result = this.getLoginResponse(userInfo, token)
      setToken(result)
      return result
    },
    /** 前端登出 */
    async logout() {
      try {
        await userApis.logout()
        await userApis.revokeRefreshToken()
      } catch(e) {
        //ignore
      }
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
      let token = await userApis.refreshToken(data.refreshToken)
      let result = this.getLoginResponse({}, token)
      setToken(result)
      return result
    },
    getLoginResponse(userInfo: any, token: any): any {
      const result = {
        ...userInfo,
        nickname: userInfo.username,
        permissions: userInfo.authorities,
        accessToken: token['access_token'],
        refreshToken: token['refresh_token']
      }
      let expires = new Date().getTime() + token['expires_in'] * 1000
      result.expires = new Date(expires).toLocaleString()
      return result
    }
  }
})

export function useUserStoreHook() {
  return useUserStore(store)
}
