module.exports = {
  root: true,
  env: {
    node: true
  },
  "extends": [
    "plugin:vue/essential",
    "@vue/standard"
  ],
  rules: {
    "no-console": process.env.NODE_ENV === "production" ? "error" : "off",
    "no-debugger": process.env.NODE_ENV === "production" ? "error" : "off",
    "quotes": [1, "double"],
    "indent": [0, 2],//缩进风格
  },
  parserOptions: {
    parser: "babel-eslint"
  },
  globals: {
    plus: true
  },
}
