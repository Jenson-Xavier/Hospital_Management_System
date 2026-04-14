<script setup lang="js">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, House, Calendar, ChatSquare } from '@element-plus/icons-vue'
import { useTokenStore } from '@/stores/token.js';
import { ElMessage, ElMessageBox } from 'element-plus'
import { patientInfoService } from '@/api/patient.js'

const router = useRouter()
const isCollapse = ref(false)

// 获取患者用户个人信息数据
const username = ref('13800138000')
const password = ref('123456')

const getPatientInfo = async () => {
  let result = await patientInfoService();
  username.value = result.data.user_id;
  password.value = result.data.password;
}
getPatientInfo();
const tokenStore = useTokenStore();

const handleCommand = (command) => {
  if (command === 'logout') {
    //退出登录
    ElMessageBox.confirm(
      '您确认要退出吗?',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
      .then(async () => {
        //退出登录 清空token
        tokenStore.removeToken()

        //跳转到登录页面
        router.push('/patient_login')
        ElMessage({
          type: 'success',
          message: '退出登录成功',
        })

      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: '用户取消了退出登录',
        })
      })
  }
}
</script>

<template>
  <el-container class="layout-container">
    <el-header height="60px" class="header">
      <div class="header-left">
        <el-icon color="#1ad8c2">
          <House />
        </el-icon>
        <span class="text" :style="{ fontWeight: 'bold' }"><router-link to="/patient/home">医院预约挂号系统</router-link></span>
      </div>
      <el-dropdown placement="bottom" @command="handleCommand">
        <span class="el-dropdown__box">
          <el-icon color="#1ad8c2" size="large" style="margin-right: 10px;">
            <UserFilled />
          </el-icon>
          <span class="text">您好，用户<span :style="{ color: '#138ce4' }"> {{ username }} </span></span>
          <el-icon>
            <CaretBottom />
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-header>

    <el-container class="el-menu-content">
      <el-aside width="200px">
        <el-menu router :default-active="$route.path" class="el-menu-vertical" :collapse="isCollapse" text-color="#ffffff"
          active-text-color="#f9f96f" background-color="#16c5b1">
          <el-menu-item index="/patient/home">
            <el-icon>
              <House />
            </el-icon>
            <span>用户首页</span>
          </el-menu-item>

          <el-sub-menu index="2">
            <template #title>
              <el-icon>
                <Calendar />
              </el-icon>
              <span>预约管理</span>
            </template>
            <el-menu-item index="/patient/appointment/by_doctor">按医生预约</el-menu-item>
            <el-menu-item index="/patient/appointment/by_office">按科室预约</el-menu-item>
            <el-menu-item index="/patient/appointment/my">我的预约</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <el-icon>
                <ChatSquare />
              </el-icon>
              <span>评价社区</span>
            </template>
            <el-menu-item index="/patient/comment/all">所有评价</el-menu-item>
            <el-menu-item index="/patient/comment/my">我的评价</el-menu-item>
            <el-menu-item index="/patient/comment/add">新增评价</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/patient/user_management">
            <el-icon>
              <User />
            </el-icon>
            <span>账户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main_content">
        <router-view>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  width: 100vw;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;


  .main_content {
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
  }

  .el-header {
    background-color: #ffffff;
    color: black;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .el-dropdown__box {
      display: flex;
      align-items: center;
      color: black;

      .el-icon {
        color: #999;
        margin-left: 10px;
      }

      &:active,
      &:focus {
        outline: none;
      }
    }
  }

  .el-menu-vertical {
    height: 100%;
  }

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 18px;
  }

  .header-right {
    color: white;
  }

  .text {
    font-family: '微软雅黑';
  }

  .el-dropdown-link {
    cursor: pointer;
    color: white;
    display: flex;
    align-items: center;
  }

  :deep(.el-main) {
    --el-main-padding: 20px;
    background-color: #f0f2f5;
  }
}
</style>