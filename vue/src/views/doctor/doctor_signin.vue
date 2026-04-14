<script lang="ts" setup>
// form
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormItemProps, type FormProps, type FormRules, type TabsPaneContext } from 'element-plus'
import {
    Check,
    Delete,
    Edit,
    Message,
    Search,
    Star,
    User,
} from '@element-plus/icons-vue'

const labelPosition = ref<FormProps['labelPosition']>('right')
const itemLabelPosition = ref<FormItemProps['labelPosition']>('')

// 可通过formRef.value访问表单实例
const formRef = ref<FormInstance>()


// 定义登录数据
const LoginData = ref({
    username: '',
    password: ''
})

// 定义表单校验规则
const Rules = reactive<FormRules>({
    username: [
        { required: true, message: "用户名不得为空", trigger: "blur" },
    ],
    password: [
        { required: true, message: "密码或验证码不得为空", trigger: "blur" },
    ],
})

// 拼图显示标识
const visible = ref(false);

import { useTokenStore } from '@/stores/token';
const tokenStore = useTokenStore();
// 登录按钮点击事件
import { doctorLoginService } from '@/api/doctor.js'
const login = async () => {
    // 对表单进行校验
    await formRef.value?.validate().catch(err => {
        ElMessage.error('登录失败(前端)')
        throw err
    })
    // 正式发送登录请求
    let result;
    result = await doctorLoginService(LoginData.value);
    if (result.code == 0) {
        ElMessage.success(result.message ? result.message : "登录成功");
        tokenStore.setToken(result.data);   // token
        visible.value = true;

    } else {
        ElMessage.error(result.message ? result.message : "登录失败");
    }
}

import { useRouter } from 'vue-router'
const router = useRouter();

// 真人识别验证码

// 引入依赖对象
import SlideVerify from "vue3-slide-verify";
// 引入依赖样式
import "vue3-slide-verify/dist/style.css";

// 拼图的图片资源 可为空，此时会自动下载使用随机网络图片
const images = reactive([
    'https://t7.baidu.com/it/u=2609096218,1652764947&fm=193&f=GIF',
    'https://t7.baidu.com/it/u=2541348729,1193227634&fm=193&f=GIF',
    'https://t7.baidu.com/it/u=2673836711,2234057813&fm=193&f=GIF',
])

// 拼图验证成功回调
const onSuccess = () => {
    ElMessage.success("验证成功！");
    // 隐藏拼图
    visible.value = false;
    // 执行跳转
    // ...
    router.push("/doctor/home")
}

// 拼图验证失败回调
const onFail = () => {
    ElMessage.error("验证不通过!")
}

</script>

<template>
    <el-row class="login-container">

        <h1 class="title">医院预约挂号系统</h1>

        <el-card class="login-card" v-if="!visible">
            <!-- 子标题 -->
            <el-row class="subtitle">
                <el-col :span="2">
                    <el-icon>
                        <User />
                    </el-icon>
                </el-col>
                <el-col :span="22">
                    <el-text class="subtitle">医生用户登录</el-text>
                </el-col>
            </el-row>

            <!-- 表单 -->
            <el-form class="form" :label-position="labelPosition" label-width="auto" :model="LoginData"
                :rules="Rules" ref="formRef" style="max-width: 600px">

                <el-form-item label="用户名" :label-position="itemLabelPosition" prop="username">
                    <el-input v-model="LoginData.username" placeholder="请输入工号" clearable/>
                </el-form-item>

                <el-form-item label="密码" :label-position="itemLabelPosition" prop="password">
                    <el-input v-model="LoginData.password" placeholder="请输入密码" type="password" show-password/>
                </el-form-item>

                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="login">
                        登录
                    </el-button>
                </el-form-item>

            </el-form>

        </el-card>

        <!--拼图部分-->
        <el-card v-if="visible">
            <slide-verify ref="slide" slider-text="向右滑动->" :imgs="images" @success="onSuccess" @fail="onFail"
                style="margin: auto;"></slide-verify>
        </el-card>
        <!-- v-if="visible" -->
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