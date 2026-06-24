/**
 * 帖子详情
 * - 顶部: 标题 + 作者 + 正文 + 配图
 * - 中部: 点赞/评论/收藏 统计
 * - 评论列表 (支持回复评论)
 * - 底部: 点赞/收藏 + 评论输入
 */
import React, { useEffect, useState, useRef } from 'react';
import { View, Text, Image, Input } from '@tarojs/components';
import Taro, { useRouter } from '@tarojs/taro';
import dayjs from 'dayjs';
import { getPostDetail, getComments, addComment, likePost } from '@/services/api';
import { useUserStore } from '@/store/user';
import type { PostItem, CommentItem } from '@/types/model';
import Empty from '@/components/Empty';
import styles from './index.module.scss';

const PostDetailPage: React.FC = () => {
  const router = useRouter();
  const id = (router.params.id as string) || '';
  const [post, setPost] = useState<PostItem | null>(null);
  const [comments, setComments] = useState<CommentItem[]>([]);
  const [inputText, setInputText] = useState('');
  const [replyTo, setReplyTo] = useState<{ id: string; user: string } | null>(null);
  const inputRef = useRef<any>(null);
  const { user } = useUserStore();

  const loadAll = async () => {
    try {
      const [p, c] = await Promise.all([getPostDetail(id), getComments(id)]);
      setPost(p);
      setComments(c);
      Taro.setNavigationBarTitle({ title: p.title.slice(0, 14) });
    } catch (e) {
      console.error('[PostDetail] 加载失败', e);
      Taro.showToast({ title: '加载失败', icon: 'none' });
    }
  };

  useEffect(() => {
    if (id) loadAll();
  }, [id]);

  const handleLike = async () => {
    if (!post) return;
    if (!user) { Taro.showToast({ title: '请先登录', icon: 'none' }); return; }
    const liked = !post.liked;
    setPost({ ...post, liked, likeCount: post.likeCount + (liked ? 1 : -1) });
    try { await likePost(post.id, liked); } catch (e) { console.error('[PostDetail] 点赞失败', e); }
  };

  const handleReply = (c: CommentItem) => {
    setReplyTo({ id: c.id, user: c.author.nickname });
    inputRef.current?.focus?.();
  };

  const handleSend = async () => {
    if (!inputText.trim()) return;
    if (!user) { Taro.showToast({ title: '请先登录', icon: 'none' }); return; }
    try {
      const c = await addComment({ postId: id, content: inputText, replyTo: replyTo?.id });
      setComments([...comments, c]);
      setInputText('');
      setReplyTo(null);
      Taro.showToast({ title: '发送成功', icon: 'success' });
    } catch (e) {
      console.error('[PostDetail] 发送评论失败', e);
      Taro.showToast({ title: '发送失败', icon: 'none' });
    }
  };

  if (!post) {
    return <View className={styles.page}><Empty text="加载中..." /></View>;
  }

  return (
    <View className={styles.page}>
      <View className={styles.body}>
        <Text className={styles.title}>{post.title}</Text>

        <View className={styles.meta}>
          <Image className={styles.avatar} src={post.author.avatar} mode="aspectFill" />
          <View className={styles.author}>
            <Text className={styles.authorName}>{post.author.nickname}</Text>
            <Text className={styles.authorMeta}>{dayjs(post.createTime).format('YYYY-MM-DD HH:mm')} · {post.viewCount} 阅读</Text>
          </View>
          <View className={styles.followBtn}>+ 关注</View>
        </View>

        <View className={styles.statRow}>
          <View className={styles.statBlock}>
            <Text className={styles.statValue}>{post.likeCount}</Text>
            <Text className={styles.statLabel}>点赞</Text>
          </View>
          <View className={styles.statBlock}>
            <Text className={styles.statValue}>{post.commentCount}</Text>
            <Text className={styles.statLabel}>评论</Text>
          </View>
          <View className={styles.statBlock}>
            <Text className={styles.statValue}>{post.collectCount}</Text>
            <Text className={styles.statLabel}>收藏</Text>
          </View>
        </View>

        <Text className={styles.content}>{post.content}</Text>
        {post.coverImage && <Image className={styles.cover} src={post.coverImage} mode="widthFix" />}

        {post.tags && post.tags.length > 0 && (
          <View className={styles.tags}>
            {post.tags.map((t) => <Text key={t.id} className={styles.tag}>#{t.name}</Text>)}
          </View>
        )}
      </View>

      <View className={styles.commentSection}>
        <Text className={styles.sectionTitle}>评论 ({comments.length})</Text>
        {comments.length === 0 ? (
          <Empty text="还没有评论,快来抢沙发~" icon="💭" />
        ) : (
          comments.map((c) => (
            <View key={c.id} className={styles.commentItem}>
              <Image className={styles.commentAvatar} src={c.author.avatar} mode="aspectFill" />
              <View className={styles.commentBody}>
                <View className={styles.commentLine1}>
                  <Text className={styles.commentAuthor}>{c.author.nickname}</Text>
                  <Text className={styles.commentTime}>{dayjs(c.createTime).format('MM-DD HH:mm')}</Text>
                </View>
                <Text className={styles.commentText}>
                  {c.replyToUser && <Text className={styles.replyTo}>@{c.replyToUser}</Text>}
                  {c.content}
                </Text>
                <View className={styles.commentAction}>
                  <Text className={styles.commentLike}>♡ {c.likeCount}</Text>
                  <Text onClick={() => handleReply(c)}>回复</Text>
                </View>
              </View>
            </View>
          ))
        )}
      </View>

      <View className={styles.bottomBar}>
        <View className={`${styles.actionBtn} ${post.liked ? styles.actionBtnActive : ''}`} onClick={handleLike}>
          <Text>{post.liked ? '❤' : '♡'}</Text>
          <Text>{post.likeCount}</Text>
        </View>
        <View className={styles.commentInput} onClick={() => inputRef.current?.focus?.()}>
          {replyTo ? `回复 ${replyTo.user}...` : '说点什么吧...'}
        </View>
        <Input
          ref={inputRef}
          value={inputText}
          onInput={(e) => setInputText(e.detail.value)}
          placeholder={replyTo ? `回复 ${replyTo.user}` : '说点什么吧...'}
          style={{ display: 'none' }}
        />
        <View className={styles.sendBtn} onClick={handleSend}>发送</View>
      </View>
    </View>
  );
};

export default PostDetailPage;
