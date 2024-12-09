<template>
    <div class="topics">
        {{ route.meta.title }}
    </div>
</template>

<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router';
defineOptions({
    name: 'Tags',
});

const route = useRoute();
const router = useRouter();
</script>

<style lang="less" scoped>
.tags {
    width: 100%;
    height: 100%;
}
</style>
