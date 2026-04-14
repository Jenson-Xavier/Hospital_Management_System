<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { doctorIdService } from '@/api/doctor';
import { ElMessage, ElMessageBox, FormInstance, FormItemProps, FormProps, FormRules, TabsPaneContext } from 'element-plus';

const labelPosition = ref<FormProps['labelPosition']>('right');
const itemLabelPosition = ref<FormItemProps['labelPosition']>('');

// 可通过formRef.value访问表单实例
const formRef_username = ref<FormInstance>()
const formRef_oldPwd = ref<FormInstance>()

// 定义注册数据
const usernameUpdateData = ref({
    verifyCode: '',
    newPassword: '',
    rePassword: ''
})

const oldPwdUpdateData = ref({
    oldPassword: '',
    newPassword: '',
    rePassword: ''
})

// 获取患者用户名
const username = ref('12312312312')
import { patientInfoService } from '@/api/patient';
// 考量到安全性，后端不会返回密码信息，故密码为null
const getPatientInfo = async () => {
    let result = await patientInfoService();
    username.value = result.data.user_id;
}
getPatientInfo();

// tab
// 定义标签页的默认选中页
const selectedTabName = ref('oldPassword')

const handleClick = (tab: TabsPaneContext, event: Event) => {
    if (tab.paneName == "verifyCode") {
        usernameUpdateData.value = {
            verifyCode: '',
            newPassword: '',
            rePassword: ''
        }
    } else if (tab.paneName == "oldPassword") {
        oldPwdUpdateData.value = {
            oldPassword: '',
            newPassword: '',
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
    if (selectedTabName.value == "verifyCode") {
        if (value == '') {
            callback(new Error("请再次输入密码"));
        } else if (value != usernameUpdateData.value.newPassword) {
            callback(new Error("两次密码输入不一致"));
        } else {
            callback();
        }
    }
    else if (selectedTabName.value == "oldPassword") {
        if (value == '') {
            callback(new Error("请再次输入密码"));
        } else if (value != oldPwdUpdateData.value.newPassword) {
            callback(new Error("两次密码输入不一致"));
        } else {
            callback();
        }
    }
}

// 用户名验证码修改密码方式校验规则
const usernameRules = reactive<FormRules>({
    verifyCode: [
        { required: true, message: "请输入验证码", trigger: "blur" },
        { min: 6, max: 6, message: "长度为6位非空字符", trigger: "blur" }
    ],
    newPassword: [
        { required: true, message: "请输入密码", trigger: "blur" },
        { min: 8, message: "长度不得少于8位", trigger: "blur" },
        { validator: checkPwd, trigger: "blur" }
    ],
    rePassword: [
        { required: true, message: "请再次输入密码", trigger: "blur" },
        { validator: checkRePwd, trigger: "blur" }
    ]
})

// 原密码修改密码方式校验规则
const oldPwdRules = reactive<FormRules>({
    oldPassword: [
        { required: true, message: "原密码不得为空", trigger: "blur" },
    ],
    newPassword: [
        { required: true, message: "请输入密码", trigger: "blur" },
        { min: 8, message: "长度不得少于8位", trigger: "blur" },
        { validator: checkPwd, trigger: "blur" }
    ],
    rePassword: [
        { required: true, message: "请再次输入密码", trigger: "blur" },
        { validator: checkRePwd, trigger: "blur" }
    ]
})

/* 验证码发送区域 */
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

// 发送验证码按钮禁用标识
let isSendingCode = ref(false);
// 倒计时变量
let countdown = ref(0);

// 调用发送邮件验证码接口
import { sendEmailService } from '@/api/patient.js';

// 发送验证码，调验证码接口
const sendVerificationCode = async () => {
    // 判断用户名类型
    const username_type = identifyUsernameType(username.value);
    // 邮箱验证码方式
    if (username_type == "code_email") {
        // 调发送邮箱接口
        let result;
        result = await sendEmailService({
            username: username.value
        });
        if (result.code == 0) {
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

// 修改密码
// 更新密码
import { updatePwdService } from '@/api/patient';
import router from '@/router';
const updatePwd = async () => {
    let mode;
    let result;
    if (selectedTabName.value == "oldPassword") {
        // 表单准备提交进行校验
        await formRef_oldPwd.value.validate().catch(err => {
            ElMessage.error('修改失败(前端))')
            throw err
        })
        mode = "password";
        result = await updatePwdService({
            oldPassword: oldPwdUpdateData.value.oldPassword,
            newPassword: oldPwdUpdateData.value.newPassword,
            mode: mode
        })
    }
    else if (selectedTabName.value == "verifyCode") {
        // 表单准备提交进行校验
        await formRef_username.value.validate().catch(err => {
            ElMessage.error('修改失败(前端))')
            throw err
        })
        mode = identifyUsernameType(username.value);
        result = await updatePwdService({
            verifyCode: usernameUpdateData.value.verifyCode,
            newPassword: usernameUpdateData.value.newPassword,
            mode: mode
        })
    }
    if (result.code == 0) {
        ElMessage.success("成功修改密码，请重新登录");
        router.push("/patient_login");
    } else {
        ElMessage.error(result.message ? result.message : "修改失败");
    }
}


const dialog_goto_update = () => {
    let message = '你确定要修改密码吗？';
    ElMessageBox.confirm(
        message,
        '确认修改',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            ElMessage({
                type: 'success',
                message: '成功确认',
            });
            updatePwd();
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            });
        })
}

// 返回用户管理界面
const back = ()=>{
    router.push("/patient/user_management")
}

</script>

<template>
    <div style="padding: 20px;" class="updatePwd_container">

        <h1 class="title">修改密码</h1>

        <el-card class="getBackPwd-card">

            <el-tabs v-model="selectedTabName" type="border-card" class="demo-tabs" stretch="true"
                @tab-click="handleClick">

                <!-- 原密码方式身份核验 -->
                <el-tab-pane label="原密码方式身份核验" name="oldPassword">
                    <!-- 表单 -->
                    <el-form class="form" :label-position="labelPosition" label-width="auto" :model="oldPwdUpdateData"
                        :rules="oldPwdRules" ref="formRef_oldPwd" style="max-width: 600px">

                        <el-form-item label="原密码" :label-position="itemLabelPosition" prop="oldPassword">
                            <el-input v-model="oldPwdUpdateData.oldPassword" placeholder="请输入原密码" clearable
                                type="password" show-password />
                        </el-form-item>

                        <el-form-item label="设置密码" :label-position="itemLabelPosition" prop="newPassword">
                            <el-input v-model="oldPwdUpdateData.newPassword" placeholder="请设置新密码" clearable
                                type="password" show-password />
                        </el-form-item>

                        <el-form-item label="确认密码" :label-position="itemLabelPosition" prop="rePassword">
                            <el-input v-model="oldPwdUpdateData.rePassword" placeholder="请再次输入密码" clearable
                                type="password" show-password />
                        </el-form-item>


                        <el-form-item>
                            <el-button class="button" type="primary" auto-insert-space @click="dialog_goto_update">
                                修改
                            </el-button>
                        </el-form-item>

                    </el-form>
                </el-tab-pane>

                <!-- 验证码方式身份核验 -->
                <el-tab-pane label="验证码方式身份核验" name="verifyCode">

                    <!-- 表单 -->
                    <el-form class="form" :label-position="labelPosition" label-width="auto" :model="usernameUpdateData"
                        :rules="usernameRules" ref="formRef_username" style="max-width: 600px">

                        <el-form-item label="验证码" :label-position="itemLabelPosition" prop="verifyCode">
                            <el-input class="input" v-model="usernameUpdateData.verifyCode" placeholder="请输入验证码"
                                type="password" show-password>

                                <template #suffix>
                                    <el-button style="border: none" :disabled="isSendingCode || countdown > 0"
                                        @click="sendVerificationCode">
                                        {{ countdown > 0 ? `重新发送(${countdown})` : '发送验证码' }}
                                    </el-button>
                                </template>

                            </el-input>

                        </el-form-item>

                        <el-form-item label="设置密码" :label-position="itemLabelPosition" prop="newPassword">
                            <el-input v-model="usernameUpdateData.newPassword" placeholder="请设置新密码" clearable
                                type="password" show-password />
                        </el-form-item>

                        <el-form-item label="确认密码" :label-position="itemLabelPosition" prop="rePassword">
                            <el-input v-model="usernameUpdateData.rePassword" placeholder="请再次输入密码" clearable
                                type="password" show-password />
                        </el-form-item>

                        <el-form-item>
                            <el-button class="button" type="primary" auto-insert-space @click="dialog_goto_update">
                                修改
                            </el-button>
                        </el-form-item>

                    </el-form>

                </el-tab-pane>
            </el-tabs>

            <el-row class="layout_vertical">
                <el-button class="button" type="text" @click="back">返回用户信息管理界面</el-button>
            </el-row>

        </el-card>

    </div>
</template>



<style lang="scss" scoped>
.dialog_item_title {
    font-family: "微软雅黑";
    font-size: large;
    font-weight: 600;
}

.dialog_item_content {
    font-family: "微软雅黑";
    font-size: large;
    font-weight: 200;
}

.updatePwd_container {

    position: relative;
    top: 10%;
    display: flex;
    align-items: center;
    justify-content: center;

    .title {
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        font-size: 36px;
        font-weight: bold;
        color: #050613;
        // background-color: #eca452;
        margin-bottom: 30px;
    }

    .getBackPwd-card {
        position: absolute;
        top: 100px;
        width: 600px;
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

            .form_item {
                margin-bottom: 30px;
            }

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