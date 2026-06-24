/**
 * 用户主页
 */
import React, { useState, useEffect } from 'react';
import { View, Text, Image } from '@tarojs/components';
import Taro, { useRouter } from '@tarojs/taro';
import { getPostList } from '@/services/api';
import type { PostItem } from '@/types/model';
import PostCard from '@/components/PostCard';
import Empty from '@/components/Empty';
import styles from './index.module.scss';

const UserHomePage: React.FC = () => {
  const router = useRouter();
  const userId = (router.params.userId as string) || 'u1';
  const [following, setFollowing] = useState(false);
  const [posts, setPosts] = useState<PostItem[]>([]);

  useEffect(() => {
    (async () => {
      try {
        // 简化: 取该用户的所有 mock 帖子
        const res = await getPostList({ pageSize: 50 });
        const mine = res.list.filter((p) => p.author.id === userId);
        setPosts(mine);
      } catch (e) {
        console.error('[UserHome] 加载失败', e);
      }
    })();
  }, [userId]);

  const user = posts[0]?.author || { id: userId, username: 'demo', nickname: '体验用户', avatar: 'https://picsum.photos/id/64/200/200' };

  return (
    <View className={styles.page}>
      <View className={styles.header}>
        <Image className={styles.avatar} src={user.avatar} mode="aspectFill" />
        <Text className={styles.nickname}>{user.nickname}</Text>
        <Text className={styles.bio}>这个人很懒,什么都没写~</Text>
        <View
          className={`${styles.followBtn} ${following ? styles.following : ''}`}
          onClick={() => {
            setFollowing(!following);
            Taro.showToast({ title: following ? '已取消关注' : '已关注', icon: 'success' });
          }}
        >
          {following ? '已关注' : '+ 关注'}
        </View>
      </View>

      <View className={styles.stats}>
        <View className={styles.statItem}>
          <Text className={styles.statValue}>{posts.length}</Text>
          <Text className={styles.statLabel}>帖子</Text>
        </View>
        <View className={styles.statItem}>
          <Text className={styles.statValue}>23</Text>
          <Text className={styles.statLabel}>关注</Text>
        </View>
        <View className={styles.statItem}>
          <Text className={styles.statValue}>12</Text>
          <Text className={styles.statLabel}>粉丝</Text>
        </View>
        <View className={styles.statItem}>
          <Text className={styles.statValue}>88</Text>
          <Text className={styles.statLabel}>获赞</Text>
        </View>
      </View>

      <View className={styles.list}>
        {posts.length === 0 ? (
          <Empty text="该用户还没有发布帖子" />
        ) : (
          posts.map((p) => <PostCard key={p.id} post={p} showCategory={false} />)
        )}
      </View>
    </View>
  );
};

export default UserHomePage;
