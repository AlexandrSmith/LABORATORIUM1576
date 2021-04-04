import PageName from 'src/router/names'
import { parsePropsToInt, RouteConfigExt } from './utils'

const routes: RouteConfigExt[] = [
  {
    path: '/login',
    component: () => import('layouts/LoginLayout.vue'),
    children: [
      {
        path: '',
        name: PageName.Main,
        component: () => import('pages/Index.vue'),
      },
      {
        path: '/auth/:provider',
        component: () => import('pages/OAuthHandler.vue'),
        props: true,
      },
    ],
  },
  {
    path: '/',
    name: PageName.MainPage,
    component: () => import('pages/Main.vue'),
  },
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '/lk',
        name: PageName.PersonalArea,
        component: () => import('pages/PersonalArea.vue'),
      },
      {
        path: '/projects/:projectId',
        name: PageName.Projects,
        component: () => import('pages/Projects.vue'),
        props: parsePropsToInt(['projectId']),
      },
      {
        path: '/subjects',
        name: PageName.Subjects,
        component: () => import('pages/Subjects.vue'),
      },
      {
        path: '/users',
        name: PageName.Users,
        component: () => import('pages/Users.vue'),
      },
      {
        path: '/news',
        name: PageName.News,
        component: () => import('pages/News.vue'),
      },
      {
        path: '/admin-projects',
        name: PageName.AdminProjects,
        component: () => import('pages/AdminProjects.vue'),
      },
      {
        path: '/404',
        name: PageName.NotFound,
        component: () => import('pages/Error404.vue'),
      },
      {
        path: '*',
        component: () => import('pages/Error404.vue'),
      },
    ],
    meta: {
      auth: true,
    },
  },
]

export default routes
