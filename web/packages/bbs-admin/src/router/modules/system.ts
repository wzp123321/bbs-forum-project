import { RouteRecordRaw } from 'vue-router';

export const systemRoutes: RouteRecordRaw[] = [
  {
    path: '/dictionaryManage',
    name: 'dictionaryManage',
    meta: {
      title: '字典管理',
    },
    component: () => import('../../pages/system/dictionary-manage/dictionary-manage.vue'),
  },
  {
    path: '/dictionaryTypeManage',
    name: 'dictionaryTypeManage',
    meta: {
      title: '字典类型管理',
    },
    component: () => import('../../pages/system/dictionary-manage/dictionary-manage.vue'),
  },
];
