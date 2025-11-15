<template>
  <div class="user-center">
    <div class="container">
      <el-page-header content="个人中心" />

      <div class="grid">
        <el-card class="card" @click="$router.push('/profile')">
          <div class="card-title">个人资料</div>
          <div class="card-sub">查看与编辑基本信息</div>
        </el-card>

        <el-card class="card" @click="$router.push('/orders')">
          <div class="card-title">我的订单</div>
          <div class="card-sub">查看历史订单与状态</div>
        </el-card>

        <el-card class="card" @click="$router.push('/products')">
          <div class="card-title">我发布的</div>
          <div class="card-sub">管理已发布的商品</div>
        </el-card>
      </div>

      <el-card class="welcome">
        <div class="welcome-title">欢迎回来</div>
        <div class="welcome-sub">在这里管理你的交易与资料</div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserCenter'
}
</script>

<style scoped>
.user-center { min-height: 100vh; background: #f5f5f5; padding: 20px 0; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 16px; margin: 16px 0 24px; }
.card { cursor: pointer; transition: transform .2s ease, box-shadow .2s ease; }
.card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,0.08); }
.card-title { font-size: 16px; font-weight: 600; color: #1f2d3d; }
.card-sub { margin-top: 6px; color: #606266; font-size: 13px; }
.welcome { border-radius: 12px; }
.welcome-title { font-size: 18px; font-weight: 600; color: #1f2d3d; }
.welcome-sub { margin-top: 6px; color: #606266; }
</style>
