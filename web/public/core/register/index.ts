import 'element-plus/dist/index.css';
import { ElButton } from 'element-plus';
import { App } from 'vue';

/**
 * 注册element-plus
 * @param app
 */
export const registerElementPlus = (app: App) => {
	const components = [ElButton];
	components.forEach((component) => app.use(component));
};
