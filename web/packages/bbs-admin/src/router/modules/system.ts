import { RouteRecordRaw } from 'vue-router';

export const systemRoutes: RouteRecordRaw[] = [
  {
    path: '/feedbackManage',
    name: 'feedbackManage',
    meta: {
      title: '反馈管理',
      hasIcon: true,
    },
    component: import('../../views/system-manage/feedback-manage/index.vue'),
  },
  {
    path: '/tagManage',
    name: 'tagManage',
    meta: {
      title: 'Tag管理',
      hasIcon: true,
    },
    component: import('../../views/tag-manage/index.vue'),
  },
];
