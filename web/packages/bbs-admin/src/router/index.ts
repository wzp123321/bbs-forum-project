import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { dictionaryRoutes, systemRoutes, postsRoutes } from './modules';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'main',
    meta: {
      title: '9527论坛',
    },
    component: () => import('../views/layout/index.vue'),
    children: [
      {
        path: '/homePage',
        name: 'homePage',
        meta: {
          title: '仪表盘',
          hasIcon: true,
        },
        component: () => import('../pages/home-page/home-page.vue'),
      },
      {
        path: '/userManage',
        name: 'userManage',
        meta: {
          title: '用户管理',
          hasIcon: true,
        },
        component: () => import('../pages/user-manage/user-manage.vue'),
      },
      ...dictionaryRoutes,
      ...systemRoutes,
      ...postsRoutes,
    ],
  },
  {
    path: '/login',
    name: 'login',
    meta: {
      title: '登录',
    },
    component: () => import('../views/login/login.vue'),
  },
  {
    path: '/demo',
    name: 'demo',
    meta: {
      title: 'demo',
    },
    component: () => import('../pages/demo/demo.vue'),
  },
  {
    path: '/404',
    name: '404',
    meta: {
      title: '404',
    },
    component: () => import('../views/404/404.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'default',
    component: () => import('../views/404/404.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  document.title = (to.meta?.name as string) ?? '9527论坛';
  next();
});

export default router;
