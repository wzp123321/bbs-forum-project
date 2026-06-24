export default defineAppConfig({
  pages: [
    'pages/home/index',
    'pages/topic/index',
    'pages/message/index',
    'pages/mine/index',
    'pages/post-detail/index',
    'pages/post-publish/index',
    'pages/login/index',
    'pages/user-home/index',
    'pages/search/index',
    'pages/topic-list/index',
  ],
  window: {
    backgroundTextStyle: 'light',
    navigationBarBackgroundColor: '#fff',
    navigationBarTitleText: 'BBS 论坛',
    navigationBarTextStyle: 'black',
    backgroundColor: '#f5f6f7',
  },
  tabBar: {
    color: '#86909c',
    selectedColor: '#165dff',
    backgroundColor: '#ffffff',
    borderStyle: 'white',
    list: [
      { pagePath: 'pages/home/index', text: '首页' },
      { pagePath: 'pages/topic/index', text: '话题' },
      { pagePath: 'pages/message/index', text: '消息' },
      { pagePath: 'pages/mine/index', text: '我的' },
    ],
  },
});
