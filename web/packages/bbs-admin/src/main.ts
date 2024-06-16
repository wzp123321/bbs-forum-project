import { createApp } from 'vue';
import App from './app.vue';
// router
import router from './router';
// 自定义指令
import { registerInputFilter } from '@bbs/core';
// 注册element-plus
import { registerElementPlus } from '../../../public/core';
// pinia
import pinia from './pinia';

const app = createApp(App);
registerInputFilter(app);
registerElementPlus(app);
app.use(pinia);
app.use(router);
app.mount('#app');
