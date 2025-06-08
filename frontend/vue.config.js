const { defineConfig } = require('@vue/cli-service')
const envConfig = require('./src/config/env')

module.exports = defineConfig({
  transpileDependencies: true,
  // 开发服务器配置
  devServer: {
    port: 8080,
    // 配置代理，只在本地环境下使用
    proxy: !envConfig.enableCloud ? {
      '/api': {
        target: envConfig.localApiUrl,
        changeOrigin: true,
        ws: true,
      }
    } : undefined
  }
})