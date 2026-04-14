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
const accountLoginData = ref({
    username: '',
    password: ''
})

const verifyLoginData = ref({
    username: '',
    password: '',
})

// tab
// 定义标签页的默认选中页
const selectedTabName = ref('account')

const handleClick = (tab: TabsPaneContext, event: Event) => {
    // formRef.value.resetFields();
    if (tab.paneName == "account") {
        accountLoginData.value = {
            username: '',
            password: ''
        }
    } else if (tab.paneName == "verify_code") {
        verifyLoginData.value = {
            username: '',
            password: ''
        }
    }
    console.log(tab, event)
}

// 定义表单校验规则
const Rules = reactive<FormRules>({
    username: [
        { required: true, message: "用户名不得为空", trigger: "blur" },
    ],
    password: [
        { required: true, message: "密码或验证码不得为空", trigger: "blur" },
    ],
})

// 函数：识别用户名类别
const identifyUsernameType = (username) => {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phonePattern = /^\d{11}$/; // 假设手机号为11位数字  

    if (emailPattern.test(username)) {
        return 'code_email';
    } else if (phonePattern.test(username)) {
        return 'code_phone';
    } else {
        return 'unknown';
    }
}

// 拼图显示标识
const visible = ref(false);

import { useTokenStore } from '@/stores/token';
const tokenStore = useTokenStore();
// 登录按钮点击事件
import { patientLoginService } from '@/api/patient.js'
const login = async () => {
    // 对表单进行校验
    await formRef.value?.validate().catch(err => {
        ElMessage.error('登录失败')
        throw err
    })
    // 正式发送登录请求
    let result;
    // 请求参数
    const params = new URLSearchParams();
    // 账号密码方式登录
    if (selectedTabName.value == "account") {
        params.append("username", accountLoginData.value.username);
        params.append("password", accountLoginData.value.password);
        params.append("login_mode", "password");
        result = await patientLoginService(params);
    }
    // 验证码方式登录
    else if (selectedTabName.value == "verify_code") {
        params.append("username", verifyLoginData.value.username);
        params.append("password", verifyLoginData.value.password);
        const username_type = identifyUsernameType(verifyLoginData.value.username);
        // 参数
        params.append("login_mode", username_type);
        result = await patientLoginService(params);
    }
    if (result.code == 0) {
        ElMessage.success(result.message ? result.message : "登录成功");
        tokenStore.setToken(result.data);   // token
        visible.value = true;

    } else {
        ElMessage.error(result.message ? result.message : "登录失败");
    }
}

// 注册按钮点击事件
import { useRouter } from 'vue-router'
const router = useRouter();
const register = () => {
    router.push('/register');
}

// 忘记密码按钮
const forgetPwd = () => {
    router.push('/forgetPwd');
}

// 真人识别验证码

// 引入依赖对象
import SlideVerify from "vue3-slide-verify";
// 引入依赖样式
import "vue3-slide-verify/dist/style.css";
import request from '@/utils/request.js'

// 拼图的图片资源 可为空，此时会自动下载使用随机网络图片
const images = reactive([
    'https://t7.baidu.com/it/u=2609096218,1652764947&fm=193&f=GIF',
    'https://t7.baidu.com/it/u=2541348729,1193227634&fm=193&f=GIF',
    'https://t7.baidu.com/it/u=2673836711,2234057813&fm=193&f=GIF',
])

// 拼图验证成功回调
const onSuccess = () => {
    ElMessage.success("验证成功");
    // 隐藏拼图
    visible.value = false;
    // 执行跳转
    // ...
    router.push('/patient/home')
}

// 拼图验证失败回调
const onFail = () => {
    ElMessage.error("验证不通过")
}

// 发送验证码按钮禁用标识
let isSendingCode = ref(false);
// 倒计时变量
let countdown = ref(0);

// 调用发送邮件验证码接口
import { sendEmailService } from '@/api/patient.js';

// 发送验证码，调验证码接口
const sendVerificationCode = async () => {
    // 判断用户名类型
    const username_type = identifyUsernameType(verifyLoginData.value.username);
    // 邮箱验证码方式
    if (username_type == "code_email") {
        // 调发送邮箱接口
        let result;
        const params = new URLSearchParams();
        result = await sendEmailService(accountLoginData.value);
        if (result.code == 0) {
            ElMessage.success(result.message ? result.message : "验证码发送成功");
            isSendingCode.value = true;
            // 设置倒计时时间，这里假设为10秒
            countdown.value = 10;

            // 倒计时效果
            const countdownInterval = setInterval(() => {
                countdown.value--;
                if (countdown.value <= 0) {
                    clearInterval(countdownInterval);
                    isSendingCode.value = false;
                }
            }, 1000);
        } else {
            ElMessage.error(result.message ? result.message : "验证码发送失败");
        }
    }
    // 短信验证码方式
    else if (username_type == "code_phone") {
        ElMessage.success("验证码发送成功");
        isSendingCode.value = true;
        // 设置倒计时时间，这里假设为10秒
        countdown.value = 10;

        // 倒计时效果
        const countdownInterval = setInterval(() => {
            countdown.value--;
            if (countdown.value <= 0) {
                clearInterval(countdownInterval);
                isSendingCode.value = false;
            }
        }, 1000);
    }
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
                    <el-text class="subtitle">患者用户登录</el-text>
                </el-col>
            </el-row>

            <el-tabs v-model="selectedTabName" type="border-card" class="demo-tabs" stretch="true"
                @tab-click="handleClick">
                <!-- 账号密码登录 -->
                <el-tab-pane label="账号密码登录" name="account">
                    <!-- 表单 -->
                    <el-form class="form" :label-position="labelPosition" label-width="auto" :model="accountLoginData"
                        :rules="Rules" ref="formRef" style="max-width: 600px">

                        <el-form-item label="用户名" :label-position="itemLabelPosition" prop="username">
                            <el-input v-model="accountLoginData.username" placeholder="手机号/邮箱号" clearable/>
                        </el-form-item>

                        <el-form-item label="密码" :label-position="itemLabelPosition" prop="password">
                            <el-input v-model="accountLoginData.password" placeholder="请输入密码" type="password" show-password/>
                        </el-form-item>

                        <el-form-item>
                            <div class="flex">
                                <el-checkbox>记住密码</el-checkbox>
                                <el-link type="primary" :underline="false" @click="forgetPwd">忘记密码？</el-link>
                            </div>
                        </el-form-item>

                        <el-form-item>
                            <el-button class="button" type="primary" auto-insert-space @click="login">
                                登录
                            </el-button>
                        </el-form-item>

                        <el-form-item>
                            <el-button class="button" type="text" @click="register">还没有账号?注册一个</el-button>
                        </el-form-item>

                    </el-form>
                </el-tab-pane>
                <!-- 验证码登录 -->
                <el-tab-pane label="验证码登录" name="verify_code">

                    <!-- 表单 -->
                    <el-form class="form" :label-position="labelPosition" label-width="auto" :model="verifyLoginData"
                        :rules="Rules" style="max-width: 600px">

                        <el-form-item label="用户名" :label-position="itemLabelPosition" prop="username">
                            <el-input v-model="verifyLoginData.username" placeholder="手机号/邮箱号" clearable/>
                        </el-form-item>

                        <el-form-item label="验证码" class="input" :label-position="itemLabelPosition" prop="password">
                            <el-input v-model="verifyLoginData.password" placeholder="请输入验证码" type="password" show-password>

                                <template #suffix>
                                    <el-button style="border: none" :disabled="isSendingCode || countdown > 0"
                                        @click="sendVerificationCode">
                                        {{ countdown > 0 ? `重新发送(${countdown})` : '发送验证码' }}
                                        <!-- <span v-show="!show" class="count">{{ count }} s</span> -->
                                    </el-button>
                                </template>

                            </el-input>

                        </el-form-item>

                        <el-form-item>
                            <el-button class="button" type="primary" auto-insert-space @click="login">
                                登录
                            </el-button>
                        </el-form-item>

                        <el-form-item>
                            <el-button class="button" type="text" @click="register">还没有账号?注册一个</el-button>
                        </el-form-item>

                    </el-form>

                </el-tab-pane>
            </el-tabs>
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




.demo-tabs>.el-tabs__content {
    padding: 32px;
    color: #6b778c;
    font-size: 32px;
    font-weight: 600;
}

// e-row
// .el-row {
//     margin-bottom: 20px;
// }

.el-row:last-child {
    margin-bottom: 0;
}

.el-col {
    border-radius: 4px;
}

// .grid-content {
//     border-radius: 4px;
//     min-height: 36px;
// }


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