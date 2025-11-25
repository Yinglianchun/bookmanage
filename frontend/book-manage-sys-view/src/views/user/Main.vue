<template>
    <div class="feedback-page">
        <section class="feedback-hero">
            <div>
                <p class="hero-eyebrow">读者反馈中心</p>
                <h1>洞察每一条心声，快速闭环</h1>
                <p class="hero-description">记录、筛选、回复建议，以更清晰的字体与更沉浸的界面联通读者与管理员。</p>
                <div class="hero-metrics">
                    <div class="metric">
                        <span>全部建议</span>
                        <strong>{{ totalItems }}</strong>
                    </div>
                    <div class="metric">
                        <span>待回复</span>
                        <strong>{{ pendingReplies }}</strong>
                    </div>
                    <div class="metric">
                        <span>本页显示</span>
                        <strong>{{ tableData.length }}</strong>
                    </div>
                </div>
            </div>
            <div class="hero-actions">
                <el-button type="primary" icon="el-icon-edit" @click="toFeedback">提交建议</el-button>
                <el-button plain icon="el-icon-refresh" @click="fetchFreshData">刷新</el-button>
            </div>
        </section>

        <el-card shadow="never" class="filter-card">
            <div class="filter-header">
                <h3>智能检索</h3>
                <p>通过关键词、公开状态等组合，快速筛选需要优先处理的建议。</p>
            </div>
            <el-form label-position="top" size="small">
                <el-row :gutter="16">
                    <el-col :xs="24" :sm="12" :md="10">
                        <el-form-item label="建议关键词">
                            <el-input
                                v-model="readerProposalQueryDto.content"
                                placeholder="输入内容关键词"
                                clearable
                                prefix-icon="el-icon-search"
                                @keyup.enter.native="handleFilter"
                                @clear="handleFilterClear" />
                        </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="6">
                        <el-form-item label="公开状态">
                            <el-select v-model="readerProposalQueryDto.isPublish" placeholder="全部" clearable style="width: 100%;">
                                <el-option label="公开" :value="true"></el-option>
                                <el-option label="私密" :value="false"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="8">
                        <el-form-item label="提交时间范围">
                            <el-date-picker
                                v-model="searchTime"
                                type="daterange"
                                unlink-panels
                                range-separator="至"
                                start-placeholder="开始日期"
                                end-placeholder="结束日期"
                                style="width: 100%;" />
                        </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="24" class="filter-actions">
                        <el-button type="primary" icon="el-icon-search" @click="handleFilter">查询</el-button>
                        <el-button plain icon="el-icon-refresh-right" @click="resetQueryCondition">重置</el-button>
                    </el-col>
                </el-row>
            </el-form>
        </el-card>

        <section class="feedback-list" v-loading="loading">
            <el-empty description="暂无数据" v-if="!tableData.length"></el-empty>
            <article v-for="(item, index) in tableData" :key="index" class="feedback-card">
                <header>
                    <div>
                        <h4>{{ item.userName || '匿名读者' }}</h4>
                        <p>ID · {{ item.id }}</p>
                    </div>
                    <el-tag :type="item.isPublish ? 'success' : 'info'" effect="dark">
                        {{ item.isPublish ? '公开' : '私密' }}
                    </el-tag>
                </header>
                <p class="content">{{ item.content }}</p>
                <div class="reply-block" :class="{ pending: !item.replyContent }">
                    <span class="label">回复</span>
                    <p>{{ item.replyContent || '管理员暂未回复' }}</p>
                </div>
                <footer>
                    <span><i class="el-icon-time"></i> 提交 · {{ item.createTime }}</span>
                    <span v-if="item.replyTime"><i class="el-icon-message"></i> 回复 · {{ item.replyTime }}</span>
                    <div class="card-actions">
                        <el-button size="mini" type="danger" plain @click="handleDelete(item)">删除</el-button>
                    </div>
                </footer>
            </article>
        </section>

        <div class="pagination-container">
            <el-pagination
                background
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="currentPage"
                :page-sizes="[5, 10, 20]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="totalItems">
            </el-pagination>
        </div>

        <el-dialog
            :visible.sync="dialogUserOperaion"
            :show-close="false"
            width="520px"
            custom-class="modern-dialog"
            append-to-body>
            <div slot="title" class="dialog-title">
                <span>提交建议</span>
                <el-button type="text" icon="el-icon-close" @click="dialogUserOperaion = false"></el-button>
            </div>
            <div class="dialog-body">
                <el-form label-position="top" size="small">
                    <el-form-item label="建议内容" required>
                        <el-input
                            type="textarea"
                            :rows="5"
                            placeholder="请填写你的建议..."
                            v-model="data.content"
                            resize="none">
                        </el-input>
                    </el-form-item>
                    <el-form-item label="是否公开">
                        <el-switch v-model="data.isPublish" active-text="公开" inactive-text="私密"></el-switch>
                    </el-form-item>
                </el-form>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogUserOperaion = false">取消</el-button>
                <el-button type="primary" icon="el-icon-s-promotion" @click="addOperation">提交</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    data() {
        return {
            loading: false,
            userPwd: '',
            data: { userAvatar: '' },
            filterText: '',
            currentPage: 1,
            pageSize: 7,
            totalItems: 0,
            dialogMessageOperation: false,
            dialogUserOperaion: false,
            isOperation: false,
            tableData: [],
            searchTime: [],
            selectedRows: [],
            status: null,
            readerProposalQueryDto: { content: '', isPublish: true },
            messsageContent: ''
        };
    },
    computed: {
        pendingReplies() {
            return this.tableData.filter(item => !item.replyTime).length;
        }
    },
    created() {
        this.fetchFreshData();
    },
    methods: {
        toFeedback() {
            this.dialogUserOperaion = true;
            this.data = { isPublish: true };
        },
        messagePushOperation() {},
        handleAvatarSuccess(res, file) {
            if (res.code !== 200) {
                this.$message.error('回复上传异常');
                return;
            }
            this.$message.success('回复上传成功');
            this.data.userAvatar = res.data;
        },
        switchChange() {
            this.fetchFreshData();
        },
        handleSelectionChange(selection) {
            this.selectedRows = selection;
        },
        async batchDelete() {
            if (!this.selectedRows.length) {
                this.$message('未选中任何数据');
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除回复数据',
                text: '删除后不可恢复，是否继续？',
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.selectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/readerProposal/batchDelete`, ids);
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
                    console.error('读者建议删除异常:', e);
                }
            }
        },
        resetQueryCondition() {
            this.readerProposalQueryDto = { content: '', isPublish: true };
            this.searchTime = [];
            this.fetchFreshData();
        },
        async addOperation() {
            if (this.userPwd !== '') {
                this.data.userPwd = this.$md5(this.$md5(this.userPwd));
            } else {
                this.data.userPwd = null;
            }
            try {
                const response = await this.$axios.post('/readerProposal/save', this.data);
                this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
                if (response.data.code === 200) {
                    this.closeDialog();
                    this.fetchFreshData();
                    this.clearFormData();
                }
            } catch (error) {
                console.error('提交表单时出现异常', error);
                this.$message.error('提交失败，请稍后重试');
            }
        },
        closeDialog() {
            this.dialogUserOperaion = false;
        },
        clearFormData() {
            this.data = {};
        },
        async fetchFreshData() {
            try {
                this.loading = true;
                this.tableData = [];
                let startTime = null;
                let endTime = null;
                if (this.searchTime != null && this.searchTime.length === 2) {
                    const [startDate, endDate] = await Promise.all(this.searchTime.map(date => date.toISOString()));
                    startTime = `${startDate.split('T')[0]}T00:00:00`;
                    endTime = `${endDate.split('T')[0]}T23:59:59`;
                }
                const params = {
                    current: this.currentPage,
                    size: this.pageSize,
                    key: this.readerProposalQueryDto.content,
                    startTime: startTime,
                    endTime: endTime,
                    ...this.readerProposalQueryDto
                };
                const response = await this.$axios.post('/readerProposal/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                console.error('查询回复信息异常:', error);
            } finally {
                this.loading = false;
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
            this.readerProposalQueryDto.content = '';
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
        handleDelete(row) {
            this.selectedRows = [row];
            this.batchDelete();
        }
    },
};
</script>
<style scoped lang="scss">
@use '../admin/styles/neo-theme.scss' as neo;

.feedback-page {
    @include neo.neo-page-shell;
    padding-bottom: 60px;
}

.feedback-hero {
    @include neo.neo-glass-card;
    display: flex;
    justify-content: space-between;
    gap: 32px;
    padding: 36px;
    margin-bottom: 26px;

    .hero-eyebrow {
        letter-spacing: 0.2em;
        color: neo.$neo-text-muted;
        text-transform: uppercase;
        margin-bottom: 6px;
    }

    h1 {
        margin: 0;
        font-size: 30px;
    }

    .hero-description {
        margin: 12px 0 24px;
        color: neo.$neo-text-secondary;
    }

    .hero-metrics {
        display: flex;
        gap: 16px;
        flex-wrap: wrap;

        .metric {
            @include neo.neo-polar-grid;
            background: transparent;
            min-width: 140px;

            span {
                color: neo.$neo-text-muted;
                font-size: 12px;
            }

            strong {
                display: block;
                margin-top: 6px;
                font-size: 24px;
            }
        }
    }

    .hero-actions {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .el-button:first-child {
            @include neo.neo-button(neo.$neo-green, lighten(neo.$neo-green, 8%));
            border: none;
        }

        .el-button:nth-child(2) {
            @include neo.neo-ghost-button;
        }
    }
}

.filter-card {
    @include neo.neo-glass-card;
    margin-bottom: 24px;
    padding: 28px;

    .filter-header {
        margin-bottom: 18px;

        h3 {
            margin: 0;
            color: neo.$neo-text-primary;
        }

        p {
            margin: 6px 0 0;
            color: neo.$neo-text-secondary;
        }
    }

    @include neo.neo-input;
}

.filter-actions {
    display: flex;
    gap: 12px;
    margin-top: 10px;

    .el-button:first-child {
        @include neo.neo-button(neo.$neo-blue, lighten(neo.$neo-blue, 8%));
    }
}

.feedback-list {
    display: grid;
    gap: 20px;
}

.feedback-card {
    border-radius: 20px;
    border: 1px solid neo.$neo-border;
    background: rgba(14, 19, 40, 0.85);
    padding: 24px;
    box-shadow: neo.$neo-shadow;

    header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        h4 {
            margin: 0;
            color: neo.$neo-text-primary;
        }

        p {
            margin: 4px 0 0;
            color: neo.$neo-text-muted;
            font-size: 12px;
        }
    }

    .content {
        margin: 16px 0;
        color: neo.$neo-text-secondary;
        line-height: 1.6;
    }

    .reply-block {
        border-radius: 16px;
        padding: 14px 18px;
        background: rgba(255, 255, 255, 0.02);
        border: 1px solid rgba(255, 255, 255, 0.04);
        margin-bottom: 18px;

        &.pending {
            border-color: rgba(neo.$neo-orange, 0.4);
            color: neo.$neo-orange;
        }

        .label {
            display: inline-block;
            font-size: 12px;
            text-transform: uppercase;
            color: neo.$neo-text-muted;
        }

        p {
            margin: 4px 0 0;
            color: neo.$neo-text-primary;
        }
    }

    footer {
        display: flex;
        align-items: center;
        justify-content: space-between;
        flex-wrap: wrap;
        gap: 10px;
        color: neo.$neo-text-muted;
        font-size: 12px;

        .card-actions {
            display: flex;
            gap: 8px;
        }
    }
}

.pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    @include neo.neo-pagination;
}

.dialog-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: neo.$neo-text-primary;
}

.dialog-body {
    @include neo.neo-input;

    .feedback-info {
        @include neo.neo-polar-grid;
        margin-bottom: 20px;

        .info-head {
            color: neo.$neo-text-primary;
            margin-bottom: 6px;
        }

        p {
            margin: 0;
            color: neo.$neo-text-secondary;
        }
    }
}

@include neo.neo-dialog;

@media (max-width: 960px) {
    .feedback-hero {
        flex-direction: column;
    }

    .hero-actions {
        flex-direction: row !important;
    }
}
</style>
