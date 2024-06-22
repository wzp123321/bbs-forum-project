// 重置样式
import './style/index.css';
// 注册
import { registerElementPlus } from './register';
// 指令
import { registerInputFilter, dragDirectiveRegister } from './directives';
// 拦截器
import './interceptor';
// 类型
export * from './model';

export { registerElementPlus, registerInputFilter, dragDirectiveRegister };
