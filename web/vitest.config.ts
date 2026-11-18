import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    environment: 'node',
    include: ['packages/**/*.test.ts', 'public/**/*.test.ts'],
    exclude: ['**/node_modules/**', '**/dist/**'],
    clearMocks: true,
    coverage: {
      clean: false,
      reporter: ['text', 'lcov'],
      include: ['public/utils/**/*.ts', 'packages/*/src/utils/**/*.ts'],
    },
  },
});
