import { ref } from 'vue';

export const useDialogOpen = () => {
  // 开关
  const visible = ref(false);
  /**
   * 打开
   */
  const handleOpen = () => {
    console.log(112313231);
    visible.value = true;
  };
  /**
   * 关闭
   */
  const handleClose = () => {
    visible.value = false;
  };

  return {
    visible,
    handleClose,
    handleOpen,
  };
};
