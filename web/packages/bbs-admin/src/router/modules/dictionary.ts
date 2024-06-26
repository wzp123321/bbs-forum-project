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
<<<<<<< HEAD:web/packages/bbs-admin/src/router/modules/system.ts
    component: () => import('../../pages/system/dictionary-type-manage/dictionary-type-manage.vue'),
=======
    component: () => import('../../pages/dictionary/dictionary-manage/dictionary-manage.vue'),
>>>>>>> 83ae41236b6012e61ef2c8efd6f873f0b705315b:web/packages/bbs-admin/src/router/modules/dictionary.ts
  },
];
