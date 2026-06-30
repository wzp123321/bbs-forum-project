import http from '@/service';
import type { CategoryVO } from './index.api';

export * from './index.api';

/** 板块列表(下拉/筛选用) */
export const listCategoriesApi = () => http.post<CategoryVO[]>('/admin/category/list');
