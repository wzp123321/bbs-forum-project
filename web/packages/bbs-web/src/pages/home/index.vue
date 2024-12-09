<template>
    <div class="home">
        {{ route.meta.title }}
    </div>
</template>

<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router';
defineOptions({
    name: 'Home',
});

const route = useRoute();
const router = useRouter();
</script>

<style lang="less" scoped>
.home {}
</style>
