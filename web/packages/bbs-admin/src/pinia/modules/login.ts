import { defineStore } from 'pinia';
import { ref } from 'vue';

const useLoginStore = defineStore('login', () => {
  const isTokenFailureFlag = ref(false);

  const setIsTokenFailureFlag = (flag: boolean) => {
    isTokenFailureFlag.value = flag;
  };

  return {
    isTokenFailureFlag,
    setIsTokenFailureFlag,
  };
});

export default useLoginStore;
