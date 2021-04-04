import { vuexKey } from 'src/store/utils'
import Vue from 'vue'
import Vuex from 'vuex'
import VuexPersistence from 'vuex-persist'

const vuexLocal = new VuexPersistence({
  key: vuexKey,
  storage: window.localStorage,
  modules: ['user', 'chat', 'notification', 'menu'],
})

Vue.use(Vuex)

export default new Vuex.Store({
  strict: !!process.env.DEV,
  plugins: [vuexLocal.plugin],
})
