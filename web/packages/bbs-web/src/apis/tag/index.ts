import http from '@/service';
import type { TagVO } from './index.api';

export * from './index.api';

/** 标签列表 */
export const listTagsApi = () => http.post<TagVO[]>('/admin/tag/list');
