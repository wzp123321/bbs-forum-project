/**
 * 媒体处理工具
 */

/**
 * 将 HTML 中的 <iframe> / <video> 包裹到 16:9 自适应容器中,
 * 解决富文本中的视频无法根据容器宽度自适应的问题。
 *
 * 只会处理尚未被 .responsive-media 包裹的标签。
 */
export const wrapMedia = (html: string): string => {
  if (!html) return '';
  return html
    .replace(
      /<iframe\b([^>]*?)\/?>(?:<\/iframe>)?/gi,
      (m, attrs: string) => {
        // 跳过已包裹的
        if (/class\s*=\s*["'][^"']*responsive-media/i.test(attrs)) return m;
        // 强制 100% 宽
        const cleaned = attrs
          .replace(/\swidth\s*=\s*["'][^"']*["']/i, '')
          .replace(/\sheight\s*=\s*["'][^"']*["']/i, '');
        return `<div class="responsive-media"><iframe${cleaned} allowfullscreen></iframe></div>`;
      },
    )
    .replace(
      /<video\b([^>]*?)>([\s\S]*?)<\/video>/gi,
      (m, attrs: string, inner: string) => {
        if (/class\s*=\s*["'][^"']*responsive-media/i.test(attrs)) return m;
        const cleaned = attrs
          .replace(/\swidth\s*=\s*["'][^"']*["']/i, '')
          .replace(/\sheight\s*=\s*["'][^"']*["']/i, '');
        return `<div class="responsive-media"><video${cleaned} controls>${inner}</video></div>`;
      },
    );
};

/**
 * 解析常见视频平台的分享链接, 返回可在 <iframe> 中嵌入的 URL
 * 支持: bilibili / youTube / vimeo / 直链 mp4 等
 */
export const parseVideoEmbed = (rawUrl: string): { tag: 'iframe' | 'video'; html: string } | null => {
  const url = (rawUrl || '').trim();
  if (!url) return null;
  // 已经是 <iframe> / <video> HTML
  if (/^<(iframe|video)\b/i.test(url)) {
    return { tag: 'iframe', html: url };
  }
  let u: URL;
  try {
    u = new URL(url);
  } catch {
    return null;
  }
  const host = u.hostname.toLowerCase();
  // bilibili: https://www.bilibili.com/video/BVxxxxx  -> //player.bilibili.com/player.html?bvid=...
  if (host.includes('bilibili.com')) {
    const bvid = u.pathname.match(/\/(BV[\w]+)/i)?.[1];
    if (bvid) {
      return {
        tag: 'iframe',
        html: `<iframe src="//player.bilibili.com/player.html?bvid=${bvid}&autoplay=0" frameborder="0" allow="autoplay; fullscreen"></iframe>`,
      };
    }
  }
  if (host === 'www.bilibili.com' || host === 'bilibili.com') {
    const m = u.pathname.match(/\/video\/(BV[\w]+)/i);
    if (m) {
      return {
        tag: 'iframe',
        html: `<iframe src="//player.bilibili.com/player.html?bvid=${m[1]}&autoplay=0" frameborder="0" allow="autoplay; fullscreen"></iframe>`,
      };
    }
  }
  // youTube
  if (host.includes('youtube.com')) {
    const id = u.searchParams.get('v') || u.pathname.split('/').pop();
    if (id) {
      return {
        tag: 'iframe',
        html: `<iframe src="https://www.youtube.com/embed/${id}" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>`,
      };
    }
  }
  if (host === 'youtu.be') {
    const id = u.pathname.replace(/^\//, '');
    if (id) {
      return {
        tag: 'iframe',
        html: `<iframe src="https://www.youtube.com/embed/${id}" frameborder="0" allowfullscreen></iframe>`,
      };
    }
  }
  // vimeo
  if (host.includes('vimeo.com')) {
    const id = u.pathname.split('/').filter(Boolean)[0];
    if (id) {
      return {
        tag: 'iframe',
        html: `<iframe src="https://player.vimeo.com/video/${id}" frameborder="0" allow="autoplay; fullscreen; picture-in-picture" allowfullscreen></iframe>`,
      };
    }
  }
  // 直链视频
  if (/\.(mp4|webm|ogg|mov)(\?|$)/i.test(u.pathname + u.search)) {
    return { tag: 'video', html: `<video src="${url}" controls></video>` };
  }
  // 兜底: 尝试作为 iframe 加载
  return { tag: 'iframe', html: `<iframe src="${url}" frameborder="0" allowfullscreen></iframe>` };
};
