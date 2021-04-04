<template>
  <q-layout view="hHh LpR lfr">
    <q-header>
      <q-toolbar class="q-px-md bg-header header__toolbar flex justify-between">
        <div class="flex">
          <router-link to="/" style="text-decoration: none; color: inherit">
            <q-icon name="app:bookshelf" size="28px" />
          </router-link>
          <div class="q-ml-sm" style="margin-top: auto; font-weight: 300; font-size: 20px">
            {{
              $router.currentRoute.name === PageName.PersonalArea
                ? 'Личный кабинет'
                : $router.currentRoute.name === PageName.MainPage
                ? 'Главная'
                : $router.currentRoute.name === PageName.Users
                ? 'Пользователи'
                : $router.currentRoute.name === PageName.Subjects
                ? 'Предметные области'
                : 'Проектная деятельность'
            }}
          </div>
        </div>
        <div class="flex cursor-pointer">
          <q-btn-dropdown
            flat
            class="menu-text flex justify-center column"
            icon="app:account"
            :label="UserStore.shortUserInfo.fullName"
          >
            <q-list>
              <q-item v-close-popup clickable @click="$router.push({ name: 'PersonalArea' })">
                <q-item-section>
                  <q-item-label>Личный кабинет</q-item-label>
                </q-item-section>
              </q-item>

              <q-item v-close-popup clickable @click="logOut">
                <q-item-section>
                  <q-item-label>Выйти</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-btn-dropdown>
        </div>
      </q-toolbar>
    </q-header>
    <q-drawer :value="true" content-class="side-menu-container column" :width="250" class="bg-side-menu">
      <side-menu :need-fetch="needFetch" />
    </q-drawer>
    <q-page-container>
      <!--      <router-view @need-fetch="needFetch = !needFetch" />-->
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script lang="ts">
import { defineComponent, ref } from '@vue/composition-api'
import { getCssModule } from 'src/utils/getCssModule'
import SideMenu from 'components/SideMenu.vue'
import PageName from 'src/router/names'
import useRouter from 'src/router/useRouter'
import UserStore from 'src/store/modules/user'

export default defineComponent({
  name: 'MainLayout',
  components: {
    SideMenu,
  },
  setup() {
    // computed(() => context.root.$q.screen.width <= 1700)
    const needFetch = ref(true)
    const router = useRouter()

    return {
      needFetch,
      PageName,
      UserStore,
      logOut() {
        void UserStore.logOut().then(() => router.push({ name: PageName.MainPage }))
      },

      css: getCssModule<{
        header: string
        footer: string
      }>(),
    }
  },
})
</script>

<style lang="scss" module>
.header {
  --q-color-primary: white;
  height: var(--header-height);
}

.footer {
  --q-color-primary: #454545;
  height: var(--footer-height);

  padding: 16px;
}

.dropdown-btn {
  color: white;
  font-weight: normal;
  &:hover {
    color: red;
  }
}
</style>
