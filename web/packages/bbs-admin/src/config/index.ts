import { systemRoutes } from '../router/modules';
import { CommonMenu } from '../service/model';

/**
 * 菜单
 */
export const menuList: CommonMenu[] = [
  {
    path: '',
    name: 'systemManage',
    meta: {
      title: '系统管理',
      icon: '',
    },
    children: systemRoutes,
  },
];
