import { RouteRecordRaw } from 'vue-router';

export const dictionaryRoutes: RouteRecordRaw[] = [
  {
    path: '/dictionaryManage',
    name: 'dictionaryManage',
    meta: {
      title: '字典管理',
    },
    component: () => import('../../views/dictionary/dictionary-manage/index.vue'),
  },
  {
    path: '/dictionaryTypeManage',
    name: 'dictionaryTypeManage',
    meta: {
      title: '字典类型管理',
    },
    component: () => import('../../views/dictionary/dictionary-type-manage/index.vue'),
  },
];
