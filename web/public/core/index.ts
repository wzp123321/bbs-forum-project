// 重置样式
import './style/index.css';
// 注册
import { registerElementPlus } from './register';
// 指令
import { registerInputFilter, dragDirectiveRegister } from './directives';
// 拦截器
import './interceptor';

export { registerElementPlus, registerInputFilter, dragDirectiveRegister };
