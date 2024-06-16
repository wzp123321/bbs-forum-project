import { App } from 'vue';

const dragDirectiveRegister = (app: App) => {
  app.directive('elDrag', {
    beforeMount(el, binding, vnode) {
      console.log(el, binding, vnode);
    },
  });
};

export default dragDirectiveRegister;
