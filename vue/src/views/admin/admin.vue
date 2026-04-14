<script setup lang="js">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, House, Calendar, ChatSquare } from '@element-plus/icons-vue'
import { useTokenStore } from '@/stores/token.js';
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const isCollapse = ref(false)

// 获取患者用户个人信息数据
const username = ref('13800138000')

import { adminIdService } from '@/api/admin';
const geAdminId = async () => {
  let result = await adminIdService();
  username.value = result.data.user_id;
  console.log(username.value);
}
geAdminId();
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
        router.push('/admin_login')
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
        <span class="text" :style="{ fontWeight: 'bold' }">医院预约挂号系统</span>
      </div>
      <el-dropdown placement="bottom" @command="handleCommand">
        <span class="el-dropdown__box">
          <el-icon color="#1ad8c2" size="large" style="margin-right: 10px;"><UserFilled /></el-icon>
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

    <el-container>
      <el-aside width="200px">
        <el-menu router :default-active="$route.path"  class="el-menu-vertical" :collapse="isCollapse" text-color="#ffffff"
          active-text-color="#f9f96f" background-color="#16c5b1">
          <el-menu-item index="/admin/home">
            <el-icon><HomeFilled /></el-icon>
            <span>管理员首页</span>
          </el-menu-item>

          <el-menu-item index="/admin/patient_account">
            <el-icon><UserFilled /></el-icon>
            <span>患者账户管理</span>
          </el-menu-item>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><Avatar /></el-icon>
              <span>医生账户管理</span>
            </template>
            <el-menu-item index="/admin/add_doctor">添加医生账户</el-menu-item>
            <el-menu-item index="/admin/del_doctor">删除医生账户</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <el-icon><House /></el-icon>
              <span>科室管理</span>
            </template>
            <el-menu-item index="/admin/add_office">添加科室</el-menu-item>
            <el-menu-item index="/admin/office_manage">删除/修改科室信息</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="4">
            <template #title>
              <el-icon><Edit /></el-icon>
              <span>医生信息管理</span>
            </template>
            <el-menu-item index="/admin/add_doctorInfo">更改医生信息</el-menu-item>
            <el-menu-item index="/admin/del_doctorInfo">删除医生信息</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/schedule_manage">
            <el-icon>
              <Calendar />
            </el-icon>
            <span>排班信息管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/appointment_manage">
            <el-icon><Memo /></el-icon>
            <span>预约记录管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/comment_manage">
            <el-icon><Comment /></el-icon>
            <span>评论管理</span>
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

  .main_content {
    // background-image: url('/src/assets/imgs/hos_bg.png');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
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