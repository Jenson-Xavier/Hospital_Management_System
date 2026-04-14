<script lang="ts" setup>
// form
import { type FormInstance, type FormItemProps, type FormProps } from 'element-plus'
import { ref } from 'vue'

const labelPosition = ref<FormProps['labelPosition']>('right')
const itemLabelPosition = ref<FormItemProps['labelPosition']>('')

// 可通过formRef.value访问表单实例
const formRef = ref<FormInstance>()

const searchForm = ref({
    user_type: ''
});

// 用户身份映射
const UserTypeMapping = ref({
    1: "管理员",
    2: "医生用户",
    3: "患者用户"
})

// 注册按钮点击事件
import { useRouter } from 'vue-router'
const router = useRouter();

const goto_login_page = () => {
    if (searchForm.value.user_type == "管理员") {
        router.push("/admin_login");
    }
    else if (searchForm.value.user_type == "医生用户") {
        router.push("/doctor_login");
    }
    else if (searchForm.value.user_type == "患者用户") {
        router.push("/patient_login");
    }
}

</script>

<template>
    <el-row class="login-container">

        <h1 class="title">医院预约挂号系统</h1>

        <el-card class="login-card">
            <!-- 子标题 -->
            <el-row class="subtitle">
                <el-col :span="2">
                    <el-icon>
                        <User />
                    </el-icon>
                </el-col>
                <el-col :span="22">
                    <el-text class="subtitle">用户身份选择</el-text>
                </el-col>
            </el-row>

            <!-- 搜索和过滤栏 -->
            <el-form :label-position="labelPosition" label-width="auto" :model="searchForm" class="form"
                style="max-width: 600px">
                <el-form-item label="用户身份">
                    <el-select v-model="searchForm.user_type" placeholder="选择用户类型" clearable
                        @clear="searchForm.user_type = ''">
                        <el-option v-for="(item, index) in UserTypeMapping" :key="index" :label="item"
                            :value="item"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="goto_login_page" auto-insert-space class="button">前往登录</el-button>
                </el-form-item>
            </el-form>
        </el-card>

    </el-row>


</template>

<style lang="scss" scoped>
.login-container {
    width: 100vw;
    height: 100vh;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    align-items: center;
    justify-content: center;
    background-image: url('/src/assets/imgs/hos_bg.png'); // 替换为你的背景图路径 
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;

    .title {
        position: absolute;
        top: 10%;
        left: 50%;
        transform: translateX(-50%);
        font-size: 36px;
        font-weight: bold;
        color: #050613;
        background-color: #eca452;
        margin-bottom: 30px;
    }

    .subtitle {
        font-size: 20px;
        color: #1e226b;
        margin-bottom: 20px;
    }

    .login-card {
        width: 400px;
        flex-grow: 0;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        background-color: white;


        .form {
            display: flex;
            flex-direction: column;
            justify-content: center;
            user-select: none;

            // 登录按钮 撑满
            .button {
                width: 100%;
            }

            .flex {
                width: 100%;
                display: flex;
                justify-content: space-between;
            }

        }

    }

}




.demo-tabs>.el-tabs__content {
    padding: 32px;
    color: #6b778c;
    font-size: 32px;
    font-weight: 600;
}

.el-row:last-child {
    margin-bottom: 0;
}

.el-col {
    border-radius: 4px;
}

:deep {
    .el-input__suffix {
        .el-input__suffix-inner .el-button {
            color: #00aaf8; // 修改el-button字体颜色
        }

        &-inner {
            flex-direction: row-reverse;
            -webkit-flex-direction: row-reverse;
            display: flex;
        }
    }
}

.input :deep .el-input__wrapper {
    padding-right: 1px;
}
</style>