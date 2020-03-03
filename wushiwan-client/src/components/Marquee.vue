<template>
  <ul
    v-if="reload"
    class="list"
    :style="stylesWrap"
    @mouseenter="onMouseenter"
    @mouseleave="onMouseleave"
  >
    <template v-for="(item, index) in marqueeList">
      <li
        ref="li"
        class="row"
        :key="index"
        :style="stylesItem"
        @transitionend="onTransitionend"
        v-html="item"
      ></li>
    </template>
  </ul>
</template>

<!--<div class="container">-->
<!--  <marquee :marqueeList="list" height="36px" width="100%" color="#f76a24" fontSize="14px" />-->
<!--</div>-->
<!--.container {-->
<!--margin: 0 auto;-->
<!--width: 300px;-->
<!--font-size: 14px;-->
<!--height: 36px;-->
<!--border: 1px dashed #000;-->
<!--text-align: left;-->
<!--padding: 0 5px;-->
<!--}-->
<!--https://github.com/Damon0820/vue-marquee-roll-up-->
<script>
  export default {
    props: {
      // 宽度
      width: {
        type: String,
        default: "200px"
      },
      // 高度
      height: {
        type: String,
        default: "38px"
      },
      // 字体颜色
      color: {
        type: String,
        default: "#787878"
      },
      // 字体大小
      fontSize: {
        type: String,
        default: "12px"
      },
      // 下拉速度
      showSpeed: {
        type: Number,
        validator (val) {
          return /^\d+$/.test(val)
        },
        default: 100
      },
      // 切换间隔时间(ms)
      pauseTime: {
        type: Number,
        validator (val) {
          return /^\d+$/.test(val)
        },
        default: 2500
      },
      // 滚动速度
      scrollSpeed: {
        type: Number,
        validator (val) {
          return /^\d+$/.test(val)
        },
        default: 100
      },
      // 悬停时暂停
      pauseOnHover: {
        type: Boolean,
        default: false
      },
      // 内容
      marqueeList: {
        type: Array,
        default: function () {
          return []
        }
      }
    },
    data () {
      return {
        current: -1,
        // ul样式
        stylesWrap: {
          width: this.width,
          height: this.height
        },
        // li样式
        stylesItem: {
          height: this.height,
          lineHeight: this.height
          // color: this.color 这里不让传递color，由外层class继承
          // fontSize: this.fontSize 这里不让传递fontsize，由外层class继承
        },
        scrollHeight: 0,

        status: "slideIn", // 切换动作
        paused: false, // 暂停

        delay: this.pauseTime, // 显示时间

        scrollLeftTransform: "", // 滚动的位置

        eventTag: 0,
        reload: true, // 强制组件重新渲染，默认是true，如果有数据变化，将其变成false，在nextTick中再变为true
        timeout: null
      }
    },
    watch: {
      marqueeList (val) {
        if (val.length > 0) {
          this.init()
          // this.reload = false
          // this.$nextTick(() => {
          //   this.reload = true
          //   this.init()
          // })
        }
      }
    },
    mounted () {
      // if (this.marqueeList.length > 0) {
      //   this.init()
      // }
    },
    activated () {
      console.log("activated")
      if (this.marqueeList.length > 0) {
        this.paused = false
        this.init()
      }
    },
    deactivated () {
      console.log("1111")
      this.current = -1
      this.paused = true
      console.log("deactivated")
    },
    destroyed () {
      console.log("222")
      console.log("destroyed")
      this.current = -1
      this.paused = true
      // clearTimeout(this.timeout)
    },
    methods: {
      init () {
        console.log("init")
        // clearTimeout(this.timeout)
        this.mqLength = this.marqueeList.length
        this.scrollHeight = this.height.replace(/[^0-9]/gi, "")
        clearTimeout(this.timeout)
        for (let i = 0; i < this.marqueeList.length; i++) {
          let li = this.$refs.li[i]
          li.style.transition = "all 0s linear"
          li.style.transform = "translate(0px, -100%)"
          li.style.visibility = "hidden"
        }
        this.$nextTick(() => {
          this.showNext()
        })
      },
      showNext () {
        this.status = "slideIn"
        this.delay = this.pauseTime
        this.current++

        if (this.current >= this.mqLength) {
          this.current = 0
        }

        let li = this.$refs.li[this.current]
        li.style.transition = "all 0s linear"
        li.style.transform = "translate(0px, -100%)"
        li.style.visibility = "hidden"

        let duration = this.scrollHeight / this.showSpeed
        this.timeout = setTimeout(() => {
          li.style.transition = `all ${duration}s linear`
          li.style.transform = "translate(0px, 0px)"
          li.style.visibility = "visible"
        }, 0)
      },
      onTransitionend () {
        // 防止重复执行
        if (this.eventTag === 0) {
          this.eventTag = 1
          this.timeout = setTimeout(() => {
            this.eventTag = 0
          }, 100)
        } else {
          return
        }

        let li = this.$refs.li[this.current]
        switch (this.status) {
          case "slideIn":
            if (!this.paused) {
              if (li.scrollWidth > li.clientWidth) {
                this.status = "scrollLeft"
                // 左移
                this.timeout = setTimeout(() => {
                  let duration = li.scrollWidth / this.scrollSpeed
                  li.style.transition = `all ${duration}s linear`
                  li.style.transform = `translate(-${li.scrollWidth}px, 0px)`
                  li.style.visibility = "visible"
                }, 500)
              } else {
                this.status = "slideOut"
                // 下移动至底部不可见区域
                this.timeout = setTimeout(() => {
                  let duration = this.scrollHeight / this.showSpeed
                  li.style.transition = `all ${duration}s linear`
                  li.style.transform = "translate(0px, 100%)"
                  li.style.visibility = "hidden"
                }, this.delay)
              }
            }
            break
          case "slideOut":
            this.showNext()
            break
          case "scrollLeft":
            this.showNext()
            break
        }
      },
      onMouseenter () {
        if (!this.pauseOnHover === true) return false

        this.paused = true
        let li = this.$refs.li[this.current]
        switch (this.status) {
          case "slideIn":
          case "slideOut":
            break
          case "scrollLeft": {
            // 记录暂停时滚动的位置
            this.scrollLeftTransform = window.getComputedStyle(li).transform
            li.style.transition = "all 0s linear"
            li.style.transform = this.scrollLeftTransform
            break
          }
        }
      },
      onMouseleave () {
        if (!this.pauseOnHover === true) return false

        this.paused = false
        let li = this.$refs.li[this.current]
        switch (this.status) {
          case "slideIn": {
            let transform = window.getComputedStyle(li).transform
            let slideInMatrix
            if (!!window.ActiveXObject || "ActiveXObject" in window) {
              // IE
              slideInMatrix = new MSCSSMatrix(transform)
            } else {
              // WebKit
              slideInMatrix = new WebKitCSSMatrix(transform)
            }
            // 进入过程中不执行切换，进入完成才切换
            if (slideInMatrix.m42 >= 0) {
              this.delay = 0
              this.onTransitionend()
            }
            break
          }
          case "slideOut":
            break
          case "scrollLeft": {
            let scrollLeftMatrix
            if (!!window.ActiveXObject || "ActiveXObject" in window) {
              // IE
              scrollLeftMatrix = new MSCSSMatrix(this.scrollLeftTransform)
            } else {
              // WebKit
              scrollLeftMatrix = new WebKitCSSMatrix(this.scrollLeftTransform)
            }
            // 去掉已经滚动的长度，再计算时间
            let translateX = li.scrollWidth + scrollLeftMatrix.m41
            let duration = translateX / this.scrollSpeed
            li.style.transition = `all ${duration}s linear`
            li.style.transform = `translate(-${li.scrollWidth}px, 0px)`
            li.style.visibility = "visible"
            break
          }
        }
      }
    }
  }
</script>

<style>
  .list {
    overflow: hidden;
    position: relative;
    width: 100%;
    display: inline-block;
  }

  .row {
    display: block;
    white-space: nowrap;
    width: 100%;
    position: absolute;
    top: 0;
    left: 0;
    transform: translate(0px, -100%);
  }
</style>
