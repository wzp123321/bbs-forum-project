import http from '@/service';
import type {
  PostVO,
  PostPageParams,
  PostSaveParams,
  DraftSaveParams,
  PublishDraftResult,
} from './index.api';
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

/** 关注动态流 */
export const followFeedApi = (params: { pageNum?: number; pageSize?: number }) =>
  http.post<PageResult<PostVO>>('/admin/post/followFeed', params);

/** 保存草稿 */
export const saveDraftApi = (params: DraftSaveParams) => http.post<number>('/admin/post/saveDraft', params);

/** 我的草稿 */
export const myDraftsApi = (params: { pageNum?: number; pageSize?: number }) =>
  http.post<PageResult<PostVO>>('/admin/post/myDrafts', params);

/** 草稿详情 */
export const getDraftApi = (id: number) => http.post<PostVO>('/admin/post/draftDetail', { id });

/** 发布草稿 */
export const publishDraftApi = (id: number) =>
  http.post<PublishDraftResult>(`/admin/post/${id}/publishDraft`);
