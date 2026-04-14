<script setup>
import router from '@/router';
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { patientInfoService } from '@/api/patient.js';
import { useTokenStore } from '@/stores/token.js';

// 获取患者用户个人信息数据
const username = ref('13800138000')
const password = ref('123456')

const updatePassword = () => {
    router.push("/patient/updatePwd");
};

const logout = () => {
    let tokenStore = useTokenStore();
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
};

const closeAccount = () => {
    router.push("/patient/del_account");
};

onMounted(async () => {
    let result = await patientInfoService();
    username.value = result.data.user_id;
    password.value = result.data.password;
});

</script>

<template>
    <div class="home-container">
        <el-row class="content">
            <el-col :span="24">
                <el-card class="box-card">
                    <el-row style="margin-left: 20px;" class="layout_vertical">
                        <el-col class="layout_vertical">
                            <span class="welcome_text">您好，用户 <span :style="{ color: '#138ce4' }">{{ username }}</span>
                                !</span>
                            <div style="margin-top: 10px;" class="hint_text">
                                欢迎访问用户管理页面！
                            </div>
                        </el-col>
                    </el-row>
                </el-card>
            </el-col>

            <el-col :span="24">
                <el-card class="box-card" style="height: 240pt;">
                    <el-row  class="layout_vertical">
                        <el-col>
                            <el-button type="success" style="margin-top: 20px;" size="large" class="button"
                                @click="updatePassword">
                                <span>修改密码</span>
                            </el-button>
                        </el-col>
                        <el-col>
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="button"
                                @click="logout">
                                <span>退出登录</span>
                            </el-button>
                        </el-col>
                        <el-col>
                            <el-button type="danger" style="margin-top: 20px;" size="large" class="button"
                                @click="closeAccount">
                                <span>注销账户</span>
                            </el-button>
                        </el-col>
                    </el-row>

                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<style lang="scss" scoped>
.home-container {
    position: relative;
    width: 100%;
    height: 100%;
    top: 50%;
    left: 60%;
    transform: translate(-50%, -50%);
    display: flex;
    align-items: center;
    justify-content: center;

    .content {
        width: 60%;

        .card-header {
            position: absolute;
            /* display: flex; */
            justify-content: space-between;
            align-items: center;
        }

        .box-card {
            background: rgba(218, 228, 236, 0.8);
            margin-bottom: 25px;
            width: 60%;
            height: 25vh;
            padding: 20px;
        }
    }
}

.image_layout {
    display: flex;
    align-items: center;
    justify-content: center;
}

.welcome_text {
    font-size: large;
    font-family: "微软雅黑";
    font-weight: 800;
}

.hint_text {
    font-size: large;
    font-family: "仿宋";
    font-weight: 200;
}

.button {
    font-size: large;
    font-family: "微软雅黑";
    width: 200px;
}

.layout_horizon {
    display: flex;
    align-items: center;
    justify-content: center;
}

.layout_vertical {
    display: flex;
    flex-direction: column;
    align-items: center;
    /* 水平居中 */
    justify-content: center;
    /* 垂直居中 */
}
</style>