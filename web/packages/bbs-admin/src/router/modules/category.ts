import { RouteRecordRaw } from 'vue-router';

export const categoryRoutes: RouteRecordRaw[] = [
  {
    path: '/categoryManage',
    name: 'categoryManage',
    meta: {
      title: '板块管理',
      hasIcon: true,
    },
    component: () => import('../../views/category-manage/index.vue'),
  },
];
