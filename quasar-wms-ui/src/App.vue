<template>
  <div>
    <Layout v-if="route.path !== '/login' && route.path !== '/register'"></Layout>
    <router-view v-else></router-view>
  </div>
</template>

<script setup>
import Layout from '@/components/Layout.vue'
import { useRoute } from 'vue-router'

const route = useRoute()

</script>

<style scoped>
/* Your CSS here */
</style>