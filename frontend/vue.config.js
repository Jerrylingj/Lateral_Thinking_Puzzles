const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // 开发服务器配置
  devServer: {
    proxy: {
      // 配置代理
      '/api': {
        target: 'http://localhost:8080', // 后端API地址
        changeOrigin: true,
        pathRewrite: {
          '^/api': '' // 将 /api 替换为空
        }
      }
    }
  }
})
