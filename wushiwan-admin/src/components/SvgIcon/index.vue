<template>
  <svg :class="svgClass" :style="style" aria-hidden="true">
    <use :xlink:href="iconName"></use>
  </svg>
</template>

<script>
  export default {
    name: "svg-icon",
    props: {
      iconClass: {
        type: String,
        required: true
      },
      className: {
        type: String
      },
      width: {
        type: String,
        default: "1em"
      },
      height: {
        type: String,
        default: "1em"
      }
    },
    computed: {
      iconName () {
        return `#icon-${this.iconClass}`
      },
      svgClass () {
        if (this.className) {
          return "svg-icon " + this.className
        } else {
          return "svg-icon"
        }
      },
      style () {
        return `width: ${this.width};height: ${this.height}`
      }
    }
  }
</script>

<style scoped lang="css">
  .svg-icon {
    width: 1em;
    height: 1em;
    fill: currentColor;
    overflow: hidden;
  }

</style>
