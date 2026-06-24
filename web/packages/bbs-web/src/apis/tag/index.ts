import http from '@/service';
import type { TagVO } from './index.api';

export * from './index.api';

/** 标签列表 */
export const listTags = () => http.get<TagVO[]>('/admin/tag/list');

/** 命名空间导出 */
export const tagApi = {
  list: listTags,
};

export default tagApi;
