import { RouteRecordRaw } from 'vue-router';

export const postsRoutes: RouteRecordRaw[] = [
  {
    path: '/postsManage',
    name: 'postsManage',
    meta: {
      title: '帖子管理',
    },
    component: () => import('../../pages/posts-manage/posts-manage.vue'),
  }
];
