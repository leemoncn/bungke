<template>
  <!-- eslint-disable vue/require-component-is-->
  <component v-bind="linkProps(to)" @click="onClick">
    <slot/>
  </component>
</template>

<script>
  import IframeLink from "@/components/IframeLink"

  export default {
    props: {
      to: {
        type: String,
        required: true
      }
    },

    components: {
      /* eslint-disable vue/no-unused-components */
      IframeLink
    },
    methods: {
      onClick () {
        if (this.isIframeLink(this.to)) {
          const url = "/iframe" + "?url=" + encodeURIComponent(this.to)
          this.$router.push(url)
        }
      },
      isIframeLink (routePath) {
        return !!routePath.startsWith("http")
      },
      linkProps (url) {
        // if (this.isExternalLink(url)) {
        //   return {
        //     is: "a",
        //     href: url,
        //     target: "_blank",
        //     rel: "noopener"
        //   }
        // }
        if (this.isIframeLink(url)) {
          return {
            is: "a",
            href: "javascript:void(0)"
          }
        }
        return {
          is: "router-link",
          to: url
        }
      }
    }
  }
</script>
