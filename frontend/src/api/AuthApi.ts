import createApiClient from 'src/api-client'

const client = createApiClient('auth')

export interface AuthRepositoryFunctions {
  passwordLogin: (username: string, password: string) => Promise<void>
  smsLogin: (phone: string) => Promise<void>
  logOut: () => Promise<void>
  checkPhone: (phone: string) => Promise<boolean>
}

export interface OAuthRepositoryFunctions {
  login: () => Promise<void>
  callback: (data: any) => Promise<void>
}

export type OAuthProvider = 'microsoft'

function oauthFactory(provider: OAuthProvider): OAuthRepositoryFunctions {
  return {
    login: () =>
      client.get(`oauth/${provider}`).then((url) => {
        window.open(url, '_self')
      }),
    callback: (data: any) => client.post(`oauth/${provider}`, data),
  }
}

export default {
  passwordLogin(username: string, password: string) {
    return client.post('login', { username, password })
  },
  logOut() {
    return client.post('logout')
  },
  microsoft: oauthFactory('microsoft'),
}
