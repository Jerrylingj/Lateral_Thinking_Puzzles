const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // 开发服务器配置
  devServer: {
    // 将端口固定为 8081，以避免与常见的 8080 端口冲突
    port: 8081,
    // 移除无效的代理配置，因为我们使用 CORS 进行跨域
  }
})
