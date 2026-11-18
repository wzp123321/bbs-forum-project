export const normalizeList = (value) => {
  if (Array.isArray(value)) return value.map((v) => String(v).trim().toLowerCase()).filter(Boolean);
  if (typeof value === 'string') {
    return value.split(/[,，、|]/).map((v) => v.trim().toLowerCase()).filter(Boolean);
  }
  return [];
};

export const exactSetMatch = (output, expected) => {
  const a = [...new Set(normalizeList(output))].sort();
  const b = [...new Set(normalizeList(expected))].sort();
  return a.length === b.length && a.every((v, i) => v === b[i]);
};

export const containsAll = (output, expected) => {
  const actual = new Set(normalizeList(output));
  const wanted = normalizeList(expected);
  return wanted.length > 0 && wanted.every((v) => actual.has(v));
};

export const recallAtK = (output, expected, k = 3) => {
  const actual = normalizeList(output).slice(0, k);
  const wanted = normalizeList(expected);
  if (wanted.length === 0) return 0;
  const hit = wanted.filter((v) => actual.includes(v)).length;
  return hit / wanted.length;
};

export const levenshtein = (left, right) => {
  const a = String(left ?? '');
  const b = String(right ?? '');
  const dp = Array.from({ length: a.length + 1 }, (_, i) => [i, ...Array(b.length).fill(0)]);
  for (let j = 0; j <= b.length; j++) dp[0][j] = j;
  for (let i = 1; i <= a.length; i++) {
    for (let j = 1; j <= b.length; j++) {
      dp[i][j] = Math.min(
        dp[i - 1][j] + 1,
        dp[i][j - 1] + 1,
        dp[i - 1][j - 1] + (a[i - 1] === b[j - 1] ? 0 : 1),
      );
    }
  }
  return dp[a.length][b.length];
};

export const similarity = (left, right) => {
  const max = Math.max(String(left ?? '').length, String(right ?? '').length);
  return max === 0 ? 1 : 1 - levenshtein(left, right) / max;
};
