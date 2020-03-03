/**
* @Author: limeng
* @Date: 2019-07-29 22:09
* @Description:
*/
<template>
  <div class="main">
    <div v-for="(item,index) in chatList" :key="index">
      <div style="text-align: center;margin-top: 10px"
           v-show="index === 0 || chatList[index - 1].chatTime !== item.chatTime">
        <span class="chat-time">{{item.chatTime}}</span>
      </div>
      <template v-if="!isFromMe(item)">
        <!--显示提交等行为-->
        <div class="chat-left" v-if="item.status">
          <img :src="getHeadImg(item)" class="head-img" alt="">
          <div class="chat-detail">
            <span class="name">{{getTitle(item)}}</span>
            <div class="chat-item">
            <span>
            <i>{{item.status}}</i>
            </span>
            </div>
          </div>
        </div>
        <div class="chat-left" v-if="item.text || item.includeImg" v-show="item.status !== '提交申诉'">
          <img :src="getHeadImg(item)" class="head-img" alt="">
          <div class="chat-detail">
            <span class="name">{{getTitle(item)}}</span>
            <div class="chat-item" v-if="item.text">
            <span>
            <i>{{item.text}}</i>
            </span>
            </div>
            <template v-for="(img,index) in item.imgList">
              <div class="chat-img" :key="'img' + index">
                <img
                  v-lazy="{src:imgFullPath(img.path),error:require('@/assets/img/accept-default.png'),loading:require('@/assets/img/accept-default.png')}"
                  alt="">
              </div>
            </template>
          </div>
        </div>
      </template>

      <template v-else>
        <div class="chat-right" v-if="item.status">
          <div class="chat-detail">
            <span class="name">{{getTitle(item)}}</span>
            <div class="chat-item">
            <span>
            <i>{{item.status}}</i>
            </span>
            </div>
          </div>
          <img :src="getHeadImg(item)" class="head-img" alt="">
        </div>
        <div class="chat-right" v-if="item.text || item.includeImg">
          <div class="chat-detail">
            <span class="name">{{getTitle(item)}}</span>
            <div class="chat-item" v-if="item.text">
            <span>
            <i>{{item.text}}</i>
            </span>
            </div>
            <template v-for="(img,index) in item.imgList">
              <div class="chat-img" :key="'img' + index">
                <img
                  v-lazy="{src:imgFullPath(img.path),error:require('@/assets/img/accept-default.png'),loading:require('@/assets/img/accept-default.png')}"
                  alt="">
              </div>
            </template>
          </div>
          <img :src="getHeadImg(item)" class="head-img" alt="">
        </div>
      </template>
    </div>
  </div>
</template>

<script>
  export default {
    name: "ChatList",
    props: {
      chatList: {
        type: Array,
        required: true
      },
      acceptUserId: {
        required: true
      }
    },
    methods: {
      isFromMe (item) {
        return item.fromUserId === this.acceptUserId
      },
      getHeadImg (item) {
        if (!item.fromUserId) {
          return require("@/assets/img/icon.png")
        }
        if (item.fromUserHeadImg) {
          return item.fromUserHeadImg
        }
        return require("@/assets/img/head.png")
      },
      getTitle (item) {
        if (!item.fromUserId) {
          return "官方"
        }
        if (this.isFromMe(item)) {
          return item.fromUserName + "(完成方)"
        }
        return "商家"
      }
    }
  }
</script>

<style scoped lang="less">
  .main {
    padding: 20px;
    background-color: #f3f3f3;
    margin-bottom: 20px;
  }

  .head-img {
    width: 30px;
    height: 30px;
    border-radius: 2px;
  }

  .chat-time {
    color: #b2b2b2;
    font-size: 12px;
    margin-top: 10px;
  }

  .chat-left {
    font-size: 0;
    margin-top: 10px;

    .chat-detail {
      font-size: 14px;
      display: inline-block;
      vertical-align: top;
      margin-top: -4px;

      .chat-item {
        width: 264px; //这里和main的padding要联动

        span::after {
          content: '';
          border: 8px solid #ffffff00;
          border-right: 16px solid #fff;
          position: absolute;
          top: 1px;
          left: -16px;
        }

        span {
          background-color: #fff;
          padding: 5px 8px;
          display: inline-block;
          border-radius: 4px;
          /*margin: 10px 0 10px 10px;*/
          margin-left: 10px;
          position: relative;
        }
      }

      .name {
        margin-left: 10px;
        font-size: 12px;
        color: #b2b2b2;
      }
    }

    .chat-img {
      margin-left: 10px;
      vertical-align: center;
      font-size: 0;
      margin-top: 10px;

      img {
        max-width: 254px; //这里和main的padding要联动
        vertical-align: center;
      }
    }
  }

  .chat-right {
    font-size: 0;
    text-align: right;
    margin-top: 10px;

    .chat-detail {
      font-size: 14px;
      display: inline-block;
      vertical-align: top;
      margin-top: -4px;

      .chat-item {
        width: 264px; //这里和main的padding要联动

        span::after {
          content: '';
          border: 8px solid #ffffff00;
          border-left: 16px solid #81d4cb;
          position: absolute;
          top: 1px;
          right: -16px;
        }

        span {
          background-color: #81d4cb;
          padding: 5px 8px;
          display: inline-block;
          border-radius: 4px;
          margin-right: 10px;
          position: relative;
          text-align: left;
        }
      }

      .name {
        margin-right: 10px;
        font-size: 12px;
        color: #b2b2b2;
      }
    }

    .chat-img {
      margin-right: 10px;
      vertical-align: center;
      font-size: 0;
      margin-top: 10px;

      img {
        max-width: 254px; //这里和main的padding要联动
        vertical-align: center;
      }
    }
  }

</style>
