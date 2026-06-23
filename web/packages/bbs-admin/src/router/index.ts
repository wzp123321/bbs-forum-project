import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { dictionaryRoutes, systemRoutes, postsRoutes, categoryRoutes } from './modules';
import { tokenStore } from '@/utils';

/** 不需要登录即可访问的页面 */
const WHITE_LIST = ['/login', '/404', '/demo'];

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
        component: () => import('../views/home/index.vue'),
      },
      {
        path: '/userManage',
        name: 'userManage',
        meta: {
          title: '用户管理',
          hasIcon: true,
        },
        component: () => import('../views/user-manage/index.vue'),
      },
      ...dictionaryRoutes,
      ...categoryRoutes,
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
    component: () => import('../views/demo/index.vue'),
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
  document.title = (to.meta?.title as string) ?? '9527论坛';
  const token = tokenStore.get();
  if (WHITE_LIST.includes(to.path)) {
    // 已登录访问登录页 → 跳首页
    if (to.path === '/login' && token) {
      next('/');
      return;
    }
    next();
    return;
  }
  if (!token) {
    next({ path: '/login', query: { redirect: to.fullPath } });
    return;
  }
  next();
});

export default router;
