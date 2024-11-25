import { RouteRecordRaw } from 'vue-router';

export const systemRoutes: RouteRecordRaw[] = [
  {
    path: '/feedbackManage',
    name: 'feedbackManage',
    meta: {
      title: '反馈管理',
      hasIcon: true,
    },
    component: () => import('../../pages/system-manage/feedback-manage/feedback-manage.vue'),
  },
  {
    path: '/tagManage',
    name: 'tagManage',
    meta: {
      title: 'Tag管理',
      hasIcon: true,
    },
    component: () => import('../../pages/system-manage/tag-manage/tag-manage.vue'),
  },
];
