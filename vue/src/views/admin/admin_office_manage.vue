<script lang="ts" setup>
import router from '@/router';
import { ElMessage, ElMessageBox, FormInstance, FormItemProps, FormProps, FormRules, useDisabled } from 'element-plus';
import { ref, computed, onMounted, reactive } from 'vue';

// 搜索表单
const searchForm = ref({
    office_name: ''
});


// 获取全部科室信息
import { officeAllService } from '@/api/office';
// 获取科室数据
const officeInfoList = ref([]);
// 科室名称=>科室id 映射
const nameToOfficeIdMap = ref([]);
const getOfficeInfo = async () => {
    let result;
    result = await officeAllService();
    if (result.code == 0) {
        officeInfoList.value = result.data;
        nameToOfficeIdMap.value = officeInfoList.value.reduce((acc, item) => {
            acc[item.name] = item.office_id;
            return acc;
        }, {});
    } else {
        ElMessage.error(result.message ? result.message : "科室信息获取失败");
    }
}
getOfficeInfo();

// 当前页码
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(officeInfoList.value.length);

// 过滤后的评价列表
const filteredOffices = computed(() => {
    let result;
    result = officeInfoList.value.filter(office => {
        return (searchForm.value.office_name === '' || office.name === searchForm.value.office_name);
    });
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});



/* 对话框显示区 */
// 科室简介详情显示对话框
const selectedIntro = ref(null);
const introDialogVisible = ref(false);
const showIntroDetails = (intro) => {
    selectedIntro.value = intro;
    introDialogVisible.value = true;
};

const closeIntroDialog = () => {
    introDialogVisible.value = false;
    selectedIntro.value = null;
}

// 科室信息查看对话框
const selectedOffice = ref(null);
const officeDialogVisible = ref(false);
const showOffice = (officeInfo) => {
    selectedOffice.value = officeInfo;
    officeDialogVisible.value = true;
};

const closeOfficeDialog = () => {
    officeDialogVisible.value = false;
    selectedOffice.value = null;
}


// 日期选择框更改
const ElementChange = () => {
    currentPage.value = 1;
}


/* 逻辑功能实现区 */
import { deleteOfficeService } from '@/api/office';
// 跳转到新增科室界面
const addOffice = async () => {
    router.push("/admin/add_office")
}

// 删除科室功能
const deleteOffice = async (OfficeId) => {
    let result;
    result = await deleteOfficeService({
        id: OfficeId
    });
    if (result.code == 0) {
        ElMessage.success("已删除该科室");
        getOfficeInfo();
    } else {
        ElMessage.error(result.message ? result.message : "删除科室失败");
    }
}

// 删除医生信息
import { deleteDoctorInfoByOfficeIdService } from '@/api/doctorInfo';
const deleteDoctorInfo = async (OfficeId) => {
    let result;
    result = await deleteDoctorInfoByOfficeIdService({
        id: OfficeId
    });
    if (result.code == 0) {
        ElMessage.success("已删除科室对应的医生信息");
    } else {
        ElMessage.error(result.message ? result.message : "删除医生信息失败");
    }
}

// 删除排班信息
import { deleteScheduleByOfficeIdService } from '@/api/schedule';
const deleteSchedule = async (OfficeId) => {
    let result;
    result = await deleteScheduleByOfficeIdService({
        id: OfficeId
    });
    if (result.code == 0) {
        ElMessage.success("已删除科室对应的排班信息");
    } else {
        ElMessage.error(result.message ? result.message : "删除排班信息失败");
    }
}

const dialog_goto_del = (OfficeId) => {
    let message = '你确定要删除该评论信息吗？';
    ElMessageBox.confirm(
        message,
        '确认删除',
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
            // 由于jpa级联删除无法实现，故需要在删除科室信息前，手动根据科室id删除医生信息和排班信息
            deleteDoctorInfo(OfficeId);
            // deleteSchedule(OfficeId);
            deleteOffice(OfficeId);
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            });
        })
}

// 修改科室信息
const labelPosition = ref<FormProps['labelPosition']>('right')
const itemLabelPosition = ref<FormItemProps['labelPosition']>('')
// 修改科室信息表单
const formRef = ref<FormInstance>();

// 科室表单校验规则
// 定义表单校验规则

// 科室名称唯一
const checkUnique = (rule, value, callback) => {
    const isUnique = !officeInfoList.value.some(item => item.name === value);
    console.log(officeInfoList.value);
    if (isUnique || (updateOfficeName.value == value)) {
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

const updateVisible = ref(false);
const updateOfficeName = ref('');
const updateOfficeData = ref({
    name: '',
    intro: '',
    office_id: ''
});

const updateOffice = async (officeInfo) => {
    // 此处必须深拷贝
    updateOfficeData.value = {
        name: officeInfo.name,
        intro: officeInfo.intro,
        office_id: officeInfo.office_id
    }
    updateOfficeName.value = officeInfo.name;
    updateVisible.value = true;
}

import { updateOfficeService } from '@/api/office';
const updateOfficeInfo = async () => {
    console.log(officeInfoList.value);
    // 表单校验
    // 对表单进行校验
    await formRef.value?.validate().catch(err => {
        ElMessage.error('登录失败(前端)')
        throw err
    })
    let result;
    result = await updateOfficeService({
        office_id: updateOfficeData.value.office_id,
        name: updateOfficeData.value.name,
        intro: updateOfficeData.value.intro,
        mode: "update"
    });
    if (result.code == 0) {
        ElMessage.success("成功更改医生信息");
        getOfficeInfo();
        updateVisible.value = false;
    } else {
        ElMessage.error(result.message ? result.message : "更改失败");
    }
}

// 从修改界面返回
const back_manage = ()=>{
    // getOfficeInfo();
    updateVisible.value = false;
}

</script>

<template>
    <div v-if="!updateVisible">
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="审核状态">
                <el-select v-model="searchForm.office_name" placeholder="选择状态" clearable
                    @clear="searchForm.office_name = '', ElementChange" style="width: 300px;">
                    <el-option v-for="item in officeInfoList" :key="item.name" :label="item.name"
                        :value="item.name"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary">查询</el-button>
            </el-form-item>
            <el-form-item>
                <el-button type="success" @click="addOffice">添加科室</el-button>
            </el-form-item>
        </el-form>

        <!-- 评价列表 -->
        <el-table :data="filteredOffices" style="width: 100%" :key="filteredOffices">
            <el-table-column prop="name" label="科室名称"></el-table-column>
            <el-table-column prop="intro" label="科室简介" width="800">
                <template #default="scope">
                    <span style="margin-right: 5px;">{{ scope.row.intro.slice(0, 40) }}<el-text
                            v-if="scope.row.intro.length > 40">...</el-text></span>
                    <el-button type="primary" v-if="scope.row.intro.length > 40"
                        @click="showIntroDetails(scope.row.intro)" size="small">详情</el-button>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="400">
                <template #default="scope">
                    <el-button type="success" @click="showOffice(scope.row)">查看</el-button>
                    <el-button type="primary" @click="updateOffice(scope.row)">修改</el-button>
                    <el-button type="danger" @click="dialog_goto_del(scope.row.office_id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize"
            layout="jumper, total, prev, pager, next" background :total="total"
            style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>

        <!-- 科室内容详情对话框 -->
        <el-dialog v-model="introDialogVisible" title="评论信息详情" width="50%" align-center>
            <div v-if="selectedIntro">
                <p class="dialog_item_content"><strong class="dialog_item_title">评论内容:</strong> {{ selectedIntro
                    }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeIntroDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 科室信息查看对话框 -->
        <el-dialog v-model="officeDialogVisible" title="评价详细信息" width="50%" align-center>
            <div v-if="selectedOffice">
                <p class="dialog_item_content"><strong class="dialog_item_title">医生所属科室:</strong> {{
                    selectedOffice.name }}
                </p>
                <p class="dialog_item_content"><strong class="dialog_item_title">评价内容:</strong> {{
                    selectedOffice.intro
                    }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeOfficeDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>

    <!-- 修改科室信息界面 -->
    <div v-if="updateVisible" style="padding: 20px;" class="addDoctor_container">

        <h1 class="title">修改科室信息</h1>

        <el-card class="card">
            <!-- 表单 -->
            <el-form class="form" :label-position="labelPosition" label-width="auto" :model="updateOfficeData"
                ref="formRef" :rules="Rules" style="max-width: 600px">

                <el-form-item label="科室名称" :label-position="itemLabelPosition" prop="name" class="form_item">
                    <el-input v-model="updateOfficeData.name" placeholder="请输入科室名称" clearable class="input"/>
                </el-form-item>

                <el-form-item label="科室简介" :label-position="itemLabelPosition" prop="intro" class="form_item">
                    <el-input v-model="updateOfficeData.intro" placeholder="请输入科室简介" clearable class="input"
                        type="textarea" :rows="6" />
                </el-form-item>

                <el-form-item class="form_item">
                    <el-row style="width: 100%; display: flex; align-items: center; justify-items: center;">

                        <el-col :span="12" style="display: flex; justify-content: center;">
                            <el-button type="primary" auto-insert-space @click="back_manage"
                                class="update_btn">
                                返回
                            </el-button>
                        </el-col>
                        <el-col :span="12" style="display: flex; justify-content: center;">
                            <el-button type="primary" auto-insert-space @click="updateOfficeInfo" class="update_btn">
                                确认更改
                            </el-button>
                        </el-col>

                    </el-row>

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
    top: 5%;
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

            .update_btn {
                width: 45%;
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
