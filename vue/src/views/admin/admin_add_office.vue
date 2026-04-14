<script lang="ts" setup>
import { ElMessage, FormInstance, FormItemProps, FormProps, FormRules } from 'element-plus';
import { reactive, ref } from 'vue';

const labelPosition = ref<FormProps['labelPosition']>('right');
const itemLabelPosition = ref<FormItemProps['labelPosition']>('');
const formRef = ref<FormInstance>();

const OfficeForm = ref({
    name: '',
    intro: '',
})

// 获取全部科室信息
import { officeAllService } from '@/api/office';
// 获取科室数据
const officeInfoList = ref([]);
const getOfficeInfo = async () => {
    let result;
    result = await officeAllService();
    if (result.code == 0) {
        officeInfoList.value = result.data;
    } else {
        ElMessage.error(result.message ? result.message : "科室信息获取失败");
    }
}
getOfficeInfo();

// 科室表单校验规则
// 科室名称唯一
const checkUnique = (rule, value, callback) => {
    const isUnique = !officeInfoList.value.some(item => item.name === value);
    console.log(officeInfoList.value);
    if (isUnique) {
        callback(); // 校验通过  
    } else {
        callback(new Error('科室名称已存在，不可重名')); // 校验失败  
    }
};

const Rules = reactive<FormRules>({
    name: [
        { required: true, message: "科室名称不得为空", trigger: "blur" },
        { validator: checkUnique, trigger: "blur" },
    ],
    intro: [
        { required: true, message: "科室介绍信息不得为空", trigger: "blur" },
    ]
})

// 添加科室
import { updateOfficeService } from '@/api/office';
import router from '@/router';
const addOffice = async () => {
    // 表单校验
    // 对表单进行校验
    await formRef.value?.validate().catch(err => {
        ElMessage.error('登录失败(前端)')
        throw err
    })
    let result;
    result = await updateOfficeService({
        name: OfficeForm.value.name,
        intro: OfficeForm.value.intro,
        mode: "add"
    });
    if (result.code == 0) {
        ElMessage.success("成功添加科室信息");
        getOfficeInfo();
        router.push("/admin/office_manage")
    } else {
        ElMessage.error(result.message ? result.message : "添加失败");
    }
}

</script>

<template>
    <div style="padding: 20px;" class="addDoctor_container">

        <h1 class="title">添加科室</h1>

        <el-card class="card">
            <!-- 表单 -->
            <el-form class="form" :label-position="labelPosition" label-width="auto" :model="OfficeForm" ref="formRef" :rules="Rules"
                style="max-width: 600px">

                <el-form-item label="科室名称" :label-position="itemLabelPosition" prop="name" class="form_item">
                    <el-input v-model="OfficeForm.name" placeholder="请输入科室名称" clearable class="input" />
                </el-form-item>

                <el-form-item label="科室介绍" :label-position="itemLabelPosition" prop="intro" class="form_item">
                    <el-input v-model="OfficeForm.intro" placeholder="请输入科室介绍信息" clearable class="input"  type="textarea" :rows="6"/>
                </el-form-item>

                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="addOffice" prop="tmp">
                        确认添加
                    </el-button>
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

.addDoctor_container {

    position: relative;
    top: 10%;
    // 设置子组件水平居中
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
        margin-bottom: 30px;
    }

    .card {
        position: absolute;
        top: 100px;
        width: 600px;
        flex-grow: 0;
        padding: 20px;
        // 设置边框弧度
        border-radius: 10px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        background-color: white;

        .form {
            display: flex;
            // 设置垂直居中
            flex-direction: column;
            justify-content: center;
            user-select: none;

            .form_item {
                margin-bottom: 30px;

                .form-item-content {
                    display: flex;
                    align-items: center;
                    /* 垂直居中对齐 */

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