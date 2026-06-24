import http from '@/utils/request';
import type { PostVO, PostSaveParams, PostPageParams, PostFlagParams } from './index.api';
import type { R } from '../types';

interface PostPageData {
  list: PostVO[];
  total: number;
  pageNum: number;
  pageSize: number;
}

/** 分页查询 */
export const pagePosts = (params: PostPageParams) =>
  http.get<R<PostPageData>>('/admin/post/page', { params }).then((res) => res.data.data);

/** 详情 */
export const getPost = (id: number) =>
  http.get<R<PostVO>>(`/admin/post/${id}`).then((res) => res.data.data);

/** 新增 */
export const createPost = (params: PostSaveParams) =>
  http.post<R<number>>('/admin/post', params).then((res) => res.data.data);

/** 编辑 */
export const updatePost = (id: number, params: PostSaveParams) =>
  http.put<R<void>>(`/admin/post/${id}`, params).then((res) => res.data);

/** 删除 */
export const deletePost = (id: number) =>
  http.delete<R<void>>(`/admin/post/${id}`).then((res) => res.data);

/** 切换置顶 */
export const toggleTop = (id: number, params: PostFlagParams) =>
  http.put<R<void>>(`/admin/post/${id}/top`, params).then((res) => res.data);
