<template>
  <div>
    Dashboard页面
  </div>
</template>

<script>
  import { mapGetters } from "vuex"

  // import Vue from "vue"

  export default {
    name: "Dashboard",
    computed: {
      ...mapGetters([
        "name"
      ])
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>

</style>
