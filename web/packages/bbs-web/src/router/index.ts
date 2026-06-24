import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { isLoggedIn } from '@/utils';

/** 不需要登录即可访问的页面 */
const WHITE_LIST = ['/login', '/register', '/404'];

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    meta: {
      title: 'BBS论坛',
    },
    component: () => import('../layouts/index.vue'),
    children: [
      {
        path: '',
        meta: { title: '首页' },
        component: () => import('../pages/home/index.vue'),
      },
      {
        path: 'topics',
        meta: { title: '话题' },
        component: () => import('../pages/topics/index.vue'),
      },
      {
        path: 'tags',
        meta: { title: '标签' },
        component: () => import('../pages/tags/index.vue'),
      },
      {
        path: 'post/publish',
        meta: { title: '发帖', requireAuth: true },
        component: () => import('../views/post-publish/index.vue'),
      },
      {
        path: 'post/:id',
        meta: { title: '帖子详情' },
        component: () => import('../views/post-detail/index.vue'),
      },
      {
        path: 'user/:userId',
        meta: { title: '用户主页' },
        component: () => import('../views/user-profile/index.vue'),
      },
      {
        path: 'search',
        meta: { title: '搜索' },
        component: () => import('../views/search-result/index.vue'),
      },
      {
        path: 'user-center',
        meta: { title: '个人中心', requireAuth: true },
        component: () => import('../views/user-center/index.vue'),
        redirect: '/user-center/overview',
        children: [
          {
            path: 'overview',
            component: () => import('../views/user-center/pages/overview.vue'),
          },
          {
            path: 'posts',
            component: () => import('../views/user-center/pages/posts.vue'),
          },
          {
            path: 'collects',
            component: () => import('../views/user-center/pages/collects.vue'),
          },
          {
            path: 'likes',
            component: () => import('../views/user-center/pages/likes.vue'),
          },
          {
            path: 'followers',
            component: () => import('../views/user-center/pages/followers.vue'),
          },
          {
            path: 'following',
            component: () => import('../views/user-center/pages/following.vue'),
          },
          {
            path: 'edit',
            component: () => import('../views/user-center/pages/edit-profile.vue'),
          },
          {
            path: 'password',
            component: () => import('../views/user-center/pages/change-password.vue'),
          },
        ],
      },
    ],
  },
  {
    path: '/login',
    meta: { title: '登录' },
    component: () => import('../views/login/index.vue'),
  },
  {
    path: '/register',
    meta: { title: '注册' },
    component: () => import('../views/register/index.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    meta: { title: '404' },
    component: () => import('../views/404/index.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, _from, next) => {
  document.title = (to.meta?.title as string) ?? '9527论坛';
  if (WHITE_LIST.includes(to.path)) {
    next();
    return;
  }
  if (to.meta?.requireAuth && !isLoggedIn()) {
    next({ path: '/login', query: { redirect: to.fullPath } });
    return;
  }
  next();
});

export default router;
