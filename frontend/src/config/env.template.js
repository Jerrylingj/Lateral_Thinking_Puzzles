// 环境配置文件
// 此文件为配置模板，请复制此文件并重命名为env.js，然后根据实际情况修改配置项
module.exports = {
    // 是否启用云端环境（true=使用云端API，false=使用本地API）
    enableCloud: false,
    
    // 本地环境API地址，默认为http://localhost:3000
    localApiUrl: 'http://localhost:3000',
    
    // 云端环境API地址，请在此处填写您的实际域名
    // 例如: 'https://api.example.com'或'http://xx.xx.xx.xx:3000'
    cloudApiUrl: ''
  } 