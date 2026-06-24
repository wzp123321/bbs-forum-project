/**
 * 话题/分类 下的帖子列表
 */
import React, { useEffect, useState } from 'react';
import { View } from '@tarojs/components';
import Taro, { useRouter } from '@tarojs/taro';
import { getPostList } from '@/services/api';
import type { PostItem } from '@/types/model';
import PostCard from '@/components/PostCard';
import Empty from '@/components/Empty';
import styles from './index.module.scss';

const TopicListPage: React.FC = () => {
  const router = useRouter();
  const categoryId = (router.params.categoryId as string) || '';
  const tagId = (router.params.tagId as string) || '';
  const name = (router.params.name as string) || '话题';

  const [list, setList] = useState<PostItem[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    Taro.setNavigationBarTitle({ title: name });
    (async () => {
      setLoading(true);
      try {
        const res = await getPostList({ categoryId, pageSize: 20 });
        let items = res.list;
        if (tagId) items = items.filter((p) => p.tags?.some((t) => t.id === tagId));
        setList(items);
      } catch (e) {
        console.error('[TopicList] 加载失败', e);
        Taro.showToast({ title: '加载失败', icon: 'none' });
      } finally {
        setLoading(false);
      }
    })();
  }, [categoryId, tagId, name]);

  return (
    <View className={styles.page}>
      {list.length === 0 && !loading ? (
        <Empty text="该话题下还没有帖子" />
      ) : (
        <View className={styles.list}>
          {list.map((p) => <PostCard key={p.id} post={p} showCategory={false} />)}
        </View>
      )}
    </View>
  );
};

export default TopicListPage;
