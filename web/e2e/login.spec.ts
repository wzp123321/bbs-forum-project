import { expect, test } from '@playwright/test';

test('登录页可渲染且包含账号表单', async ({ page }) => {
  await page.goto('/login');
  await expect(page.getByRole('heading', { name: '账号登录' })).toBeVisible();
  await expect(page.locator('input[type="text"]').first()).toBeVisible();
  await expect(page.getByRole('button', { name: /登\s*录/ })).toBeVisible();
});
