<template>
  <div id="app">
    <transition :name="transitionName">
      <navigation>
        <router-view class="child-view"/>
      </navigation>
    </transition>
    <!--<button class="debug" @click="reload" v-show="!process.env.VUE_APP_DIST">刷新</button>-->
  </div>
</template>

<script>

  export default {
    name: "App",
    data () {
      return {
        transitionName: "",
        platformClass: "platform-ios"
      }
    },
    created () {
      if (/iPad|iPhone|iPod/.test(navigator.userAgent)) {
        this.platformClass = "platform-ios"
      } else {
        this.platformClass = "platform-ios"// 先都用ios的动画，android的动画有点快
      }
      document.querySelector("body").className = this.platformClass

      this.$navigation.on("forward", (to, from) => {
        if (from.route.name === null) { // 初始化进入程序
          this.transitionName = ""
          return
        }
        this.transitionName = "forward"
      })
      this.$navigation.on("back", (to, from) => {
        this.transitionName = "back"
      })
      this.$navigation.on("replace", (to, from) => {
        this.transitionName = ""
      })
    },
    mounted () {
    },
    methods: {
      reload () {
        window.location.reload(true)
      }
    }
  }
</script>

<style lang="less" scoped>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  .child-view {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: #fff;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  .debug {
    position: absolute;
    top: 0;
    left: 20%;
    font-size: 20px;
    background: #fff;
    overflow: hidden;
    z-index: 9999;
  }

  .forward-enter-active,
  .forward-leave-active,
  .back-enter-active,
  .back-leave-active,
  .platform-ios .forward-enter-active,
  .platform-ios .forward-leave-active,
  .platform-ios .back-enter-active,
  .platform-ios .back-leave-active {
    -webkit-transition-duration: .5s;
    transition-duration: .5s;
    -webkit-transition-timing-function: cubic-bezier(.36, .66, .04, 1);
    transition-timing-function: cubic-bezier(.36, .66, .04, 1);
    -webkit-transition-property: opacity, -webkit-transform;
    transition-property: opacity, transform;
  }

  .platform-android .forward-enter-active,
  .platform-android .forward-leave-active,
  .platform-android .back-enter-active,
  .platform-android .back-leave-active {
    -webkit-transition-duration: .2s;
    transition-duration: .2s;
    -webkit-transition-timing-function: cubic-bezier(0.4, 0.6, 0.2, 1);
    transition-timing-function: cubic-bezier(0.4, 0.6, 0.2, 1);
  }

  .view-right() {
    -webkit-transform: translateX(100%);
    transform: translateX(100%);
  }

  .view-left() {
    -webkit-transform: translateX(-33%);
    transform: translateX(-33%);
  }

  .view-center() {
    -webkit-transform: translateX(0);
    transform: translateX(0);
  }

  .forward-enter {
    .view-right();
    opacity: 1;
    z-index: 2;
  }

  .forward-enter-active {
    box-shadow: 0 0 10px rgba(0, 0, 0, .15);
  }

  .forward-enter-to {
    .view-center();
    opacity: 1;
    z-index: 2;
  }

  .forward-leave {
    .view-center();
    opacity: 0.8;
    z-index: 1;
  }

  .forward-leave-to {
    .view-left();
    opacity: 0;
    z-index: 1;
  }

  .back-enter {
    .view-left();
    opacity: 0.8;
    z-index: 1;
  }

  .back-enter-to {
    .view-center();
    opacity: 1;
    z-index: 1;
  }

  .back-leave {
    .view-center();
    opacity: 1;
    z-index: 2;
  }

  .back-leave-active {
    box-shadow: 0 0 10px rgba(0, 0, 0, .15);
  }

  .back-leave-to {
    .view-right();
    opacity: 1;
    z-index: 2;
  }

</style>
