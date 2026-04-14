<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { doctorIdService } from '@/api/doctor';
import { ElMessage, FormInstance, FormItemProps, FormProps, FormRules } from 'element-plus';
import { doctorInfoService } from '@/api/doctorInfo';

const labelPosition = ref<FormProps['labelPosition']>('right');
const itemLabelPosition = ref<FormItemProps['labelPosition']>('');
const formRef = ref<FormInstance>();

const officeInfo = ref({
    name: '',
    intro: ''
});

// 校验是否为正整数
const checkNum = (rule, value, callback) => {
    if (!/^[1-9]\d*$/.test(value)) {
        callback(new Error("工龄必须为正整数"));
    } else {
        callback(); // 验证通过  
    }
}

// 获取医生用户工号
const officeId = ref({id: ''});
const username = ref('12312312312')

// 获取科室列表
import { singleOfficeService } from '@/api/office';
const getOfficeInfo = async () => {
    let result;
    result = await doctorInfoService();
    username.value = result.data.doctor.user_id;
    officeId.value.id = result.data.office.office_id;
    result = await singleOfficeService({
        id: officeId.value.id
    });
    if (result.code == 0) {
        ElMessage.success("成功查询到科室信息");
        officeInfo.value.name = result.data.name;
        officeInfo.value.intro = result.data.intro;
    }
    else {
        ElMessage.success("科室信息查询失败");
    }
}
getOfficeInfo();


</script>

<template>
    <div style="padding: 20px;" class="updatePwd_container">

        <h1 class="title">我的科室</h1>

        <el-card class="getBackPwd-card">
            <!-- 表单 -->
            <el-form class="form" :label-position="labelPosition" label-width="auto" :model="officeInfo"
                ref="formRef" style="max-width: 600px">

                <el-form-item label="科室名称" :label-position="itemLabelPosition" prop="name" class="form_item">
                    <el-input v-model="officeInfo.name" clearable class="input" disabled />
                </el-form-item>

                <el-form-item label="科室介绍" :label-position="itemLabelPosition" prop="intro" class="form_item">
                    <el-input v-model="officeInfo.intro" clearable class="input"
                        type="textarea" :rows="5" disabled />
                </el-form-item>

            </el-form>
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

                .form-item-content {
                    display: flex;  
                    align-items: center; /* 垂直居中对齐 */  
                
                    .office_select {
                        width: 360px;
                        margin-right: 5px;
                    }
                }
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
</style>