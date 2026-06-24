import type { App, Directive, DirectiveBinding } from 'vue';

/**
 * v-lazy 图片懒加载指令
 * - 用法: <img v-lazy="url" />
 * - 滚到视口附近才设置 src, 没进入视口时显示占位/空
 * - 已被加载过的 src 自动跳过
 */
interface LazyBinding extends DirectiveBinding<string | undefined> {}

const OBSERVER: WeakMap<HTMLElement, IntersectionObserver> = new WeakMap();
const LOADED = new WeakSet<HTMLElement>();

function applySrc(el: HTMLImageElement, src: string) {
  if (!src) return;
  if (el.src === src) return;
  LOADED.add(el);
  el.src = src;
  el.removeAttribute('data-lazy-pending');
}

function ensureObserver(): IntersectionObserver {
  if ((ensureObserver as any)._inst) {
    return (ensureObserver as any)._inst;
  }
  const obs = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        const el = entry.target as HTMLImageElement;
        const src = el.getAttribute('data-lazy-src');
        if (src) applySrc(el, src);
        obs.unobserve(el);
      });
    },
    { rootMargin: '200px 0px' },
  );
  (ensureObserver as any)._inst = obs;
  return obs;
}

const lazy: Directive<HTMLImageElement, string | undefined> = {
  mounted(el, binding: LazyBinding) {
    const src = binding.value;
    if (!src) return;
    el.setAttribute('data-lazy-src', src);
    el.removeAttribute('src');
    el.setAttribute('data-lazy-pending', '1');
    el.style.background = '#f5f7fa';
    const obs = ensureObserver();
    obs.observe(el);
    OBSERVER.set(el, obs);
  },
  updated(el, binding: LazyBinding) {
    if (LOADED.has(el) && el.getAttribute('data-lazy-src') === binding.value) return;
    const src = binding.value;
    if (!src) return;
    el.setAttribute('data-lazy-src', src);
    el.removeAttribute('src');
    el.setAttribute('data-lazy-pending', '1');
    const obs = ensureObserver();
    obs.observe(el);
  },
  unmounted(el) {
    const obs = OBSERVER.get(el);
    if (obs) obs.unobserve(el);
    OBSERVER.delete(el);
    LOADED.delete(el);
  },
};

export default function lazyDirectiveRegister(app: App) {
  app.directive('lazy', lazy);
}
