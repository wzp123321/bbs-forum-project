import { ElMessage } from 'element-plus';

/** 统一 message 提示 */
export const message = {
  success: (text: string) => ElMessage.success(text),
  error: (text: string) => ElMessage.error(text),
  warning: (text: string) => ElMessage.warning(text),
  info: (text: string) => ElMessage.info(text),
};
