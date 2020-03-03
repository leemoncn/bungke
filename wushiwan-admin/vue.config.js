// vue.config.js

const path = require("path")

function resolve (dir) {
  return path.join(__dirname, "./", dir)
}

module.exports = {
  devServer: {
    port: 9999,
    disableHostCheck: true
  },
  chainWebpack: config => {
    const svgRule = config.module.rule("svg")
    svgRule.uses.clear()
    svgRule.test(/\.svg$/)
      .include.add(resolve("src/icon"))
      .end()
      .use("svg-sprite-loader")
      .loader("svg-sprite-loader")
      .options({
        symbolId: "icon-[name]"
      })
  }
}
