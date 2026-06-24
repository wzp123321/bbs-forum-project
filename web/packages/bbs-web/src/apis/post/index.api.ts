/** 帖子 VO */
export interface PostVO {
  id: number;
  userId?: string;
  userName?: string;
  categoryId?: number;
  categoryName?: string;
  title?: string;
  content?: string;
  contentType?: number;
  viewCount?: number;
  likeCount?: number;
  commentCount?: number;
  collectCount?: number;
  isTop?: number;
  isEssence?: number;
  createTime?: string;
  updateTime?: string;
  tagIds?: number[];
  tagNames?: string[];
}

/** 帖子分页查询参数 */
export interface PostPageParams {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
  categoryId?: number;
  tagId?: number;
  status?: number;
}

/** 帖子新增/编辑入参 */
export interface PostSaveParams {
  title: string;
  content: string;
  contentType?: number;
  categoryId: number;
  tagIds?: number[];
}

/** 分页响应 */
export interface PageData<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
}
