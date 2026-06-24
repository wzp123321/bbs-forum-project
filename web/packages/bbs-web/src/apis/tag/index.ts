import http from '@/service';
import type { TagVO } from './index.api';

/** 标签列表 */
export const listTags = () => http.get<TagVO[]>('/admin/tag/list');
