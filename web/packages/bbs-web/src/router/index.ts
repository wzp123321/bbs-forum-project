import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    meta: {
      title: 'BBS论坛',
    },
    component: () => import('../layouts/index.vue'),
    children: [
      {
        path: '/',
        meta: {
          title: '首页',
        },
        component: () => import('../pages/home/index.vue'),
      },
      {
        path: '/topics',
        meta: {
          title: '话题',
        },
        component: () => import('../pages/topics/index.vue'),
      },
      {
        path: '/tags',
        meta: {
          title: '话题',
        },
        component: () => import('../pages/tags/index.vue'),
      },
    ],
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
