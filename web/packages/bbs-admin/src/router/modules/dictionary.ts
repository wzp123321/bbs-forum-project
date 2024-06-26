import { RouteRecordRaw } from 'vue-router';

export const dictionaryRoutes: RouteRecordRaw[] = [
  {
    path: '/dictionaryManage',
    name: 'dictionaryManage',
    meta: {
      title: '字典管理',
    },
    component: () => import('../../pages/dictionary/dictionary-manage/dictionary-manage.vue'),
  },
  {
    path: '/dictionaryTypeManage',
    name: 'dictionaryTypeManage',
    meta: {
      title: '字典类型管理',
    },
    component: () => import('../../pages/dictionary/dictionary-type-manage/dictionary-type-manage.vue'),
  },
];
