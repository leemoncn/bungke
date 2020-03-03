/**
* @Author: leemon
* @Date: 2019-04-22 11:13
* @Description:
*/
<template>
  <div>
    <el-row :gutter="20" class="top">
      <el-col :span="8">
        <div class="col">
          <el-row type="flex" justify="space-around" align="middle" style="height: 100px" :gutter="0">
            <el-col :span="8">
              <div class="flex-row flex-justify-center">
                <el-button type="danger" @click="onCleanCacheButtonClicked">清理缓存</el-button>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="flex-row flex-justify-center">
                <el-button type="danger" @click="onTestButtonClicked">测试</el-button>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="flex-row flex-justify-center">
                <el-button type="danger" @click="pushNotification">发送通知</el-button>
              </div>
            </el-col>
          </el-row>
          <el-row type="flex" justify="space-around" align="middle" style="height: 100px" :gutter="0">
            <el-col :span="8" v-if="isDistEnv">
              <div class="flex-row flex-justify-center">
                <el-button type="danger" @click="mayiDialogVisible = true">从蚂蚁创建任务</el-button>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="col">

        </div>
      </el-col>
      <el-col :span="8">
        <div class="col">

        </div>
      </el-col>
    </el-row>
    <push-dialog :edit-dialog-visible.sync="pushDialogVisible"/>
    <el-dialog title="从蚂蚁创建任务" :visible.sync="mayiDialogVisible" :close-on-click-modal="false" id="mayiDialog">
      <el-form :model="createMissionModel" prop="missionNo" :rules="createMissionDialogRules">
        <el-form-item label="蚂蚁任务编号" label-width="120px" prop="missionNo">
          <el-input v-model="createMissionModel.missionNo" autocomplete="off">
            <el-button slot="append" icon="el-icon-search" @click="searchMissionFromMayi"></el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="蚂蚁session" label-width="120px" prop="sessionId">
          <el-input v-model="createMissionModel.sessionId" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="发布到哪个用户" label-width="120px" prop="toUserId">
          <el-select v-model="createMissionModel.toUserId" placeholder="请选择">
            <el-option label="100130(跑不快)" :value="100130"/>
            <el-option label="100132(清月)" :value="100132"/>
            <el-option label="100133(冰冰水晶)" :value="100133"/>
            <el-option label="100125(冰冰兔)" :value="100125"/>
          </el-select>
        </el-form-item>
        <div v-if="mayiMissionDetail">
          <p>标题：{{mayiMissionDetail.task_title}}</p>
          <p>类别：{{mayiMissionDetail.class_name}}</p>
          <p>单价：{{mayiMissionDetail.unit_price}}元</p>
          <p>剩余数量：{{mayiMissionDetail.remainder_num}}</p>
          <p>进行中数量：{{mayiMissionDetail.ongoing_num}}</p>
          <p>是否置顶：{{mayiMissionDetail.top_flag === "1"}}</p>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="mayiDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="createMissionFromMayi">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import { CLEAN_CACHE_URL, MAYI_CREATE_MISSION, MAYI_MISSION_DETAIL, TEST_URL } from "@/config/host"
  import PushDialog from "@/views/Push/PushDialog"

  export default {
    name: "Home",
    components: { PushDialog },
    data () {
      return {
        pushDialogVisible: false,
        mayiDialogVisible: false,
        createMissionModel: {},
        mayiMissionResult: {},
        mayiMissionDetail: null,
        isDistEnv: process.env.VUE_APP_DIST,
        createMissionDialogRules: {
          missionNo: [{ required: true, message: "请输入蚂蚁任务编号", trigger: "blur" }],
          sessionId: [{ required: true, message: "请输入蚂蚁session", trigger: "blur" }],
          toUserId: [{ required: true, message: "请选择发布到那个用户", trigger: "blur" }]
        }
      }
    },
    methods: {
      onCleanCacheButtonClicked () {
        this.$confirm("此操作将清理redis上所有的缓存, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.httpPostWithLoading(CLEAN_CACHE_URL, null).then(({ data, success }) => {
            if (success) {
              this.$message({
                type: "success",
                message: "清理成功!"
              })
            }
          })
        }).catch(() => {
          // this.$message({
          //   type: "info",
          //   message: "已取消删除"
          // })
        })
      },
      onTestButtonClicked () {
        this.httpPostWithLoading(TEST_URL, null)
      },
      pushNotification () {
        this.pushDialogVisible = true
      },
      async createMissionFromMayi () {
        const result = await this.httpPostWithLoading(MAYI_CREATE_MISSION, this.createMissionModel, "#mayiDialog .el-dialog")
        if (result.success) {
          this.createMissionModel.missionNo = null
          this.$message({
            type: "success",
            message: "创建成功!"
          })
        }
        this.mayiDialogVisible = false
        this.mayiMissionDetail = null
      },
      async searchMissionFromMayi () {
        const result = await this.httpPostWithLoading(MAYI_MISSION_DETAIL, this.createMissionModel, "#mayiDialog .el-dialog")
        if (result.success) {
          this.mayiMissionDetail = result.data
        }
      }
    }
  }
</script>

<style scoped lang="less">
  .top {
    margin: 20px !important;

    .col {
      background: #e5e9f2;
      border-radius: 6px;
      height: 300px;
    }
  }
</style>
