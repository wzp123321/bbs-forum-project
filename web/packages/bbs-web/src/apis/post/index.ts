import http from '@/service';
import type { PostVO, PostPageParams, PostSaveParams, PageData } from './index.api';

export * from './index.api';

/** 帖子分页 */
export const pagePosts = (params: PostPageParams) =>
  http.get<PageData<PostVO>>('/admin/post/page', { params: params as Record<string, unknown> });

/** 帖子详情 */
export const getPost = (id: number) => http.get<PostVO>(`/admin/post/${id}`);

/** 发帖 */
export const createPost = (params: PostSaveParams) =>
  http.post<number>('/admin/post', params);

/** 编辑 */
export const updatePost = (id: number, params: PostSaveParams) =>
  http.put<void>(`/admin/post/${id}`, params);

/** 删除 */
export const deletePost = (id: number) => http.delete<void>(`/admin/post/${id}`);

/** 命名空间导出 */
export const postApi = {
  page: pagePosts,
  detail: getPost,
  create: createPost,
  update: updatePost,
  delete: deletePost,
};

export default postApi;
