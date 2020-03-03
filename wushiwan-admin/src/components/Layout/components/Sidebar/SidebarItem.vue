<template>
  <div v-if="item.isShow" class="menu-wrapper">
    <!--子菜单还有子菜单-->
    <el-submenu v-if="item.children instanceof Array && item.children.length > 0"
                :index="item.id.toString()">
      <!--有子菜单的子菜单-->
      <template slot="title">
        <item :title="item.name" :icon="item.icon"/>
      </template>

      <template v-if="item.isShow">
        <div v-for="(child,index) in item.children" :key="index">
          <sidebar-item
            v-if="child.children && child.children.length>0"
            :is-nest="true"
            :item="child"
            :key="child.id.toString()"
            class="nest-menu"/>
          <el-menu-item v-else-if="child.isShow" :index="child.id.toString()">
            <item :title="child.name" :icon="child.icon"/>
          </el-menu-item>
        </div>
      </template>

    </el-submenu>
    <!--子菜单可以点击-->
    <el-menu-item v-else :index="item.id.toString()">
      <template slot="title">
        <item :title="item.name" :icon="item.icon"/>
      </template>
    </el-menu-item>
  </div>
</template>

<script>
  import Item from "./Item"

  export default {
    name: "SidebarItem",
    components: { Item },
    props: {
      // route object
      item: {
        type: Object,
        required: true
      }
    },
    data () {
      return {
        onlyOneChild: null
      }
    },
    methods: {
      resolvePath (routePath) {
        if (routePath === undefined) {
          return ""
        }
        return routePath
      },
      haveHrefChildren (children) { // children里面也可打开的页面，也就是有href字段
        for (let child of children) {
          if (child.href) {
            return true
          }
          const c = child.children
          if (c instanceof Array && c.children.length > 0) {
            return this.haveHrefChildren(c)
          }
        }
        return false
      }
    }
  }
</script>
