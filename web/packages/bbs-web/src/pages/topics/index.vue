<template>
    <div class="topics">
        {{ route.meta.title }}
    </div>
</template>

<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router';
defineOptions({
    name: 'Topics',
});

const route = useRoute();
const router = useRouter();
</script>

<style lang="less" scoped>
.topics {
    width: 100%;
    height: 100%;
}
</style>
