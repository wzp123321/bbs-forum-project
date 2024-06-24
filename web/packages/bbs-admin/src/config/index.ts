import { systemRoutes, userRoutes } from '../router/modules';
import { RouteRecordRaw } from 'vue-router';

/**
 * 菜单
 */
export const menuList: RouteRecordRaw[] = [
  {
    path: 'system',
    name: 'systemManage',
    meta: {
      title: '系统管理',
      hasIcon: true,
    },
    children: systemRoutes,
  },
  ...userRoutes,
];
