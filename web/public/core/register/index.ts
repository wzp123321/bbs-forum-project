import 'element-plus/dist/index.css';
import { ElButton, ElInput, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus';
import { App } from 'vue';

/**
 * 注册element-plus
 * @param app
 */
export const registerElementPlus = (app: App) => {
  const components = [ElButton, ElInput, ElDropdown, ElDropdownMenu, ElDropdownItem];
  components.forEach((component) => app.use(component));
};
