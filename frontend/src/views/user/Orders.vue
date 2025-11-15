<template>
  <div class="user-orders">
    <div class="container">
      <el-page-header content="我的订单" />

      <el-empty description="暂未实现，后续接入真实数据" class="mt-20" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserOrders'
}
</script>

<style scoped>
.user-orders { min-height: 100vh; background: #f5f5f5; padding: 20px 0; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.mt-20 { margin-top: 20px; }
</style>
