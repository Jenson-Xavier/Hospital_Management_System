<script lang="ts" setup>
// form
import { reactive, ref } from 'vue'
import { ElMessage, FormInstance, FormRules, type FormItemProps, type FormProps, type TabsPaneContext } from 'element-plus'
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
const formRef_phone = ref<FormInstance>()
const formRef_email = ref<FormInstance>()

// 定义找回密码数据
const PhoneGetBackData = ref({
    username: '',
    verifyCode: '',
    password: '',
    rePassword: ''
})

const EmailGetBackData = ref({
    username: '',
    verifyCode: '',
    password: '',
    rePassword: ''
})

// tab
// 定义标签页的默认选中页
const selectedTabName = ref('phone')

const handleClick = (tab: TabsPaneContext, event: Event) => {
    // formRef.value.resetFields();
    if (tab.paneName == "phone") {
        PhoneGetBackData.value = {
            username: '',
            verifyCode: '',
            password: '',
            rePassword: ''
        }
    } else if (tab.paneName == "email") {
        EmailGetBackData.value = {
            username: '',
            verifyCode: '',
            password: '',
            rePassword: ''
        }
    }
    console.log(tab, event)
}

// 定义表单校验规则

// 校验密码函数
const checkPwd = (rule, value, callback) => {
    let hasUpperCase = false;
    let hasLowerCase = false;
    let hasDigit = false;

    for (let i = 0; i < value.length; i++) {
        const c = value.charAt(i);
        if (/[A-Z]/.test(c)) {          // 大写字母
            hasUpperCase = true;
        } else if (/[a-z]/.test(c)) {   // 小写字母
            hasLowerCase = true;
        } else if (/[0-9]/.test(c)) {   // 数字
            hasDigit = true;
        }
        if (hasUpperCase && hasLowerCase && hasDigit) {
            callback(); // Validation passed  
            return;
        }
    }
    callback(new Error("密码须同时包含大小写字母和数字"));
}

// 确认密码校验函数
const checkRePwd = (rule, value, callback) => {
    if (selectedTabName.value == "phone") {
        if (value == '') {
            callback(new Error("请再次输入密码"));
        } else if (value != PhoneGetBackData.value.password) {
            callback(new Error("两次密码输入不一致"));
        } else {
            callback();
        }
    }
    else if (selectedTabName.value == "email") {
        if (value == '') {
            callback(new Error("请再次输入密码"));
        } else if (value != EmailGetBackData.value.password) {
            callback(new Error("两次密码输入不一致"));
        } else {
            callback();
        }
    }
}

// 邮箱校验函数
const checkEmail = (rule, value, callback) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (value == '') {
        callback(new Error("请输入邮箱号"))
    }
    else if (!emailRegex.test(value)) {
        callback(new Error("请按正确邮箱格式输入邮箱号"));
    } else {
        callback();
    }
}

// 手机号找回密码方式校验规则
const phoneRules = reactive<FormRules>({
    username: [
        { required: true, message: "手机号不得为空", trigger: "blur" },
        { min: 11, max: 11, message: "长度为11位非空字符", trigger: "blur" }
    ],
    verifyCode: [
        { required: true, message: "请输入验证码", trigger: "blur" },
        { min: 6, max: 6, message: "长度为6位非空字符", trigger: "blur" }
    ],
    password: [
        { required: true, message: "请输入密码", trigger: "blur" },
        { min: 8, message: "长度不得少于8位", trigger: "blur" },
        { validator: checkPwd, trigger: "blur" }
    ],
    rePassword: [
        { required: true, message: "请再次输入密码", trigger: "blur" },
        { validator: checkRePwd, trigger: "blur" }
    ]
})

// 邮箱找回密码方式校验规则
const emailRules = reactive<FormRules>({
    username: [
        { required: true, message: "邮箱号不得为空", trigger: "blur" },
        { validator: checkEmail, trigger: "blur" }
    ],
    verifyCode: [
        { required: true, message: "请输入验证码", trigger: "blur" },
        { min: 6, max: 6, message: "长度为6位非空字符", trigger: "blur" }
    ],
    password: [
        { required: true, message: "请输入密码", trigger: "blur" },
        { min: 8, message: "长度不得少于8位", trigger: "blur" },
        { validator: checkPwd, trigger: "blur" }
    ],
    rePassword: [
        { required: true, message: "请再次输入密码", trigger: "blur" },
        { validator: checkRePwd, trigger: "blur" }
    ]
})


// 返回登录界面
import { useRouter } from 'vue-router'
const router = useRouter();
const backLogin = () => {
    router.push('/patient_login');
}

// 调用后台接口 完成找回密码
import { patientGetBackPwdService } from '@/api/patient.js'
const register = async () => {
    // 表单准备提交进行校验
    if (selectedTabName.value == "phone") {
        // 表单准备提交进行校验
        await formRef_phone.value.validate().catch(err => {
            ElMessage.error('找回密码失败(前端 手机))')
            throw err
        })
    } else if (selectedTabName.value == "email") {
        // 表单准备提交进行校验
        await formRef_email.value.validate().catch(err => {
            ElMessage.error('找回密码失败(前端 邮箱))')
            throw err
        })
    }
    // 正式发送找回密码请求
    let result;
    if (selectedTabName.value == "phone") {
        const params = PhoneGetBackData.value;
        params["getBack_mode"] = "phone";
        result = await patientGetBackPwdService(params);
    } else if (selectedTabName.value == "email") {
        const params = EmailGetBackData.value;
        params["getBack_mode"] = "email";
        result = await patientGetBackPwdService(params);
    }
    if (result.code == 0) {
        ElMessage.success(result.message ? result.message : "找回密码成功");
        router.push('/patient_login');
    } else {
        ElMessage.error(result.message ? result.message : "找回密码失败");
    }
}

// 发送验证码按钮禁用标识
let isSendingCode = ref(false);
// 倒计时变量
let countdown = ref(0);

// 调用发送邮件验证码接口
import { sendEmailService } from '@/api/patient.js';

// 发送验证码，调验证码接口
const sendVerificationCode = async () => {
    // 邮箱验证码方式
    if (selectedTabName.value == "email") {
        // 调发送邮箱接口
        let result;
        const params = new URLSearchParams();
        result = await sendEmailService(EmailGetBackData.value);
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
    else if (selectedTabName.value == "phone") {
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
    <el-row class="getBackPwd-container">

        <h1 class="title">医院预约挂号系统</h1>

        <el-card class="getBackPwd-card">
            <!-- 子标题 -->
            <el-row class="subtitle">
                <el-col :span="2">
                    <el-icon>
                        <User />
                    </el-icon>
                </el-col>
                <el-col :span="22">
                    <el-text class="subtitle">患者用户找回密码</el-text>
                </el-col>
            </el-row>

            <el-tabs v-model="selectedTabName" type="border-card" class="demo-tabs" stretch="true"
                @tab-click="handleClick">
                <!-- 手机号找回密码 -->
                <el-tab-pane label="短信验证码找回" name="phone">
                    <!-- 表单 -->
                    <el-form class="form" :label-position="labelPosition" label-width="auto" :model="PhoneGetBackData"
                        :rules="phoneRules" ref="formRef_phone" style="max-width: 600px">

                        <el-form-item label="用户名" :label-position="itemLabelPosition" prop="username">
                            <el-input v-model="PhoneGetBackData.username" placeholder="手机号" clearable/>
                        </el-form-item>

                        <el-form-item label="验证码" :label-position="itemLabelPosition" prop="verifyCode">
                            <el-input class="input" v-model="PhoneGetBackData.verifyCode" placeholder="请输入验证码"
                                type="password" show-password>

                                <template #suffix>
                                    <el-button style="border: none" :disabled="isSendingCode || countdown > 0"
                                        @click="sendVerificationCode">
                                        {{ countdown > 0 ? `重新发送(${countdown})` : '发送验证码' }}
                                        <!-- <span v-show="!show" class="count">{{ count }} s</span> -->
                                    </el-button>
                                </template>

                            </el-input>
                        </el-form-item>

                        <el-form-item label="设置密码" :label-position="itemLabelPosition" prop="password">
                            <el-input v-model="PhoneGetBackData.password" placeholder="请设置密码" type="password" show-password/>
                        </el-form-item>

                        <el-form-item label="确认密码" :label-position="itemLabelPosition" prop="rePassword">
                            <el-input v-model="PhoneGetBackData.rePassword" placeholder="请再次输入密码" type="password" show-password/>
                        </el-form-item>


                        <el-form-item>
                            <el-button class="button" type="primary" auto-insert-space @click="register" prop="tmp">
                                确认设置
                            </el-button>
                        </el-form-item>

                    </el-form>
                </el-tab-pane>
                <!-- 邮箱号找回密码 -->
                <el-tab-pane label="邮箱验证码找回" name="email">

                    <!-- 表单 -->
                    <el-form class="form" :label-position="labelPosition" label-width="auto" :model="EmailGetBackData"
                        :rules="emailRules" ref="formRef_email" style="max-width: 600px">

                        <el-form-item label="用户名" :label-position="itemLabelPosition" prop="username">
                            <el-input v-model="EmailGetBackData.username" placeholder="邮箱号" clearable/>
                        </el-form-item>

                        <el-form-item label="验证码" class="input" :label-position="itemLabelPosition" prop="verifyCode">
                            <el-input v-model="EmailGetBackData.verifyCode" placeholder="请输入验证码" type="password" show-password>

                                <template #suffix>
                                    <el-button style="border: none" :disabled="isSendingCode || countdown > 0"
                                        @click="sendVerificationCode">
                                        {{ countdown > 0 ? `重新发送(${countdown})` : '发送验证码' }}
                                        <!-- <span v-show="!show" class="count">{{ count }} s</span> -->
                                    </el-button>
                                </template>

                            </el-input>

                        </el-form-item>

                        <el-form-item label="设置密码" :label-position="itemLabelPosition" prop="password">
                            <el-input v-model="EmailGetBackData.password" placeholder="请设置密码" type="password" show-password/>
                        </el-form-item>

                        <el-form-item label="确认密码" :label-position="itemLabelPosition" prop="rePassword">
                            <el-input v-model="EmailGetBackData.rePassword" placeholder="请再次输入密码" type="password" show-password/>
                        </el-form-item>

                        <el-form-item>
                            <el-button class="button" type="primary" auto-insert-space @click="register" prop="tmp">
                                确认设置
                            </el-button>
                        </el-form-item>

                    </el-form>

                </el-tab-pane>
            </el-tabs>

            <el-button class="button" type="text" @click="backLogin">返回登录界面</el-button>

        </el-card>
    </el-row>
</template>

<style lang="scss" scoped>
.getBackPwd-container {
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
        font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif;
        font-size: 20px;
        color: #1e226b;
        margin-bottom: 20px;
    }

    .getBackPwd-card {
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

    .button {
        width: 100%;
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