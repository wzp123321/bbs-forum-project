import { CommonMenu } from '@/service/model';
import { systemRoutes, userRoutes } from '../router/modules';
import { RouteRecordRaw } from 'vue-router';
import { randomNumber16 } from 'zp-common-utils';

/**
 * 处理菜单
 * @param list
 * @returns
 */
const convertMenu = (list: RouteRecordRaw[]): CommonMenu[] => {
  return (
    list?.map((item) => ({
      path: item?.path ?? '',
      name: String(item?.name) ?? '',
      hasIcon: true,
      index: randomNumber16(),
      meta: item?.meta as any,
      children: convertMenu(item?.children ?? []),
    })) ?? []
  );
};

/**
 * 菜单
 */
export const menuList: CommonMenu[] = [
  {
    path: 'system',
    name: 'systemManage',
    hasIcon: true,
    index: randomNumber16(),
    meta: {
      title: '系统管理',
    },
    children: convertMenu(systemRoutes),
  },
  ...convertMenu(userRoutes),
];
