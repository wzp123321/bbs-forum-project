/**
 * 帖子 Mock 数据
 * 图片使用 picsum.photos, 不依赖本地资源
 */
import type { PostItem, UserInfo } from '@/types/model';

const users: UserInfo[] = [
  { id: 'u1', username: 'tech_master', nickname: '科技达人', avatar: 'https://picsum.photos/id/64/200/200', bio: '热爱技术与分享' },
  { id: 'u2', username: 'foodie', nickname: '美食家小王', avatar: 'https://picsum.photos/id/177/200/200', bio: '走遍大街小巷寻找美食' },
  { id: 'u3', username: 'coder_zhang', nickname: '码农小张', avatar: 'https://picsum.photos/id/338/200/200', bio: '全栈工程师 / Vue & Java' },
  { id: 'u4', username: 'travel_li', nickname: '旅行家李', avatar: 'https://picsum.photos/id/1027/200/200', bio: '在路上' },
  { id: 'u5', username: 'designer', nickname: '设计师阿月', avatar: 'https://picsum.photos/id/91/200/200', bio: 'UI / 插画 / 平面' },
];

export const mockPosts: PostItem[] = [
  {
    id: 'p1', title: '2026 年最值得关注的前端技术趋势',
    content: '本文盘点 2026 年值得关注的几大前端技术趋势: Signals、Server Components、Edge Rendering……',
    author: users[0], category: { id: 'c1', name: '科技' }, tags: [{ id: 't1', name: '前端' }, { id: 't2', name: '趋势' }],
    likeCount: 328, commentCount: 56, viewCount: 4521, collectCount: 124, liked: false, collected: false,
    coverImage: 'https://picsum.photos/id/1/750/500', createTime: '2026-06-22 14:32', top: true, essence: true,
  },
  {
    id: 'p2', title: '广州 24 小时美食地图',
    content: '早茶 → 牛杂 → 糖水 → 宵夜,广州一天 24 小时吃喝不重样……',
    author: users[1], category: { id: 'c2', name: '美食' }, tags: [{ id: 't3', name: '广州' }, { id: 't4', name: '探店' }],
    likeCount: 215, commentCount: 42, viewCount: 3144, collectCount: 89,
    coverImage: 'https://picsum.photos/id/292/750/500', createTime: '2026-06-22 10:15',
  },
  {
    id: 'p3', title: 'Spring Boot 3.4 性能调优实战',
    content: 'JVM 参数 / HikariCP 连接池 / 慢 SQL 排查,生产环境调优经验分享……',
    author: users[2], category: { id: 'c1', name: '科技' }, tags: [{ id: 't5', name: 'Java' }, { id: 't6', name: '性能' }],
    likeCount: 187, commentCount: 31, viewCount: 2890, collectCount: 76, liked: true,
    coverImage: 'https://picsum.photos/id/2/750/500', createTime: '2026-06-21 18:42',
  },
  {
    id: 'p4', title: '西藏自驾 15 天:从拉萨到阿里',
    content: '详细路线、住宿、装备清单、高反应对……这篇攻略帮到你……',
    author: users[3], category: { id: 'c3', name: '旅行' }, tags: [{ id: 't7', name: '西藏' }, { id: 't8', name: '自驾' }],
    likeCount: 542, commentCount: 128, viewCount: 8976, collectCount: 312, essence: true,
    coverImage: 'https://picsum.photos/id/1015/750/500', createTime: '2026-06-20 09:08',
  },
  {
    id: 'p5', title: 'Figma 插件开发入门指南',
    content: '从 0 到 1 开发一个 Figma 插件,完整流程 + 代码示例……',
    author: users[4], category: { id: 'c1', name: '科技' }, tags: [{ id: 't9', name: 'Figma' }, { id: 't10', name: '设计' }],
    likeCount: 96, commentCount: 14, viewCount: 1245, collectCount: 38,
    coverImage: 'https://picsum.photos/id/3/750/500', createTime: '2026-06-19 16:24',
  },
  {
    id: 'p6', title: 'Vue3 + Taro 跨端开发最佳实践',
    content: '我们在生产项目中使用 Taro4 + Vue3 + NutUI 的踩坑总结……',
    author: users[2], category: { id: 'c1', name: '科技' }, tags: [{ id: 't1', name: '前端' }, { id: 't11', name: 'Taro' }],
    likeCount: 145, commentCount: 22, viewCount: 1987, collectCount: 56,
    createTime: '2026-06-19 11:30',
  },
  {
    id: 'p7', title: '成都火锅地图,本地人私藏清单',
    content: '不踩雷的 8 家店,人均 50 ~ 200,附地址和营业时间……',
    author: users[1], category: { id: 'c2', name: '美食' }, tags: [{ id: 't12', name: '成都' }, { id: 't13', name: '火锅' }],
    likeCount: 268, commentCount: 67, viewCount: 3567, collectCount: 142,
    coverImage: 'https://picsum.photos/id/312/750/500', createTime: '2026-06-18 19:50',
  },
  {
    id: 'p8', title: '我是如何在 3 个月内减肥 20 斤的',
    content: '饮食 + 运动 + 作息,科学减肥不反弹……附上详细计划表……',
    author: users[3], category: { id: 'c4', name: '生活' }, tags: [{ id: 't14', name: '健身' }, { id: 't15', name: '减肥' }],
    likeCount: 432, commentCount: 89, viewCount: 6721, collectCount: 234,
    coverImage: 'https://picsum.photos/id/326/750/500', createTime: '2026-06-18 14:20',
  },
  {
    id: 'p9', title: 'Mac 必装软件清单 2026 版',
    content: '开发 / 效率 / 设计各类软件推荐,持续更新中……',
    author: users[0], category: { id: 'c1', name: '科技' }, tags: [{ id: 't16', name: 'Mac' }],
    likeCount: 156, commentCount: 43, viewCount: 2456, collectCount: 98,
    coverImage: 'https://picsum.photos/id/6/750/500', createTime: '2026-06-17 22:10',
  },
  {
    id: 'p10', title: '日落氛围感照片怎么拍?',
    content: '构图 / 时间 / 后期,小红书爆款黄昏照教程……',
    author: users[4], category: { id: 'c3', name: '旅行' }, tags: [{ id: 't17', name: '摄影' }, { id: 't18', name: '后期' }],
    likeCount: 89, commentCount: 19, viewCount: 1342, collectCount: 45,
    coverImage: 'https://picsum.photos/id/1018/750/500', createTime: '2026-06-17 17:45',
  },
];
