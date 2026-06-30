import { ref } from 'vue';

export const useDialogOpen = () => {
  // 开关
  const visible = ref(false);
  /**
   * 打开
   */
  const openDialog = () => {
    visible.value = true;
  };
  /**
   * 关闭
   */
  const closeDialog = () => {
    visible.value = false;
  };

  return {
    visible,
    closeDialog,
    openDialog,
  };
};
