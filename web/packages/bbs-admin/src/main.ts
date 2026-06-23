import { createApp } from 'vue';
import App from './app.vue';
// router
import router from './router';
// 样式
import './assets/css/common.less';
// 自定义指令
import { registerInputFilter } from '@bbs/core';
// 注册element-plus
import { registerElementPlus } from '../../../public/core';
// pinia
import pinia from './pinia';
// 加载 axios 实例 + 拦截器
import './utils/request';

const app = createApp(App);
registerInputFilter(app);
registerElementPlus(app);
app.use(pinia);
app.use(router);
app.mount('#app');
