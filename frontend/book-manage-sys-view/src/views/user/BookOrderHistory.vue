<template>
    <el-row style="background-color: #FFFFFF;padding: 20px 0;border-radius: 5px;">
        <el-row style="padding: 10px;margin: 0 10px;">
            <el-row>
                <span class="top-bar">注册时间</span>
                <el-date-picker size="small" style="width: 220px;" v-model="searchTime" type="daterange"
                    range-separator="至" start-placeholder="起始时间" end-placeholder="结束时间">
                </el-date-picker>
                <el-button size="small" class="customer"
                    style="margin-left: 10px;background-color: rgb(230, 162, 60);color: rgb(245,245,245);border: none;"
                    type="primary" @click="handleFilter">立即查询</el-button>
            </el-row>
        </el-row>
        <el-row style="margin: 10px 20px;">
            <el-table :data="tableData" style="width: 1100px">
                <el-table-column prop="bookName" width="128" label="书籍名"></el-table-column>
                <el-table-column prop="deadlineNum" width="138" label="借阅数"></el-table-column>
                <el-table-column prop="isReturn" width="148" label="是否归还">
                    <template slot-scope="scope">
                        <span>{{ scope.row.isReturn ? '已归还' : '未归还' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="returnTime" width="120" label="归还时间"></el-table-column>
                <el-table-column prop="createTime" label="借书时间"></el-table-column>
                <el-table-column label="操作" fixed="right" width="150">
                    <template slot-scope="scope">
                        <span class="text-button" @click="handleEdit(scope.row)">还书</span>
                        <span class="text-button" @click="handleDelete(scope.row)">删除</span>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin: 20px 0;float: right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[5, 7]"
                :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="totalItems"></el-pagination>
        </el-row>
        <el-dialog :visible.sync="dialogOperation" width="25%">
            <div slot="title">
                <p class="dialog-title">还书</p>
            </div>
            <div style="padding:0 20px 20px 15px;">
                <div style="margin-bottom: 10px;">
                    <div class="point">归还提醒：</div>
                    <div class="point">书籍归还之后，可继续借书。恶意借书不还，将进入系统黑名单。</div>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button size="small"
                    style="background-color: rgb(230, 162, 60);color: rgb(245,245,245);border: none;" class="customer"
                    type="info" @click="returnOperation">确 定</el-button>
                <el-button class="customer" size="small" style="background-color: rgb(241, 241, 241);border: none;"
                    @click="dialogOperation = false">取 消</el-button>
            </span>
        </el-dialog>
    </el-row>
</template>

<script>
export default {
    data() {
        return {
            data: {},
            currentPage: 1,
            cover: null,
            pageSize: 7,
            totalItems: 0,
            dialogOperation: false, // 开关
            isOperation: false, // 开关-标识新增或修改
            tableData: [],
            selectedRows: [],
            bookOrderHistoryQueryDto: {}, // 搜索条件
            options: [],
            bookshelfOptions: [],
            bookOrderHistorys: [],
            searchTime: []
        };
    },
    created() {

        this.fetchFreshData();
        this.fetchCategory();
        this.fetchBookshelf();
    },
    methods: {

        bookshelfConfig(item) {
            return item.floor + "-" + item.area + "-" + item.frame;
        },
        fetchBookshelf() {
            this.$axios.post('bookshelf/query', {}).then(res => {
                if (res.data.code === 200) {
                    this.bookshelfOptions = res.data.data;
                }
            })
        },
        fetchCategory() {
            this.$axios.post('category/query', {}).then(res => {
                if (res.data.code === 200) {
                    this.options = res.data.data;
                }
            })
        },

        resetQueryCondition() {
            this.bookOrderHistoryQueryDto = {};
            this.fetchFreshData();
        },

        // 查询用户的预约记录
        getBookOrderHistory() {
            this.$axios.post('bookOrderHistory/queryUser', {}).then(res => {
                if (res.data.code === 200) {
                    this.bookOrderHistorys = res.data.data;
                }
            })
        },

        // 信息新增
        async returnOperation() {
            try {
                // 发之前，组装数据
                const bookOrderHsitory = {
                    id: this.data.id,
                    isReturn: true
                }
                const response = await this.$axios.put('/bookOrderHistory/update', bookOrderHsitory);
                if (response.data.code === 200) {
                    this.fetchFreshData();
                    this.data = {};
                    this.dialogOperation = false;
                    this.$notify({
                        title: '还书结果',
                        message: '还书成功',
                        type: 'success'
                    });
                } else {
                    this.$notify({
                        title: '还书结果',
                        message: response.data.msg,
                        type: 'error'
                    });
                }
            } catch (error) {
                this.$notify({
                    title: '还书结果',
                    message: error,
                    type: 'error'
                });
            }
        },
        closeDialog() {
            this.dialogOperation = false;
        },
        clearFormData() {
            this.data = {};
            this.cover = null;
        },
        async fetchFreshData() {
            try {
                this.tableData = [];
                let startTime = null;
                let endTime = null;
                if (this.searchTime != null && this.searchTime.length === 2) {
                    const [startDate, endDate] = await Promise.all(this.searchTime.map(date => date.toISOString()));
                    startTime = `${startDate.split('T')[0]}T00:00:00`;
                    endTime = `${endDate.split('T')[0]}T23:59:59`;
                }
                // 请求参数
                const params = {
                    current: this.currentPage,
                    size: this.pageSize,
                    startTime: startTime,
                    endTime: endTime,
                    ...this.bookOrderHistoryQueryDto
                };
                const response = await this.$axios.post('/bookOrderHistory/queryUser', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                console.error('借阅历史信息异常:', error);
            }
        },
        add() {
            this.dialogOperation = true;
        },
        handleFilter() {
            this.currentPage = 1;
            this.fetchFreshData();
        },
        handleFilterClear() {
            this.handleFilter();
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.currentPage = 1;
            this.fetchFreshData();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.fetchFreshData();
        },
        messagePush(row) {
            this.data = { ...row };
        },
        handleEdit(row) {
            this.dialogOperation = true;
            this.data = { ...row };
        },
        handleDelete(row) {
            this.selectedRows.push(row);
            this.batchDelete();
        },
        // 批量删除数据
        async batchDelete() {
            if (!this.selectedRows.length) {
                this.$message(`未选中任何数据`);
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除借阅记录数据',
                text: `删除后不可恢复，是否继续？`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.selectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/bookOrderHistory/batchDelete`, ids);
                    if (response.data.code === 200) {
                        this.$swal.fire({
                            title: '删除提示',
                            text: response.data.msg,
                            icon: 'success',
                            showConfirmButton: false,
                            timer: 2000,
                        });
                        this.fetchFreshData();
                        return;
                    }
                } catch (e) {
                    this.$swal.fire({
                        title: '错误提示',
                        text: e,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: 2000,
                    });
                    console.error(`用户信息删除异常：`, e);
                }
            }
        },
    },
};
</script>
<style scoped lang="scss">
.list-cover {
    width: 50px;
    height: 70px;
    border-radius: 5px;
}
</style>