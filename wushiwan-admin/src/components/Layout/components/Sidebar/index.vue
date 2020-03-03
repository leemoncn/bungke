<template>
  <el-scrollbar wrap-class="scrollbar-wrapper">
    <el-menu
      :show-timeout="200"
      :default-active="$route.meta.id"
      :collapse="isCollapse"
      @select="menuItemSelected"
      mode="vertical"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF">
      <sidebar-item v-for="(route,index) in menuList" :key="index" :item="route"/>
    </el-menu>
  </el-scrollbar>
</template>

<script>
  import { mapGetters } from "vuex"
  import SidebarItem from "./SidebarItem"

  export default {
    components: { SidebarItem },
    data () {
      return {
        menus: []
      }
    },
    computed: {
      ...mapGetters([
        "sidebar",
        "permission_routers",
        "addRouters",
        "menuList"
      ]),
      isCollapse () {
        return !this.sidebar.opened
      }
    },
    methods: {
      findMenuById (menuId, array) {
        for (let i = 0; i < array.length; i++) {
          const menu = array[i]

          if (menu.meta.id === menuId) {
            return menu
          }
          if (menu.children && menu.children instanceof Array) {
            const result = this.findMenuById(menuId, menu.children)
            if (result) {
              return result
            }
          }
        }
        return null
      },
      menuItemSelected: function (index) {
        const router = this.findMenuById(index, this.menus)
        this.$router.push({ path: router.path })
      }
    },
    created () {
      this.menus = this.addRouters.find((item) => item.path === "/").children
    }
  }
</script>
