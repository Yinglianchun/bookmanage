<template>
    <el-row style="background-color: #FFFFFF;padding: 20px 0;border-radius: 5px;">
        <el-row style="padding: 10px;margin: 0 10px;">
            <el-row>
                <span class="top-bar">书籍名</span>
                <el-input size="small" style="width: 188px;margin-right: 10px;" v-model="bookQueryDto.name" placeholder="书籍名" clearable
                    @clear="handleFilterClear">
                </el-input>
                <el-button size="small" class="customer" 
                    style="background-color: rgb(230,162,60);color: rgb(245,245,245);border: none;"type="primary"
                    @click="handleFilter">立即查询</el-button>
            </el-row>
        </el-row>
        <el-row style="margin: 10px 20px;">
            <el-table :data="tableData" style="width: 1400px">
                <el-table-column prop="cover" width="100" label="书籍封面">
                    <template slot-scope="scope">
                        <img :src="scope.row.cover" class="list-cover"/>
                    </template>
                </el-table-column>
                <el-table-column prop="name" width="148" label="书籍名"></el-table-column>
                <el-table-column prop="author" width="148" label="作者"></el-table-column>
                <el-table-column prop="num" width="148" label="馆藏数"></el-table-column>
                <el-table-column prop="bookshelf" width="200" label="书架">
                    <template slot-scope="scope">
                        <span>{{ bookshelfConfig(scope.row) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="categoryName" width="148" label="书籍类别"></el-table-column>
                <el-table-column label="操作" fixed="right" width="110">
                    <template slot-scope="scope">
                        <span class="text-button" @click="handleEdit(scope.row)">借 书</span>
                        <span class="text-button" @click="handleDelete(scope.row)">删 除</span>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin: 20px 0;float: right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[5, 7]"
                :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="totalItems"></el-pagination>
        </el-row>
        <!-- 操作面板 -->
        <el-dialog :visible.sync="dialogUserOperaion" width="25%">
            <div slot="title">
                <p class="dialog-title">借书</p>
            </div>
            <div style="padding:0 20px 20px 15px;">
                <div style="margin-bottom: 10px;">
                    <div class="point">借书数量</div>
                    <el-input-number size="small" v-model="data.deadlineNum" :min="1" :max="10" label="数量"></el-input-number>
                </div>
                <div>
                    <div class="point">归还日期</div>
                    <el-date-picker size="small"
                      v-model="data.returnTime"
                      type="date"
                      placeholder="选择日期">
                    </el-date-picker>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button size="small" style="background-color: rgb(230,162,60);color: rgb(245,245,245);border: none;"
                    class="customer" type="info" @click="addOperation">确 定</el-button>
                <el-button class="customer" size="small" style="background-color: rgb(241, 241, 241);border: none;"
                    @click="dialogUserOperaion = false">取 消</el-button>
            </span>
        </el-dialog>
    </el-row>
</template>

<script>
export default {
    data() {
        return {
            userPwd: '',
            data: { userAvatar: '' },
            filterText: '',
            currentPage: 1,
            cover:null,
            pageSize: 7,
            totalItems: 0,
            dialogMessageOperation: false,
            dialogUserOperaion: false, // 开关
            isOperation: false, // 开关-标识新增或修改
            tableData: [],
            searchTime: [],
            selectedRows: [],
            status: null,
            bookQueryDto: {}, // 搜索条件
            options:[],
            bookshelfOptions:[],
            bookOrderHistorys:[]
        };
    },
    created() {
        this.fetchFreshData();
        this.fetchCategory();
        this.fetchBookshelf();

    },
    methods: {
        bookshelfConfig(item){
            return item.floor+"-"+item.area+"-"+item.frame;
        },
        fetchCategory(){
            this.$axios.post('category/query',{}).then(res=>{
                if(res.data.code==200){
                    this.options = res.data.data;
                }
            })
        },
        fetchBookshelf(){
            this.$axios.post('bookshelf/query',{}).then(res=>{
                if(res.data.code==200){
                    this.bookshelfOptions = res.data.data;
                }
            })
        },
        resetQueryCondition() {
            this.searchTime = [];
            this.fetchFreshData();
        },
        //查询用户的预约记录
        getBookOrderHistory(){
            this.$axios.post('bookOrderHistory/queryUser',{}).then(res=>{
                if(res.data.code ===200){
                    this.bookOrderHistory=res.data.data;
                }
            })
        },


        // 信息新增
        async addOperation() {
            if (this.userPwd !== '') {
                this.data.userPwd = this.$md5(this.$md5(this.userPwd));
            } else {
                this.data.userPwd = null;
            }
            try {
                this.data.bookId=this.data.id;
                const response = await this.$axios.post('/bookOrderHistory/save', this.data);
                if (response.data.code === 200) {
                    this.fetchFreshData();
                    this.data={};
                    this.dialogMessageOperation = false;
                    this.$notify({
                      title: '借书结果',
                      message: '借书成功，请及时归还',
                      type: 'success'
                    });
                }else{
                    this.$notify({
                      title: '借书结果',
                      message: response.data.msg,
                      type: 'error'
                    });
                }
            } catch (error) {
                console.error('提交表单时出错:', error);
                this.$message.error('提交失败，请稍后再试！');
            }
        },
        closeDialog() {
            this.dialogUserOperaion = false;
        },
        clearFormData() {
            this.data = {};
            this.cover = null;
        },
        async fetchFreshData() {
            try {
                this.tableData = [];
                // 请求参数
                const params = {
                    current: this.currentPage,
                    size: this.pageSize,
                    key: this.filterText,
                    isPlanBuy: 0, 
                    ...this.bookQueryDto
                };
                const response = await this.$axios.post('/book/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                console.error('查询书籍信息异常:', error);
            }
        },
        add() {
            this.dialogUserOperaion = true;
        },
        handleFilter() {
            this.currentPage = 1;
            this.fetchFreshData();
        },
        handleFilterClear() {
            this.filterText = '';
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
            this.dialogMessageOperation = true;
            this.data = { ...row };
        },
        handleEdit(row) {
            this.dialogUserOperaion = true;
            this.data = { ...row }
        },
        handleDelete(row) {
            this.selectedRows.push(row);
            this.batchDelete();
        }
    },
};
</script>
<style scoped lang="scss">
.list-cover{
    width: 40px;
    height: 60px;
    border-radius: 5px;
}

.tag-tip {
    display: inline-block;
    padding: 5px 10px;
    border-radius: 5px;
    background-color: rgb(245, 245, 245);
    color: rgb(104, 118, 130);
}

.input-def {
    height: 40px;
    line-height: 40px;
    outline: none;
    border: none;
    font-size: 20px;
    color: rgb(102, 102, 102);
    font-weight: 900;
    width: 100%;
}

.dialog-footer {
    /* 使按钮水平居中 */
    display: flex;
    justify-content: center;
    align-items: center;
}

/* 如果需要调整按钮之间的间距 */
.customer {
    margin: 0 8px;
    /* 根据需要调整间距 */
}
</style>