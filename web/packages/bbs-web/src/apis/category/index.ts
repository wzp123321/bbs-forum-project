import http from '@/service';
import type { CategoryVO } from './index.api';

/** 板块列表(下拉/筛选用) */
export const listCategories = () => http.get<CategoryVO[]>('/admin/category/list');
