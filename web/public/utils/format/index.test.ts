import { describe, expect, it } from 'vitest';
import { formatTimeStamp } from './index';

describe('formatTimeStamp', () => {
  it('formats a timestamp with the requested pattern', () => {
    const stamp = new Date(2026, 0, 2, 3, 4, 5).getTime();
    expect(formatTimeStamp(stamp, 'YYYY-MM-DD HH:mm:ss')).toBe('2026-01-02 03:04:05');
  });

  it('returns a formatted string for epoch zero', () => {
    expect(formatTimeStamp(0, 'YYYY')).toHaveLength(4);
  });
});
