import { CommonMenu } from '@/service/model';
import { dictionaryRoutes, systemRoutes } from '../router/modules';
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
    path: '/homePage',
    name: 'home',
    icon: 'IconHome',
    index: randomNumber16(),
    meta: {
      title: '仪表盘',
    },
  },
  {
    path: '/userManage',
    name: 'userManage',
    icon: 'IconUser',
    index: randomNumber16(),
    meta: {
      title: '用户管理',
    },
  },
  {
    path: 'dictionary',
    name: 'dictionary',
    icon: 'IconDictionary',
    index: randomNumber16(),
    meta: {
      title: '字典管理',
    },
    children: convertMenu(dictionaryRoutes),
  },
  {
    path: 'system',
    name: 'system',
    icon: 'IconSystem',
    index: randomNumber16(),
    meta: {
      title: '系统管理',
    },
    children: convertMenu(systemRoutes),
  },
];
