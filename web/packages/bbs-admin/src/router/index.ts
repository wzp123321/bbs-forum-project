import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { systemRoutes } from './modules';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'main',
    meta: {
      title: '9527论坛',
    },
    component: () => import('../views/layout/index.vue'),
    children: [...systemRoutes],
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
