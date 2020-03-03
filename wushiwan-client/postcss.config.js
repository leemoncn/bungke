// module.exports = {
//   plugins: {
//     "autoprefixer": {
//       browsers: ["Android >= 4.0", "iOS >= 7"]
//     },
//     "postcss-pxtorem": {
//       rootValue: 37.5,
//       propList: ["*"]
//     }
//   }
// }

// const AutoPrefixer = require("autoprefixer")
// const pxtorem = require("postcss-pxtorem")
//
// module.exports = ({ file }) => {
//   let remUnit
//   if (file && file.dirname && file.dirname.indexOf("vant") > -1) {
//     console.log("aaafile = ", file.basename, file.dirname)
//     remUnit = 37.5
//   } else {
//     console.log("bbfile = ", file.basename, file.dirname)
//     remUnit = 108
//   }
//   return {
//     plugins: [pxtorem({
//       rootValue: remUnit,
//       propList: ["*"]
//     }), AutoPrefixer({
//       browsers: ["android >= 4.0", "iOS >= 7"]
//     })]
//   }
// }
