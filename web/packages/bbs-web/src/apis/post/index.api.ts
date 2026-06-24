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
  /** 阅读权限: 1公开 2登录可见 3粉丝可见 4仅作者 */
  readPerm?: number;
  createTime?: string;
  updateTime?: string;
  tagIds?: number[];
  tagNames?: string[];
}

/** 阅读权限枚举 */
export const READ_PERM = {
  PUBLIC: 1,
  LOGIN: 2,
  FOLLOWER: 3,
  PRIVATE: 4,
} as const;

/** 阅读权限描述 */
export const READ_PERM_LABELS: Record<number, string> = {
  1: '公开',
  2: '登录可见',
  3: '粉丝可见',
  4: '仅作者',
};

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
