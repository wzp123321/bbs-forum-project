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
  /** 1正常 0已删 2审核中 3审核不通过 */
  status?: number;
  isTop?: number;
  isEssence?: number;
  topTime?: string;
  createTime?: string;
  updateTime?: string;
  tagIds?: number[];
  tagNames?: string[];
}

/** 帖子新增/编辑入参 */
export interface PostSaveParams {
  title: string;
  content: string;
  contentType?: number;
  categoryId: number;
  tagIds?: number[];
  status?: number;
  isTop?: number;
  isEssence?: number;
}

/** 帖子分页查询参数 */
export interface PostPageParams {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
  categoryId?: number;
  tagId?: number;
  status?: number;
  userId?: string;
}

/** 置顶/精华/状态切换入参 */
export interface PostFlagParams {
  isTop?: number;
  isEssence?: number;
  status?: number;
}
