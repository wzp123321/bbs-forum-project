import { defineStore } from 'pinia';
import { ref } from 'vue';

const useDemoStore = defineStore('demo', () => {
  const counter = ref(0);

  const increment = () => {
    counter.value++;
  };

  return {
    counter,
    increment,
  };
});

export default useDemoStore;
