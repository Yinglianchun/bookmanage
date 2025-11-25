<template>
    <div class="page-container">
        <el-card shadow="never" class="main-card">
            <!-- 顶部搜索与操作区 -->
            <div class="header-actions">
                <div class="search-area">
                    <span class="label">公告检索</span>
                    <el-input
                        v-model="noticeQueryDto.name"
                        placeholder="输入公告标题"
                        size="small"
                        clearable
                        prefix-icon="el-icon-search"
                        style="width: 200px; margin-right: 10px;"
                        @clear="handleFilterClear"
                        @keyup.enter.native="handleFilter">
                    </el-input>
                    <span class="label" style="margin-left: 20px;">发布时间</span>
                    <el-date-picker
                        size="small"
                        v-model="searchTime"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始时间"
                        end-placeholder="结束时间"
                        style="width: 260px;">
                    </el-date-picker>
                    <el-button type="primary" size="small" icon="el-icon-search" style="margin-left: 10px;" @click="handleFilter">查询</el-button>
                    <el-button plain size="small" icon="el-icon-refresh" @click="resetQueryCondition">重置</el-button>
                </div>

                <div class="button-area">
                    <el-button type="success" size="small" icon="el-icon-plus" @click="addNotice">新增公告</el-button>
                    <el-button type="danger" plain size="small" icon="el-icon-delete" :disabled="!selectedRows.length" @click="batchDelete()">批量删除</el-button>
                </div>
            </div>

            <!-- 数据表格 -->
            <div class="table-container">
                <el-table
                    :data="tableData"
                    style="width: 100%"
                    @selection-change="handleSelectionChange">
                    <el-table-column type="selection" width="50" align="center"></el-table-column>

                    <el-table-column prop="name" label="公告标题" min-width="300" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <span class="notice-title">{{ scope.row.name }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column prop="createTime" label="发布时间" width="180" sortable show-overflow-tooltip></el-table-column>

                    <el-table-column label="操作" width="150" fixed="right" align="center">
                        <template slot-scope="scope">
                            <el-button type="text" size="small" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
                            <el-button type="text" size="small" icon="el-icon-delete" class="danger-text" @click="handleDelete(scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页 -->
            <div class="pagination-container">
                <el-pagination
                    background
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[8, 20, 50]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="totalItems">
                </el-pagination>
            </div>
        </el-card>
    </div>
</template>

<script>
export default {
    data() {
        return {
            data: {},
            filterText: '',
            currentPage: 1,
            pageSize: 8,
            totalItems: 0,
            tableData: [],
            searchTime: [],
            selectedRows: [],
            noticeQueryDto: {},
        };
    },
    created() {
        this.fetchFreshData();
    },
    methods: {
        // 公告新增
        addNotice() {
            sessionStorage.setItem('noticeOperation', 'save');
            this.$router.push('/createNotice');
        },
        // 多选框选中
        handleSelectionChange(selection) {
            this.selectedRows = selection;
        },
        // 批量删除数据
        async batchDelete() {
            if (!this.selectedRows.length) {
                this.$message('未选中任何数据');
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除公告数据',
                text: '删除后不可恢复，是否继续？',
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.selectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`notice/batchDelete`, ids);
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
                    console.error('公告信息删除异常:', e);
                }
            }
        },
        resetQueryCondition() {
            this.noticeQueryDto = {};
            this.searchTime = [];
            this.fetchFreshData();
        },
        clearFormData() {
            this.data = {};
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
                    ...this.noticeQueryDto
                };
                const response = await this.$axios.post('notice/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                console.error('查询公告信息异常:', error);
            }
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
        handleEdit(row) {
            sessionStorage.setItem('noticeInfo', JSON.stringify(row));
            sessionStorage.setItem('noticeOperation', 'update');
            this.$router.push('/createNotice');
        },
        handleDelete(row) {
            this.selectedRows.push(row);
            this.batchDelete();
        }
    },
};
</script>
<style scoped lang="scss">
@use './styles/neo-theme.scss' as neo;

.page-container {
    @include neo.neo-page-shell;
    padding-bottom: 40px;
}

.main-card {
    @include neo.neo-glass-card;
    padding: 26px 30px 30px;
}

.header-actions {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    flex-wrap: wrap;
    gap: 18px;
    padding-bottom: 18px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);

    .search-area {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        gap: 12px;
        @include neo.neo-input;

        .label {
            font-size: 14px;
            font-weight: 600;
            color: neo.$neo-text-secondary;
            display: flex;
            align-items: center;
            gap: 6px;

            &::before {
                content: '';
                width: 4px;
                height: 16px;
                border-radius: 2px;
                background: linear-gradient(135deg, neo.$neo-orange 0%, neo.$neo-red 100%);
                box-shadow: 0 0 10px rgba(neo.$neo-orange, 0.4);
            }
        }

        .el-button[type='primary'] {
            @include neo.neo-button(neo.$neo-orange, lighten(neo.$neo-orange, 10%));
            height: 34px;
        }

        .el-button.plain {
            @include neo.neo-ghost-button;
            height: 34px;
        }
    }

    .button-area {
        display: flex;
        gap: 12px;

        .el-button--success {
            @include neo.neo-button(neo.$neo-green, lighten(neo.$neo-green, 6%));
        }

        .el-button--danger {
            @include neo.neo-button(neo.$neo-red, lighten(neo.$neo-red, 6%));
        }
    }
}

.table-container {
    margin-top: 24px;
    @include neo.neo-table;

    ::v-deep .el-table {
        background: transparent;
    }
}

.notice-title {
    font-weight: 600;
    color: neo.$neo-text-primary;
    transition: color 0.2s ease, transform 0.2s ease;

    &:hover {
        color: neo.$neo-blue;
        transform: translateX(4px);
    }
}

.danger-text {
    color: lighten(neo.$neo-red, 10%) !important;
}

.pagination-container {
    margin-top: 10px;
    display: flex;
    justify-content: center;
    padding-top: 12px;
    border-top: 1px solid rgba(255, 255, 255, 0.04);
    @include neo.neo-pagination;
}
</style>
