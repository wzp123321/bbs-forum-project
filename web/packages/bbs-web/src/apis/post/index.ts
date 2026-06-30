import http from '@/service';
import type { PostVO, PostPageParams, PostSaveParams } from './index.api';
import { PageResult } from '../types';

export * from './index.api';

/** 帖子分页 */
export const pagePostsApi = (params: PostPageParams) => http.post<PageResult<PostVO>>('/admin/post/page', params);

/** 帖子详情 */
export const getPostApi = (id: number) => http.post<PostVO>('/admin/post/detail', { id });

/** 发帖 */
export const createPostApi = (params: PostSaveParams) => http.post<number>('/admin/post/create', params);

/** 编辑 */
export const updatePostApi = (id: number, params: PostSaveParams) =>
  http.post<void>(`/admin/post/${id}/update`, params);

/** 删除 */
export const deletePostApi = (id: number) => http.post<void>('/admin/post/delete', { id });
