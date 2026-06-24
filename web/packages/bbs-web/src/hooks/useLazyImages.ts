import { watch, type Ref } from 'vue';

/**
 * 在指定根容器内对所有 <img> 应用懒加载
 * - v-html 渲染富文本时, 把 <img src="X"> 改为 data-lazy-src 模式
 * - 通过 IntersectionObserver 进入视口才真正设置 src
 * - 离开容器时停止观察
 */
export function useLazyImages(rootRef: Ref<HTMLElement | undefined>) {
  let observer: IntersectionObserver | null = null;
  const observed = new WeakSet<HTMLImageElement>();

  const attach = () => {
    const root = rootRef.value;
    if (!root) return;
    if (!observer) {
      observer = new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            if (!entry.isIntersecting) return;
            const el = entry.target as HTMLImageElement;
            const src = el.getAttribute('data-lazy-src');
            if (src && el.src !== src) {
              el.src = src;
            }
            el.removeAttribute('data-lazy-src');
            observer?.unobserve(el);
          });
        },
        { rootMargin: '200px 0px' },
      );
    }
    const imgs = root.querySelectorAll<HTMLImageElement>('img');
    imgs.forEach((img) => {
      if (observed.has(img)) return;
      observed.add(img);
      const src = img.getAttribute('src');
      if (!src) return;
      if (img.complete && img.naturalWidth > 0) return;
      img.setAttribute('data-lazy-src', src);
      img.removeAttribute('src');
      observer!.observe(img);
    });
  };

  watch(
    () => rootRef.value,
    () => attach(),
    { flush: 'post' },
  );
}
