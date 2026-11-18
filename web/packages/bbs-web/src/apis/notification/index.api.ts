/** 通知类型: 1赞帖子 2评论帖子 3回复评论 4关注 5系统 */
export type NotificationType = 1 | 2 | 3 | 4 | 5;

/** 通知 VO */
export interface NotificationVO {
  id: number;
  type: NotificationType;
  /** 类型文案: 点赞 / 评论 / 回复 / 关注 / 公告 */
  typeText: string;
  actorId?: string;
  /** 触发者昵称 */
  actorName?: string;
  actorAvatar?: string;
  postId?: number;
  commentId?: number;
  postTitle?: string;
  content?: string;
  isRead: number;
  createTime?: string;
}

/** 通知分页参数 */
export interface NotificationPageParams {
  pageNum?: number;
  pageSize?: number;
  /** 只看未读 */
  onlyUnread?: boolean;
}
