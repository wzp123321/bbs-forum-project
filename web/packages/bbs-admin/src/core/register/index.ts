const files: any = import.meta.glob('../../components/**/*.vue');
import { App } from 'vue';

export const registerGlobalComponents = (app: App<Element>) => {
  if (files && Object.keys(files)) {
    Object.keys(files).forEach((item) => {
      const firstName = item.split('/');
      const componentName = firstName[firstName.length - 1].split('.')[0];
      app.component(componentName, files[item].default);
    });
  }
};
