/**
* @Author: limeng
* @Date: 2019-04-11 22:12
* @Description:
*/
<template>
  <div class="content">
    <img class="img"
         v-lazy="{src:imgFullPath(missionItem.imgUrl),error:require('@/assets/img/accept-default.png'),loading:require('@/assets/img/accept-default.png')}"
         alt=""/>
    <div class="right-div flex-column">
      <span class="title">{{missionTypeName + "&nbsp;&nbsp;" + missionItem.title}}</span>

      <div class="description">
        <span>编号：{{missionItem.id}}</span>
        <span class="price">{{`单价: ${missionItem.price / 100}元`}}</span>
      </div>
      <div class="description">
        <span>截止：{{missionItem.deadlineTime}}</span>
      </div>
      <div>
        <img src="@/assets/img/ding.png" class="img-icon" v-if="missionItem.isDing">
        <img src="@/assets/img/bao.png" class="img-icon" v-if="missionItem.isBao">
        <template v-if="missionItem.mobilePropertyId === 18">
          <img src="@/assets/img/android.png" class="img-icon">
          <img src="@/assets/img/ios.png" class="img-icon">
        </template>
        <template v-else>
          <img src="@/assets/img/android.png" class="img-icon" v-if="missionItem.mobilePropertyId === 19">
          <img src="@/assets/img/ios.png" class="img-icon" v-if="missionItem.mobilePropertyId === 20">
        </template>
      </div>
    </div>
  </div>
</template>

<script>
  import { getPropertyById } from "@/service/property"

  export default {
    name: "AcceptMissionItem",
    data: function () {
      return {}
    },
    props: {
      missionItem: {
        type: Object,
        required: true
      }
    },
    computed: {
      missionTypeName: function () {
        const property = getPropertyById(this.missionItem.missionTypePropertyId)
        return property.name
      }
    },
    mounted () {
    }
  }
</script>

<style scoped lang="less">
  @border: 8px;

  .content {
    background-color: white;
    display: flex;
    height: 96px;
    align-items: center;
  }

  .img {
    width: 64px;
    height: 85px;
    border-radius: 4px;
    flex-shrink: 0;
    object-fit: cover;
  }

  .title {
    font-size: 17px;
    color: #424242;
    margin-top: 6px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    width: 270px;
  }

  .description {
    line-height: normal;

    span {
      font-size: 14px;
      color: #424242;
    }

    .price {
      margin-left: 6px;
    }
  }

  .right-div {
    margin-left: 15px;
    height: 100%;
    width: 100%;
  }

  .img-icon {
    width: 20px;
    height: 20px;
    margin-right: 4px;
  }

</style>
