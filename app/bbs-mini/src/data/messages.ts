import type { CommentItem, MessageItem, UserInfo } from '@/types/model';

const u: UserInfo = { id: 'u1', username: 'tech_master', nickname: '科技达人', avatar: 'https://picsum.photos/id/64/200/200' };

export const mockComments: CommentItem[] = [
  { id: 'cm1', postId: 'p1', author: u, content: '总结得很到位,期待 Server Components 的落地。', likeCount: 12, createTime: '2026-06-22 15:10' },
  { id: 'cm2', postId: 'p1', author: { id: 'u3', username: 'coder_zhang', nickname: '码农小张', avatar: 'https://picsum.photos/id/338/200/200' }, content: 'Signals 确实香,Vue 也在推。', likeCount: 8, createTime: '2026-06-22 15:42' },
  { id: 'cm3', postId: 'p1', author: { id: 'u5', username: 'designer', nickname: '设计师阿月', avatar: 'https://picsum.photos/id/91/200/200' }, content: '前端设计师也想转,可以先从哪个方向入手?', likeCount: 5, replyTo: 'cm1', replyToUser: '科技达人', createTime: '2026-06-22 16:08' },
  { id: 'cm4', postId: 'p2', author: { id: 'u4', username: 'travel_li', nickname: '旅行家李', avatar: 'https://picsum.photos/id/1027/200/200' }, content: '收藏了,下个月去广州一定挨个打卡!', likeCount: 6, createTime: '2026-06-22 11:30' },
  { id: 'cm5', postId: 'p2', author: { id: 'u2', username: 'foodie', nickname: '美食家小王', avatar: 'https://picsum.photos/id/177/200/200' }, content: '感谢支持!有新的店我也会继续更新~', likeCount: 3, replyTo: 'cm4', replyToUser: '旅行家李', createTime: '2026-06-22 12:00' },
];

export const mockMessages: MessageItem[] = [
  { id: 'm1', type: 'like', fromUser: u, targetTitle: '2026 年最值得关注的前端技术趋势', content: '点赞了你的帖子', createTime: '2026-06-22 16:30', read: false },
  { id: 'm2', type: 'comment', fromUser: { id: 'u3', username: 'coder_zhang', nickname: '码农小张', avatar: 'https://picsum.photos/id/338/200/200' }, targetTitle: 'Mac 必装软件清单 2026 版', content: '评论: 感谢分享,已收藏!', createTime: '2026-06-22 15:20', read: false },
  { id: 'm3', type: 'follow', fromUser: { id: 'u5', username: 'designer', nickname: '设计师阿月', avatar: 'https://picsum.photos/id/91/200/200' }, content: '关注了你', createTime: '2026-06-22 14:00', read: true },
  { id: 'm4', type: 'system', content: '你的帖子已被设为精华帖', targetTitle: '2026 年最值得关注的前端技术趋势', createTime: '2026-06-22 10:00', read: true },
  { id: 'm5', type: 'comment', fromUser: { id: 'u4', username: 'travel_li', nickname: '旅行家李', avatar: 'https://picsum.photos/id/1027/200/200' }, targetTitle: '广州 24 小时美食地图', content: '评论: 这家牛杂真的绝!', createTime: '2026-06-21 20:30', read: true },
];
