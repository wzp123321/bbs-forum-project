/**
 * PostCard 帖子卡片
 * - 列表/首页/话题/搜索通用
 * - 极简布局: 头像 + 昵称 + 标题 + 摘要 + 配图(可选) + 底部统计
 */
import React from 'react';
import { View, Text, Image } from '@tarojs/components';
import Taro from '@tarojs/taro';
import classnames from 'classnames';
import dayjs from 'dayjs';
import type { PostItem } from '@/types/model';
import styles from './index.module.scss';

interface Props {
  post: PostItem;
  showCategory?: boolean;
  onClick?: (p: PostItem) => void;
}

const PostCard: React.FC<Props> = ({ post, showCategory = true, onClick }) => {
  const handleClick = () => {
    onClick?.(post);
    Taro.navigateTo({ url: `/pages/post-detail/index?id=${post.id}` });
  };

  return (
    <View className={styles.card} onClick={handleClick} hoverClass={styles.cardActive} hoverStayTime={50}>
      <View className={styles.header}>
        <Image className={styles.avatar} src={post.author.avatar} mode="aspectFill" />
        <View className={styles.headerInfo}>
          <View className={styles.headerLine}>
            <Text className={styles.nickname}>{post.author.nickname}</Text>
            {showCategory && post.category && (
              <Text className={styles.categoryTag}>{post.category.name}</Text>
            )}
            {post.essence && <Text className={styles.essenceTag}>精</Text>}
          </View>
          <Text className={styles.time}>{dayjs(post.createTime).format('MM-DD HH:mm')}</Text>
        </View>
      </View>

      <View className={styles.body}>
        <Text className={styles.title}>{post.title}</Text>
        <Text className={styles.summary}>{post.content}</Text>
        {post.coverImage && (
          <View className={styles.coverWrap}>
            <Image className={styles.cover} src={post.coverImage} mode="aspectFill" />
          </View>
        )}
        {post.tags && post.tags.length > 0 && (
          <View className={styles.tags}>
            {post.tags.map((t) => (
              <Text key={t.id} className={styles.tagItem}>#{t.name}</Text>
            ))}
          </View>
        )}
      </View>

      <View className={styles.footer}>
        <Text className={styles.stat}>👁 {post.viewCount}</Text>
        <Text className={classnames(styles.stat, post.liked && styles.statLiked)}>
          {post.liked ? '❤' : '♡'} {post.likeCount}
        </Text>
        <Text className={styles.stat}>💬 {post.commentCount}</Text>
        <Text className={styles.stat}>⭐ {post.collectCount}</Text>
      </View>
    </View>
  );
};

export default PostCard;
