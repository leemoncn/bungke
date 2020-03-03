/**
* @Author: limeng
* @Date: 2019-04-03 22:10
* @Description:
*/
<template>
  <div class="column-view">
    <van-nav-bar
      title="发布任务"
      right-text="说明"
      left-text="返回"
      fixed
      left-arrow
      @click-left="onNavBarLeftPressed"
      @click-right="onNavBarRightPressed"/>
    <div class="under-nav main-height overflow-auto">
      <section>
        <van-cell-group title="任务详情">
          <van-cell title="任务类型" :value="missionTypeTitles[missionTypeIndex]" is-link
                    @click="showMissionTypePicker = true"/>
          <van-cell title="支持设备" :value="platformNames[supportPlatformIndex]" is-link
                    @click="showPlatformTypePicker = true"/>
          <van-field v-model="missionTitle" label="标题" placeholder="项目名称+核心要求，12字以内"/>
          <van-cell title="截止时间" :value="deadLineTimeString" is-link
                    @click="onDeadLinePickClick"/>
          <van-field v-model="price" label="出价" :placeholder="pricePlaceholder" type="number"/>
          <van-field v-model="count" label="数量" :placeholder="countPlaceholder" type="number"/>
          <van-cell title="合计" :value="totalPrice" :label="feeText">
            <!--<span style="float: left">placeholder="最少50单"</span>-->
          </van-cell>
        </van-cell-group>
        <van-cell-group title="审核内容">
          <van-collapse v-model="collapseActiveName" accordion>
            <van-collapse-item title="审核图样" :label="`上传验证图样例，最多可上传${sampleImgCount}张`" name="1">
              <div class="sample-img">
                <div class="sample-img-card" v-for="(item,index) in sampleImgArray"
                     :key="index" @click.stop="onSampleItemClick(index)">
                  <img-card :add-size="50" @onReadImg="onSampleReadImg" @onDeleteImg="onSampleDeleteImg(index)"
                            :img-content="item.content"/>
                </div>
                <div class="sample-img-card" v-show="sampleImgArray.length < sampleImgCount">
                  <img-card :add-size="50" @onReadImg="onSampleReadImg"/>
                </div>
              </div>
            </van-collapse-item>
            <van-collapse-item title="操作说明" label="简单易懂的操作指南将大幅度提高接单效率" name="2">
              <van-steps direction="vertical" :active="-1">
                <van-step v-for="(item,index) in explainSteps" :key="index">
                  <div class="flex-row flex-align-center">
                    <van-icon :info="index + 1" class="order-icon"/>
                    <div class="explain-img-card" @click="onExplainItemClick(index)">
                      <img-card :add-size="40" @onReadImg="onExplainReadImg"
                                @onDeleteImg="onExplainDeleteImg(index)"
                                :img-content="item.img?item.img.content:null" :name="index + ''" :delete-size="25"/>
                    </div>
                    <textarea v-model="item.text" class="text-explain-input" placeholder="请输入步骤说明"></textarea>
                    <van-icon name="clear" :size="px2rem(30) + 'rem'"
                              color="#969799" class="explain-delete-all"
                              @click.stop="onExplainDelete(index)"/>
                  </div>
                </van-step>
              </van-steps>
              <div class="flex-row flex-justify-center">
                <button class="add-explain-button" @click="onAddExplainStepPressed">添加步骤</button>
              </div>
            </van-collapse-item>
          </van-collapse>
          <van-field v-model="missionUrl" label="链接" placeholder="请输入准确链接地址，如无可不填"/>
          <van-field v-model="textVerify" label="文字验证" type="textarea" placeholder="如需接单者提供文字信息，请在此输入，如不需要可不填，500字以内"
                     autosize/>
          <van-field v-model="remarks" label="备注" type="textarea" placeholder="200字以内" autosize/>
        </van-cell-group>
      </section>

      <div class="submit">
        <button class="left-button" @click="onNavBarLeftPressed">取&nbsp;&nbsp;消</button>
        <button @click="onCreateMissionButtonPressed">提&nbsp;&nbsp;交</button>
      </div>
    </div>

    <van-popup v-model="showMissionTypePicker" position="bottom" :overlay="true">
      <van-picker :columns="missionTypeTitles"
                  show-toolbar
                  title="请选择任务类型"
                  @confirm="onMissionTypePickConfirm"
                  @cancel="showMissionTypePicker = false"/>
    </van-popup>
    <van-popup v-model="showPlatformTypePicker" position="bottom" :overlay="true">
      <van-picker :columns="platformNames"
                  show-toolbar
                  title="请选择支持设备类型"
                  @confirm="onPlatformTypePickConfirm"
                  @cancel="showPlatformTypePicker = false"/>
    </van-popup>
    <van-popup v-model="showMissionDeadLineTimePicker" position="bottom" :overlay="true">
      <van-datetime-picker
        type="datetime"
        :min-date="minDate"
        show-toolbar
        title="请选择截止时间"
        @confirm="onDeadLinePickConfirm"
        @cancel="showMissionDeadLineTimePicker = false"/>
    </van-popup>
  </div>
</template>

<script>
  import { ImagePreview } from "vant"
  import { getPropertyListByType, PropertyType } from "@/service/property"
  import { CREATE_MISSION_URL, IMG_LIST_URL, MISSION_DETAIL_URL, MISSION_STEP_URL } from "@/config/host"
  import { getMissionRuleByTypePropertyId } from "@/service/mission-rule"
  import ImgCard from "@/components/ImgCard"
  import { createFileContentArrayFromFileArray, uploadToQiniu } from "@/service/upload"

  export default {
    name: "CreateMission",
    components: { ImgCard },
    data () {
      return {
        missionId: null,
        showMissionTypePicker: false,
        showPlatformTypePicker: false,
        showMissionDeadLineTimePicker: false,
        supportPlatformIndex: 0,
        supportPlatformPropertyId: null,
        missionTypeIndex: 0,
        missionTypePropertyId: null,
        missionTitle: "",
        deadLineTime: null,
        minDate: new Date(),
        price: null,
        count: null,
        collapseActiveName: "",
        missionUrl: "",
        textVerify: "",
        remarks: "",
        sampleImgArray: [],
        explainSteps: [{}]
      }
    },
    computed: {
      totalPrice: function () {
        if (this.price === null || this.price === undefined || this.count === null || this.count === undefined) {
          return null
        }
        const partner = this.$store.getters.partner
        const minFeePrice = partner.minFeePrice / 100
        // 当前单位元
        let price = this.count * this.price
        let fee = price * this.$store.getters.partner.feePercent / 100.0
        if (fee < minFeePrice) {
          fee = minFeePrice
        }
        return (price + fee).toFixed(2)
      },
      feeText: function () {
        // 服务费：成交额XXX 最低XXX元
        const partner = this.$store.getters.partner
        const minFeePrice = partner.minFeePrice / 100
        if (minFeePrice === 0) {
          return `服务费：成交额${partner.feePercent}%`
        }
        return `服务费：成交额${partner.feePercent}% 最低${minFeePrice}元`
      },
      missionTypes: function () {
        let missionRuleList = this.$store.getters.missionRuleList
        let types = []
        for (const e of missionRuleList) {
          types.push(this.getPropertyById(e.typePropertyId))
        }
        return types
      },
      missionTypeTitles: function () {
        return this.missionTypes.map((p) => {
          return p.name
        })
      },
      supportPlatforms: function () {
        return getPropertyListByType(PropertyType.DEVICE_TYPE)
      },
      platformNames: function () {
        return this.supportPlatforms.map((p) => {
          return p.name
        })
      },
      deadLineTimeString: function () {
        if (this.deadLineTime) {
          return this.deadLineTime.format()
        }
        return null
      },
      missionRule: function () {
        if (this.missionTypePropertyId) {
          return getMissionRuleByTypePropertyId(this.missionTypePropertyId)
        }
        return null
      },
      pricePlaceholder: function () {
        if (this.missionRule) {
          return `最低出价${this.missionRule.minPrice / 100}元`
        }
        return ""
      },
      countPlaceholder: function () {
        if (this.missionRule) {
          return `最少${this.missionRule.minCount}单`
        }
        return ""
      },
      sampleImgCount: function () {
        if (this.missionRule) {
          return this.missionRule.verifyImgCount
        }
        return 5
      }
    },
    async mounted () {
      this.missionId = this.$route.query.id
      if (this.missionId) { // 重新发布任务，或者说是修改任务
        this.showLoading()
        const result = await this.httpPost(MISSION_DETAIL_URL, { id: this.missionId })
        if (result.success) {
          const data = result.data
          for (let i = 0; i < this.missionTypes.length; i++) {
            const missionType = this.missionTypes[i]
            if (missionType.id === data.typePropertyId) {
              this.missionTypeIndex = i
              break
            }
          }
          for (let i = 0; i < this.supportPlatforms.length; i++) {
            const supportPlatform = this.supportPlatforms[i]
            if (supportPlatform.id === data.mobilePropertyId) {
              this.supportPlatformIndex = i
              break
            }
          }
          this.missionTitle = data.title
          if (data.deadlineTime) {
            let arr1 = data.deadlineTime.split(" ")
            let sdate = arr1[0].split("-")
            this.deadLineTime = new Date(sdate[0], sdate[1] - 1, sdate[2])
          }
          this.price = data.price / 100.0
          this.count = data.count
          this.missionUrl = data.url
          this.textVerify = data.textVerify
          this.remarks = data.remark
          const sampleData = await this.httpPost(IMG_LIST_URL, { dataId: this.missionId, type: 0 })
          if (sampleData.success) {
            sampleData.data.forEach(value => {
              this.sampleImgArray.push({ content: value.path })
            })
          }
          const explainData = await this.httpPost(MISSION_STEP_URL, { id: this.missionId })
          if (explainData.success) {
            this.explainSteps = explainData.data
            for (const step of this.explainSteps) {
              let img = step.img
              if (img) {
                step.img = { content: img }
              }
            }
          }
        }
        this.hideLoading()
      }
      this.missionTypePropertyId = this.missionTypes[this.missionTypeIndex].id
      this.supportPlatformPropertyId = this.supportPlatforms[this.supportPlatformIndex].id
    },
    methods: {
      onNavBarLeftPressed () {
        this.$router.back()
      },
      onNavBarRightPressed () {
      },
      onMissionTypePickConfirm (value, index) {
        this.showMissionTypePicker = false
        this.missionTypeIndex = index
        this.missionTypePropertyId = this.missionTypes[this.missionTypeIndex].id
      },
      onPlatformTypePickConfirm (value, index) {
        this.showPlatformTypePicker = false
        this.supportPlatformIndex = index
        this.supportPlatformPropertyId = this.supportPlatforms[this.supportPlatformIndex].id
      },
      onDeadLinePickConfirm (value, index) {
        this.showMissionDeadLineTimePicker = false
        this.deadLineTime = value
      },
      onDeadLinePickClick () {
        this.minDate = new Date(new Date().getTime() + 25 * 60 * 60 * 1000)
        this.showMissionDeadLineTimePicker = true
      },
      onSampleItemClick (itemIndex) { // 点击审核图样的某一张图片
        ImagePreview({
          images: createFileContentArrayFromFileArray(this.sampleImgArray),
          startPosition: itemIndex
        })
      },
      onExplainItemClick (itemIndex) { // 点击操作说明的某一张图片
        if (!this.explainSteps[itemIndex].img) {
          return
        }
        ImagePreview({
          images: createFileContentArrayFromFileArray([this.explainSteps[itemIndex].img]),
          startPosition: 0
        })
      },
      onSampleReadImg (file, name) { // 审核图样
        this.sampleImgArray.push(file)
      },
      onSampleDeleteImg (index) { // 审核图样
        this.sampleImgArray.splice(index, 1)
      },
      onExplainReadImg (file, name) { // 操作说明
        console.log("explainSteps = ", this.explainSteps)
        console.log("name = ", name)
        let oldValue = this.explainSteps[name]
        oldValue.img = file

        this.$set(this.explainSteps, name, oldValue)
      },
      onExplainDeleteImg (index) { // 操作说明
        let oldValue = this.explainSteps[index]
        oldValue.img = null
        this.$set(this.explainSteps, index, oldValue)
      },
      onExplainDelete (index) {
        if (this.explainSteps.length === 1) { // 最后一个
          this.explainSteps = [{}]
        } else {
          this.explainSteps.splice(index, 1)
        }
      },
      onAddExplainStepPressed () {
        if (this.explainSteps.length >= 30) {
          this.toastFail("最多30个步骤")
        } else {
          this.explainSteps.push({})
        }
      },
      async onCreateMissionButtonPressed () {
        const data = {
          typePropertyId: this.missionTypePropertyId,
          mobilePropertyId: this.supportPlatformPropertyId,
          title: this.missionTitle,
          deadlineTime: this.deadLineTimeString,
          price: this.price * 100,
          count: this.count,
          url: this.missionUrl,
          textVerify: this.textVerify,
          remark: this.remarks
        }
        if (this.missionId) {
          data.missionId = this.missionId
        }
        if (!this.validData(data)) {
          return
        }
        this.showLoading()
        this.uploadSample(data)
      },
      async uploadSample (data) {
        // 需要上传的审核图样
        let needUploadSampleImgArray = []
        for (const sample of this.sampleImgArray) {
          if (sample.file) {
            needUploadSampleImgArray.push(sample)
          }
        }
        if (needUploadSampleImgArray.length === 0) { // 直接上传操作图样
          let sampleImgPathArray = []
          for (const item of this.sampleImgArray) {
            sampleImgPathArray.push(item.content)
          }
          data.sampleImgList = sampleImgPathArray
          this.uploadExplain(data)
        } else { // 先上传审核图样
          await uploadToQiniu(needUploadSampleImgArray, async (key, list) => {
            data.sampleKey = key
            let sampleImgPathArray = []
            for (const item of this.sampleImgArray) {
              if (!item.file) { // 没有修改过的，不需要上传的
                sampleImgPathArray.push(item.content)
              }
            }
            for (const item of list) { // 上传完成的
              sampleImgPathArray.push(item.fileName)
            }
            data.sampleImgList = sampleImgPathArray
            this.uploadExplain(data)
          }, () => {
            this.toastFail("图片上传失败，请重试")
          })
        }
      },
      async uploadExplain (data) {
        // 需要上传的操作图样
        let needUploadExplainImgArray = []
        for (const explain of this.explainSteps) {
          if (explain.img && explain.img.file) {
            needUploadExplainImgArray.push(explain.img)
          }
        }
        if (needUploadExplainImgArray.length === 0) { // 直接创建任务
          data.explainList = []
          this.requestCreateMission(data)
        } else { // 先上传操作图片
          await uploadToQiniu(needUploadExplainImgArray, async (key, list) => {
            data.explainKey = key
            data.explainList = this.createExplainList(list)
            this.requestCreateMission(data)
          }, () => {
            this.toastFail("图片上传失败，请重试")
          })
        }
      },
      createExplainList (list) {
        let explainList = []
        let index = 0
        for (const step of this.explainSteps) {
          let sa = {}
          if (step.text) {
            sa.text = step.text
          }
          if (step.img) {
            if (step.img.file) { // 上传过图片
              sa.img = list[index].fileName
              index++
            } else { // 重发任务，没改图片
              sa.img = step.img.content
            }
          }
          if (sa.text || sa.img) {
            explainList.push(sa)
          }
        }
        return explainList
      },
      async requestCreateMission (data) {
        const result = await this.httpPostWithLoading(CREATE_MISSION_URL, data)
        if (result.success) {
          this.$store.commit("updateUser", result.data)
          this.toastSuccess("创建成功")
          this.$router.back()
        }
      },
      validData (data) {
        const deadlineTime = data.deadlineTime
        if (!deadlineTime) {
          this.toastFail("请选择截止时间")
          return false
        }
        const title = data.title
        if (title.length === 0 || !title) {
          this.toastFail("请输入标题")
          return false
        }
        if (title.length > 12) {
          this.toastFail("标题过长")
          return false
        }
        // 正整数正则
        const re = /^[0-9]+$/
        const price = data.price
        if (!price) {
          this.toastFail("请输入出价")
          return false
        }
        if (price < this.missionRule.minPrice) {
          this.toastFail(`最低出价为${this.missionRule.minPrice / 100}元`)
          return false
        }
        if (price !== this.price * 100) {
          this.toastFail("请输入正确的出价")
          return false
        }
        const count = data.count
        if (!count) {
          this.toastFail("请输入数量")
          return false
        }
        if (!re.test(count)) {
          this.toastFail("数量需为整数")
          return false
        }
        if (count < this.missionRule.minCount) {
          this.toastFail(`最低数量为${this.missionRule.minCount}`)
          return false
        }
        if ((this.totalPrice * 100) > this.$store.getters.user.missionCoin) {
          this.toastFail("任务币余额不足")
          return false
        }
        if (!this.sampleImgArray || this.sampleImgArray.length === 0) {
          this.toastFail("请添加审核图样")
          return false
        }
        const url = data.url
        if (url.length > 500) {
          this.toastFail("链接过长")
          return false
        }
        const textVerify = data.textVerify
        if (textVerify.length > 500) {
          this.toastFail("文字验证过长")
          return false
        }
        const remark = data.remark
        if (remark.length > 200) {
          this.toastFail("备注过长")
          return false
        }
        if (this.explainSteps.length > 1) {
          for (let i = 0; i < this.explainSteps.length; i++) {
            const explain = this.explainSteps[i]
            if (!explain.text && !explain.img) {
              this.toastShortMsg(`请输入第${i + 1}步的步骤说明或者说明图片`)
              return false
            }
          }
        }
        return true
      }
    }
  }
</script>

<style scoped lang="less">
  .swipe-img {
    width: calc(100% - 8px);
    height: calc(100% - 8px);
    border: 2px;
    border-radius: 10px;
    background-color: #f3f5f9;
    object-fit: cover;
  }

  .sample-img {
    width: 100%;
    height: 130px;
    white-space: nowrap;
    overflow-y: hidden;
    overflow-x: scroll;
    display: flex;

    &::-webkit-scrollbar {
      display: none;
    }

    .sample-img-card {
      width: 100px;
      height: 130px;
      flex-shrink: 0;
    }
  }

  .swipe-item {
    display: flex;
    align-items: center;
    justify-content: center;

    .delete {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 30px;
      height: 30px;
      background-color: white;
      position: absolute;
      border: 2px;
      border-radius: 50%;
      left: 0;
      top: 0
    }
  }

  .plus-div {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .submit {
    display: flex;
    background-color: transparent;
    justify-content: center;
    padding: 30px 40px;

    .left-button {
      margin-right: 40px
    }

    button {
      width: 94px;
      height: 36px;
      line-height: 36px;
      border-radius: 10px;
      font-size: 16px;
      color: #ffffff;
    }

    button:nth-child(1) {
      background-color: #dd4944;
    }

    button:nth-child(2) {
      background-color: #81d4cb;
    }
  }

  .explain-img-card {
    width: 85px;
    height: 110px;
    margin-left: 10px;
    flex-shrink: 0;
  }

  .explain-delete-all {
    margin-left: 10px;
  }

  .text-explain-input {
    width: 200px;
    height: 70px;
    font-size: 14px;
    border: 1px solid #b3b3b3;
    border-radius: 4px;
    resize: none;
    color: black;

    &::-webkit-input-placeholder {
      font-size: 14px;
    }

    ::-moz-placeholder {
      font-size: 14px;
    }

    ::-moz-placeholder {
      font-size: 14px;
    }

    ::-ms-input-placeholder {
      font-size: 14px;
    }
  }

  .order-icon {
    div {
      background-color: #969696;
    }
  }

  .add-explain-button {
    width: 120px;
    height: 32px;
    border-radius: 10px;
    text-align: center;
    font-size: 14px;
    color: #ffffff;
    background-color: #81d4cb;
  }
</style>
