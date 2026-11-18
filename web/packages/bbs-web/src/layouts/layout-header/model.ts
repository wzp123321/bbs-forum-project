/**
 * 顶部导航菜单
 *
 * 约定: path 为空表示对应页面尚未提供, 不会渲染到导航上。
 * 新增菜单前必须先有对应路由, 否则就是死链。
 */
export const headerLeftMenuList = [
  {
    title: '首页',
    path: '/',
    icon: '',
    children: [],
  },
  {
    title: '话题',
    path: '/topics',
    icon: '',
    children: [],
  },
  {
    title: '标签',
    path: '/tags',
    icon: '',
    children: [],
  },
];

/** 创作中心入口, 均落到发帖页 */
export const creationEntries = [
  { title: '写文章', path: '/post/publish' },
  { title: '发话题', path: '/post/publish' },
];
