<template>
  <div class="login-container">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" autocomplete="on"
             label-position="left">
      <h3 class="title">bhmanage-ui</h3>
      <el-form-item prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user"/>
        </span>
        <el-input v-model="loginForm.username" name="username" type="text" autocomplete="on" placeholder="账号"/>
      </el-form-item>
      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password"/>
        </span>
        <el-input
          :type="pwdType"
          v-model="loginForm.password"
          name="password"
          autocomplete="on"
          placeholder="密码"
          @keyup.enter.native="handleLogin"/>
        <span class="show-pwd" @click="showPwd">
          <svg-icon icon-class="eye"/>
        </span>
      </el-form-item>
      <el-form-item>
        <el-button :loading="loading" type="primary" style="width:100%;" @click.native.prevent="handleLogin">
          登 录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

  export default {
    name: "Login",
    data () {
      const validateUsername = (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入账号"))
        }
        callback()
      }
      const validatePass = (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入密码"))
        }
        callback()
      }
      return {
        loginForm: {
          username: "",
          password: ""
        },
        loginRules: {
          username: [{ required: true, trigger: "blur", validator: validateUsername }],
          password: [{ required: true, trigger: "blur", validator: validatePass }]
        },
        loading: false,
        pwdType: "password",
        redirect: undefined
      }
    },
    watch: {
      $route: {
        handler: function (route) {
          this.redirect = route.query && route.query.redirect
        },
        immediate: true
      }
    },
    methods: {
      showPwd () {
        if (this.pwdType === "password") {
          this.pwdType = ""
        } else {
          this.pwdType = "password"
        }
      },
      async handleLogin () {
        let formValid = false
        this.$refs.loginForm.validate(valid => {
          formValid = valid
        })
        if (!formValid) {
          return
        }
        this.loading = true
        const data = await this.$store.dispatch("login", {
          account: this.loginForm.username,
          password: this.loginForm.password,
          typePropertyId: 1
        }).catch(() => { this.loading = false })
        this.loading = false
        if (data.success) {
          this.$router.push({ path: this.redirect || "/" })
          this.$message({
            message: "登录成功",
            type: "success"
          })
        }
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss">
  $bg: #2d3a4b;
  $light_gray: #eee;

  /* reset element-ui css */
  .login-container {
    .el-input {
      display: inline-block;
      height: 47px;
      width: 85%;

      input {
        background: transparent;
        border: 0;
        -webkit-appearance: none;
        border-radius: 0;
        padding: 12px 5px 12px 15px;
        color: $light_gray;
        height: 47px;
        caret-color: white;

        &:-webkit-autofill {
          -webkit-box-shadow: 0 0 0 1000px $bg inset !important;
          -webkit-text-fill-color: #fff !important;
        }
      }
    }

    .el-form-item {
      border: 1px solid rgba(255, 255, 255, 0.1);
      background: #2d3a4b;
      border-radius: 5px;
      color: #454545;
    }
  }

</style>

<style rel="stylesheet/scss" lang="scss" scoped>
  $bg: #2d3a4b;
  $dark_gray: #889aa4;
  $light_gray: #eee;
  .login-container {
    position: fixed;
    height: 100%;
    width: 100%;
    background-color: $bg;

    .login-form {
      position: absolute;
      left: 0;
      right: 0;
      width: 520px;
      max-width: 100%;
      padding: 35px 35px 15px 35px;
      margin: 120px auto;
    }

    .tips {
      font-size: 14px;
      color: #fff;
      margin-bottom: 10px;

      span {
        &:first-of-type {
          margin-right: 16px;
        }
      }
    }

    .svg-container {
      padding: 6px 5px 6px 15px;
      color: $dark_gray;
      vertical-align: middle;
      width: 30px;
      display: inline-block;
    }

    .vcode-item {
      .el-input {
        width: 45%;
      }

      .vcode-container {
        cursor: pointer;
        width: 150px;
        height: 52px;
        margin-right: 10px;
        float: right;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0 auto 40px auto;
      text-align: center;
      font-weight: bold;
    }

    .show-pwd {
      position: absolute;
      right: 10px;
      top: 7px;
      font-size: 16px;
      color: $dark_gray;
      cursor: pointer;
      user-select: none;
    }
  }
</style>
