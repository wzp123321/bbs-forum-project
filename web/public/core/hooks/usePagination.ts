import { ref } from 'vue';
import { COMMON_PAGE_SIZES as pageSizes } from '../model';

export const usePagination = () => {
  // 页码
  const pageNum = ref(1);
  // 条数
  const pageSize = ref(pageSizes?.[0]);
  // 总数
  const total = ref(0);
  /**
   * 设置页码
   * @param {number} value 页码
   */
  const setPageNum = (value: number) => {
    pageNum.value = value;
  };
  /**
   * 设置条数
   * @param {number} value 条数
   */
  const setPageSize = (value: number) => {
    pageNum.value = 1;
    pageSize.value = value;
  };

  return {
    pageNum,
    pageSize,
    total,
    setPageNum,
    setPageSize,
  };
};
