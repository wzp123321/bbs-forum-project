/** 帖子 VO */
export interface PostVO {
  id: number;
  userId?: string;
  userName?: string;
  /** 作者昵称 (为空时后端已回退为 userName) */
  nickname?: string;
  /** 作者头像 URL */
  avatar?: string;
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

/** 阅读权限标签颜色 */
export const readPermTagType = (readPerm: number): string => {
  const map: Record<number, string> = { 2: 'info', 3: 'warning', 4: 'danger' };
  return map[readPerm] || 'info';
};

/** 阅读权限文本 */
export const readPermLabel = (readPerm: number): string => {
  return READ_PERM_LABELS[readPerm] || '';
};

/** 帖子列表排序: latest 最新 / hot 热门 / essence 精华 */
export type PostSort = 'latest' | 'hot' | 'essence';

/** 帖子分页查询参数 */
export interface PostPageParams {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
  categoryId?: number;
  tagId?: number;
  status?: number;
  userId?: string;
  /** 排序, 不传等价于 latest */
  sort?: PostSort;
}

/** 帖子新增/编辑入参 */
export interface PostSaveParams {
  title: string;
  content: string;
  contentType?: number;
  categoryId: number;
  tagIds?: number[];
}

/** 草稿保存入参: 标题与正文可只填一项 */
export interface DraftSaveParams {
  id?: number;
  title?: string;
  content?: string;
  contentType?: number;
  categoryId?: number;
  tagIds?: number[];
}

/** 草稿发布结果 */
export interface PublishDraftResult {
  /** 发布后的状态: 1 正常 / 2 审核中 */
  status: number;
  needAudit: boolean;
}
