import AuthApi, { OAuthProvider } from 'src/api/AuthApi'
import UserApi from 'src/api/UserApi'
import { Role, UserInfo } from 'src/models/User'
import store from 'src/store'
import checkIfInStore from 'src/store/utils'
import handleError, { handleApiError } from 'src/utils/errorHandler'
import { Action, config, getModule, Module, Mutation, VuexModule } from 'vuex-module-decorators'

config.rawError = true

export interface AuthInfo {
  loggedIn: boolean
}

export interface OAuthCallbackData {
  provider: OAuthProvider
  payload: Record<string, unknown>
}

@Module({
  name: 'user',
  store,
  dynamic: true,
  namespaced: true,
  preserveState: checkIfInStore('user'),
})
class UserModule extends VuexModule implements AuthInfo {
  loggedIn = false
  info: UserInfo | null = null

  get name() {
    return this.info?.shortName
  }

  get id() {
    return this.info?.id
  }

  get shortUserInfo() {
    return { id: this.info?.id, fullName: this.info?.fullName }
  }

  get isTeacher() {
    return this.info?.role === Role.Teacher
  }
  get isExpert() {
    return this.info?.role === Role.Expert
  }
  get isAdmin() {
    return this.info?.role === Role.Admin
  }
  get isStudent() {
    return this.info?.role === Role.Student
  }

  @Action({ rawError: true })
  async passwordLogIn({ username, password }: { username: string; password: string }) {
    try {
      await AuthApi.passwordLogin(username, password)
      // await this.context.dispatch('fetchUserInfo')
      await this.fetchUserInfo()
      this.LOG_IN()
    } catch (e) {
      this.LOG_OUT()
      throw e
    }
  }

  @Action
  async oauthLogIn(provider: OAuthProvider) {
    try {
      await AuthApi[provider].login()
    } catch (e) {
      handleApiError(e)
    }
  }

  @Action
  async handleOAuthCallback(data: OAuthCallbackData) {
    try {
      await AuthApi[data.provider].callback({
        code: data.payload.code,
        state: data.payload.state,
      })
      // await this.context.dispatch('fetchUserInfo')
      await this.fetchUserInfo()
      this.LOG_IN()
    } catch (e) {
      this.LOG_OUT()
      throw e
    }
  }

  @Action
  async logOut(): Promise<void> {
    try {
      if (this.loggedIn) {
        this.LOG_OUT()
        await AuthApi.logOut()
      }
    } catch (e) {
      handleError(e)
    }
  }

  @Action
  async fetchUserInfo() {
    const info = await UserApi.info()
    this.SET_INFO(info)
  }

  @Mutation
  LOG_IN() {
    this.loggedIn = true
  }

  @Mutation
  LOG_OUT() {
    this.loggedIn = false
    this.info = null
  }

  @Mutation
  SET_INFO(info: UserInfo | null) {
    this.info = info
  }
}

const UserStore = getModule(UserModule)
export default UserStore
