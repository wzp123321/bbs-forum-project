import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/demo',
  },
  {
    path: '/demo',
    meta: {
      title: 'demo',
    },
    component: () => import('../pages/demo/demo.vue'),
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
