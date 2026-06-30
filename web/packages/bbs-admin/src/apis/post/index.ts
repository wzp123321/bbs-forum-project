import http from '@/utils/request';
import type { PostVO, PostSaveParams, PostPageParams, PostFlagParams } from './index.api';
import type { IResponseVO } from '../types';

export * from './index.api';

interface PostPageData {
  list: PostVO[];
  total: number;
  pageNum: number;
  pageSize: number;
}

/** 分页查询 */
export const pagePostsApi = (params: PostPageParams) =>
  http.post<IResponseVO<PostPageData>>('/admin/post/page', params).then((res) => res.data.data);

/** 详情 */
export const getPostApi = (id: number) => http.post<IResponseVO<PostVO>>('/admin/post/detail', { id }).then((res) => res.data.data);

/** 新增 */
export const createPostApi = (params: PostSaveParams) =>
  http.post<IResponseVO<number>>('/admin/post/create', params).then((res) => res.data.data);

/** 编辑 */
export const updatePostApi = (id: number, params: PostSaveParams) =>
  http.post<IResponseVO<void>>(`/admin/post/${id}/update`, params).then((res) => res.data);

/** 删除 */
export const deletePostApi = (id: number) => http.post<IResponseVO<void>>('/admin/post/delete', { id }).then((res) => res.data);

/** 切换置顶 */
export const toggleTopApi = (id: number, params: PostFlagParams) =>
  http.post<IResponseVO<void>>(`/admin/post/${id}/toggleTop`, params).then((res) => res.data);

/** 切换精华 */
export const toggleEssenceApi = (id: number, params: PostFlagParams) =>
  http.post<IResponseVO<void>>(`/admin/post/${id}/toggleEssence`, params).then((res) => res.data);
