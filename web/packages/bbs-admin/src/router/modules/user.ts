import { RouteRecordRaw } from 'vue-router';

export const userRoutes: RouteRecordRaw[] = [
  {
    path: '/userManage',
    name: 'userManage',
    meta: {
      title: '用户管理',
      hasIcon: true,
    },
    component: () => import('../../pages/user/user-manage/user-manage.vue'),
  },
];
