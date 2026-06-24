import { RouteRecordRaw } from 'vue-router';

export const commentRoutes: RouteRecordRaw[] = [
  {
    path: '/commentManage',
    name: 'commentManage',
    meta: {
      title: '评论管理',
      hasIcon: true,
    },
    component: () => import('../../views/comment-manage/index.vue'),
  },
];
