<template>
  <section class="app-main">
    <iframe v-for="item in iframeList" width="100%" :key="item" height="100%" :src="item"
            v-show="isIframe && item === currentIframeUrl"></iframe>
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view :key="key" v-if="!isIframe"/>
      </keep-alive>
    </transition>
  </section>
</template>

<script>
  import { mapGetters } from "vuex"

  export default {
    name: "AppMain",
    computed: {
      ...mapGetters([
        "iframeList",
        "currentIframeUrl"
      ]),
      cachedViews () {
        return this.$store.state.tagsView.cachedViews
      },
      key () {
        return this.$route.fullPath
      },
      isIframe () {
        const iframe = this.$route.meta.iframe
        if (iframe) {
          return true
        }
        return false
      }
    },
    watch: {
      $route () {
        this.refreshIframe()
      }
    },
    mounted () {
      this.refreshIframe()
    },
    methods: {
      refreshIframe () {
        if (this.$route.fullPath.startsWith("/iframe")) {
          const url = this.$route.query.url
          this.$store.commit("ADD_IFRAME", url)
          this.$store.commit("SET_CURRENT_IFRAME_URL", url)
        }
      }
    }
  }
</script>

<style scoped>
  .app-main {
    /*50 = navbar  */
    /*34 = tagsView*/
    /*总共84*/
    min-height: calc(100vh - 84px);
    height: calc(100vh - 84px);
    width: 100%;
    position: relative;
    overflow: hidden;
  }
</style>
