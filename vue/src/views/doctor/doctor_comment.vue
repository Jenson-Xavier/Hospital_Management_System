<script setup>
import { computed, ref } from 'vue';

// 初始数据
const commentInfos = ref([]);

const searchForm = ref({
    start_date: '',
    end_date: ''
});

// 获取全部评论信息
import { getCommentInfoService, addCommentService } from '@/api/comment';
import { doctorIdService } from '@/api/doctor';
import { ElMessage } from 'element-plus';

const getCommentInfo = async () => {
    let result = await getCommentInfoService();
    if (result.code == 0) {
        // ElMessage.success("成功查询全部评论");
        commentInfos.value = result.data;
    } else {
        ElMessage.error(result.message ? result.message : "查询失败");
    }
}
getCommentInfo();
console.log(commentInfos);

// 获取医生用户工号
const username = ref('12312312312')
const getDoctorId = async () => {
    let result = await doctorIdService();
    username.value = result.data.user_id;
}
getDoctorId();

// 当前页码
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(commentInfos.value.length);

// 解析日期
const formatTimestamp = (timestamp)=>{
    const date = new Date(timestamp);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份从0开始  
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

// 过滤后的评价列表
const filteredComments = computed(() => {
    let result = commentInfos.value.map(item => ({
        ...item,
        // publisher: item.publisher.user_id,
        publish_time: formatTimestamp(item.publish_time),
    })).filter(comment => {
        const commentDate = new Date(comment.publish_time);
        const start_date = searchForm.value.start_date ? new Date(searchForm.value.start_date) : null;
        const end_date = searchForm.value.end_date ? new Date(searchForm.value.end_date) : null;

        if (end_date) {  
            end_date.setDate(end_date.getDate() + 1);  
        }  

        // 检查评论日期是否在开始日期和结束日期之间  
        const isWithinDateRange = (
            (!start_date || commentDate >= start_date) &&
            (!end_date || commentDate <= end_date)
        );

        return (isWithinDateRange && username.value == comment.commented.user_id);
    });
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});


// 查看评价详细信息
const commentDialogVisible = ref(false);
const selectedComment = ref(null);

const showCommentDetails = (comment) => {
    selectedComment.value = comment;
    commentDialogVisible.value = true;
};

const closeCommentDialog = () => {
    commentDialogVisible.value = false;
    selectedComment.value = null;
};

// 查看完整评价
const contentDialogVisible = ref(false);
const selectedContent = ref(null);

const showFullContent = (content) => {
    contentDialogVisible.value = true;
    selectedContent.value = content;
};

const closeContentDialog = () => {
    contentDialogVisible.value = false;
    selectedContent.value = null;
};


// 日期选择框更改
const ElementChange = () => {
    currentPage.value = 1;
}

</script>

<template>
    <div>
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="开始时间">
                <el-date-picker v-model="searchForm.start_date" value-format="YYYY-MM-DD" placeholder="选择日期" clearable
                    @clear="searchForm.start_date = ''" style="width: 220px;" @change="ElementChange"></el-date-picker>
            </el-form-item>

            <el-form-item label="结束时间">
                <el-date-picker v-model="searchForm.end_date" value-format="YYYY-MM-DD" placeholder="选择日期" clearable
                    @clear="searchForm.end_date = ''" style="width: 220px;" @change="ElementChange"></el-date-picker>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="onSearch">查询</el-button>
            </el-form-item>
        </el-form>

        <!-- 评价列表 -->
        <el-table :data="filteredComments" style="width: 100%">
            <el-table-column prop="publisher.user_id" label="患者用户名"></el-table-column>
            <el-table-column prop="publish_time" label="评论发布时间"></el-table-column>
            <el-table-column prop="comment_text" label="评论内容" width="300">
                <template #default="scope">
                    <span style="margin-right: 5px;">{{ scope.row.comment_text.slice(0, 10) }}<el-text v-if="scope.row.comment_text.length > 10">...</el-text></span>
                    <el-button type="primary" @click="showCommentDetails(scope.row.comment_text)"
                        size="small" v-if="scope.row.comment_text.length > 10">详情</el-button>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="success" @click="showFullContent(scope.row)">查看评价</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
            layout="jumper, total, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="handlePageChange" style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>
        <!-- 评价内容详细信息对话框 -->
        <el-dialog v-model="commentDialogVisible" title="评论内容详情" width="50%" align-center>
            <div v-if="selectedComment">
                <p>{{ selectedComment }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeCommentDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 评价详细信息对话框 -->
        <el-dialog v-model="contentDialogVisible" title="评价详细信息" width="50%" align-center>
            <div v-if="selectedContent">
                <p class="dialog_item_content"><strong class="dialog_item_title">评论者用户名:</strong> {{ selectedContent.publisher.user_id }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">评论发布时间:</strong> {{ selectedContent.publish_time }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">评论内容:</strong> {{ selectedContent.comment_text }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeContentDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>



<style scoped>
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
</style>