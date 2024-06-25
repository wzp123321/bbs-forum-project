import 'element-plus/dist/index.css';
import {
  ElConfigProvider,
  ElButton,
  ElInput,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElPagination,
  ElTable,
  ElTableColumn,
  ElDrawer,
} from 'element-plus';
import { App } from 'vue';

/**
 * 注册element-plus
 * @param app
 */
export const registerElementPlus = (app: App) => {
  const components = [
    ElConfigProvider,
    ElButton,
    ElInput,
    ElDropdown,
    ElDropdownMenu,
    ElDropdownItem,
    ElPagination,
    ElTable,
    ElTableColumn,
    ElDrawer,
  ];
  components.forEach((component) => app.use(component));
};
