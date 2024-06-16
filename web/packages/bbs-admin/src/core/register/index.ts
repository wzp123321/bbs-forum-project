const files: any = import.meta.glob('./**/*.vue');
import { App } from 'vue';

const install = (app: App<Element>) => {
  if (files && Object.keys(files)) {
    Object.keys(files).forEach((item) => {
      const firstName = item.split('/');
      const componentName = firstName[firstName.length - 1].split('.')[0];
      app.component(componentName, files[item].default);
    });
  }
};

export default install;
