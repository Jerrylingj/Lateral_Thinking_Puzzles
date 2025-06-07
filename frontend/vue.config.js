const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // 开发服务器配置
  devServer: {
    port: 8080,
    // 配置代理，解决跨域问题
    proxy: {
      '/api': {
        target: 'http://localhost:3000', // 目标后端服务器地址
        changeOrigin: true, // 支持跨域
        ws: true, // 支持 websocket
      }
    }
  }
})
