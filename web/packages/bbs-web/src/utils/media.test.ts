import { describe, expect, it } from 'vitest';
import { wrapMedia, parseVideoEmbed } from './media';

describe('wrapMedia', () => {
  it('wraps iframe and strips fixed width/height', () => {
    const html = '<iframe src="https://example.com" width="640" height="360"></iframe>';
    const result = wrapMedia(html);
    expect(result).toContain('class="responsive-media"');
    expect(result).not.toContain('width="640"');
    expect(result).not.toContain('height="360"');
  });

  it('does not double-wrap existing containers', () => {
    const once = wrapMedia('<video src="a.mp4"></video>');
    const twice = wrapMedia(once);
    expect(twice).toBe(once);
  });
});

describe('parseVideoEmbed', () => {
  it('returns null for empty input', () => {
    expect(parseVideoEmbed('  ')).toBeNull();
  });

  it('converts bilibili share link to player iframe', () => {
    const result = parseVideoEmbed('https://www.bilibili.com/video/BV1xx411c7mD');
    expect(result?.tag).toBe('iframe');
    expect(result?.html).toContain('player.bilibili.com');
    expect(result?.html).toContain('BV1xx411c7mD');
  });

  it('converts direct mp4 link to video tag', () => {
    const result = parseVideoEmbed('https://cdn.example.com/a.mp4?v=1');
    expect(result?.tag).toBe('video');
    expect(result?.html).toContain('<video');
  });
});
