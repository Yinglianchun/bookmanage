<template>
  <div class="page-container category-manage">
    <section class="page-hero">
      <div class="hero-content">
        <p class="hero-eyebrow">分类资产</p>
        <h1>构建清晰的图书分类系统</h1>
        <p class="hero-description">灵活维护分类层级，保持馆藏知识结构井然有序。</p>
        <div class="hero-metrics">
          <div class="metric">
            <span>全部分类</span>
            <strong>{{ totalItems }}</strong>
          </div>
          <div class="metric">
            <span>本页展示</span>
            <strong>{{ tableData.length }}</strong>
          </div>
          <div class="metric">
            <span>已选中</span>
            <strong>{{ selectedRows.length }}</strong>
          </div>
        </div>
      </div>
      <div class="hero-actions">
        <el-button class="hero-btn primary" icon="el-icon-plus" size="medium" @click="add">新增分类</el-button>
        <el-button
          class="hero-btn ghost"
          icon="el-icon-delete"
          size="medium"
          :disabled="!selectedRows.length"
          @click="batchDelete">批量删除</el-button>
      </div>
    </section>

    <section class="insights-grid">
      <div class="insight-card" v-for="card in analyticsCards" :key="card.label">
        <p>{{ card.label }}</p>
        <strong>{{ card.value }}</strong>
        <span>{{ card.caption }}</span>
      </div>
    </section>

    <el-card shadow="never" class="filters-card">
      <div class="filters-header">
        <div>
          <h3>分类筛选</h3>
          <p>快速定位需要调整的分类，支持名称匹配与时间区间。</p>
        </div>
        <el-tag type="success" effect="dark">实时同步</el-tag>
      </div>
      <div class="filters-body">
        <el-form label-position="top" size="small" class="filter-form">
          <el-row :gutter="20">
            <el-col :xs="24" :md="10">
              <el-form-item label="分类名称">
                <el-input
                  v-model="categoryQueryDto.name"
                  placeholder="输入分类名称"
                  clearable
                  prefix-icon="el-icon-collection"
                  @clear="handleFilterClear"
                  @keyup.enter.native="handleFilter" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="10">
              <el-form-item label="创建时间范围">
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
            <el-col :xs="24" :md="4" class="filters-actions">
              <el-button type="primary" icon="el-icon-search" @click="handleFilter">查询</el-button>
              <el-button type="default" plain icon="el-icon-refresh-left" @click="resetQueryCondition">重置</el-button>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div class="table-card__header">
        <div>
          <h3>分类列表</h3>
          <p>可视化掌控分类结构，支持即时编辑与批量操作。</p>
        </div>
        <div class="header-badges">
          <span class="badge badge-safe"><i class="el-icon-collection-tag"></i> 结构维护</span>
        </div>
      </div>
      <div class="table-card__body">
        <div class="table-toolbar">
          <span>已选择 {{ selectedRows.length }} 项</span>
          <span>最近刷新：{{ lastRefresh || '加载中...' }}</span>
        </div>
        <el-table
          :data="tableData"
          @selection-change="handleSelectionChange"
          v-loading="loading">
          <el-table-column type="selection" width="55" align="center"></el-table-column>
          <el-table-column prop="name" label="分类名称" min-width="220" show-overflow-tooltip>
            <template slot-scope="scope">
              <span class="category-name">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" align="center">
            <template slot-scope="scope">
              <div class="table-actions">
                <el-button type="text" class="neo-link" size="small" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="text" class="danger-link" size="small" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
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
    </el-card>

    <el-dialog
      :visible.sync="dialogUserOperaion"
      :show-close="false"
      width="420px"
      append-to-body>
      <div slot="title" class="dialog-title">
        <span>{{ !isOperation ? '新增图书分类' : '编辑图书分类' }}</span>
        <el-button type="text" icon="el-icon-close" @click="dialogUserOperaion = false"></el-button>
      </div>
      <div class="dialog-body">
        <el-form label-position="top" size="small">
          <el-form-item label="分类名称" required>
            <el-input v-model="data.name" placeholder="请输入分类名称"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button size="medium" @click="dialogUserOperaion = false">取消</el-button>
        <el-button type="primary" size="medium" @click="!isOperation ? addOperation() : updateOperation()">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userPwd: '',
      data: { userAvatar: '' },
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
      categoryQueryDto: {},
      messsageContent: '',
      loading: false,
      lastRefresh: ''
    };
  },
  created() {
    this.fetchFreshData();
  },
  computed: {
    analyticsCards() {
      const initials = new Set(this.tableData.map(item => (item.name || '').charAt(0))).size;
      return [
        { label: '分类总数', value: this.totalItems || 0, caption: '系统累计' },
        { label: '当前页', value: this.tableData.length, caption: '实时展示' },
        { label: '已选中', value: this.selectedRows.length, caption: '批量处理' },
        { label: '首字母类别', value: initials, caption: '名称分布' }
      ];
    }
  },
  methods: {
    messagePushOperation() {
      const messages = [];
      const message = {
        receiverId: this.data.id,
        content: this.data.content
      };
      messages.push(message);
      this.$axios.post('/message/systemInfoSave', messages).then(response => {
        const { data } = response;
        if (data.code === 200) {
          this.$swal.fire({
            title: '消息推送',
            text: '推送成功',
            icon: 'success',
            showConfirmButton: false,
            timer: 1000,
          });
          this.dialogMessageOperation = false;
          this.data = {};
        }
      });
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message('请先勾选需要删除的分类');
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: '删除提示',
        text: `删除后不可恢复，确定继续吗？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          const ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/category/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$swal.fire({
              title: '删除成功',
              text: response.data.msg,
              icon: 'success',
              showConfirmButton: false,
              timer: 2000,
            });
            this.fetchFreshData();
          }
        } catch (e) {
          this.$swal.fire({
            title: '错误提示',
            text: e,
            icon: 'error',
            showConfirmButton: false,
            timer: 2000,
          });
        }
      }
    },
    resetQueryCondition() {
      this.categoryQueryDto = {};
      this.searchTime = [];
      this.currentPage = 1;
      this.fetchFreshData();
    },
    async updateOperation() {
      try {
        const response = await this.$axios.put('/category/update', this.data);
        this.$swal.fire({
          title: '分类更新',
          text: response.data.msg,
          icon: response.data.code === 200 ? 'success' : 'error',
          showConfirmButton: false,
          timer: 1000,
        });
        if (response.data.code === 200) {
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        console.error('提交表单出现异常', error);
        this.$message.error('提交失败，请稍后再试');
      }
    },
    async addOperation() {
      try {
        const response = await this.$axios.post('/category/save', this.data);
        this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
        if (response.data.code === 200) {
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        console.error('提交表单出现异常', error);
        this.$message.error('提交失败，请稍后再试');
      }
    },
    closeDialog() {
      this.dialogUserOperaion = false;
      this.isOperation = false;
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
          key: this.categoryQueryDto.name || '',
          startTime: startTime,
          endTime: endTime,
          ...this.categoryQueryDto
        };
        const response = await this.$axios.post('/category/query', params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
        this.lastRefresh = new Date().toLocaleString();
      } catch (error) {
        console.error('查询分类信息异常:', error);
      } finally {
        this.loading = false;
      }
    },
    add() {
      this.data = {};
      this.dialogUserOperaion = true;
      this.isOperation = false;
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleFilterClear() {
      this.categoryQueryDto.name = '';
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
      this.isOperation = true;
      row.userPwd = null;
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
@use './styles/neo-theme.scss' as neo;

.page-container {
  @include neo.neo-page-shell;
}

.page-hero {
  @include neo.neo-glass-card;
  display: flex;
  justify-content: space-between;
  gap: 36px;
  padding: 36px;
  margin-bottom: 24px;
  align-items: center;

  .hero-content {
    flex: 1;

    .hero-eyebrow {
      letter-spacing: 0.2em;
      text-transform: uppercase;
      color: neo.$neo-text-muted;
      font-size: 12px;
      margin-bottom: 10px;
    }

    h1 {
      margin: 0;
      font-size: 28px;
      color: neo.$neo-text-primary;
    }

    .hero-description {
      color: neo.$neo-text-secondary;
      margin: 10px 0 24px;
      max-width: 520px;
    }

    .hero-metrics {
      display: flex;
      gap: 18px;
      flex-wrap: wrap;

      .metric {
        @include neo.neo-polar-grid;
        min-width: 140px;

        span {
          display: block;
          font-size: 12px;
          color: neo.$neo-text-muted;
        }

        strong {
          display: block;
          font-size: 22px;
          margin-top: 4px;
          color: neo.$neo-text-primary;
        }
      }
    }
  }

  .hero-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .hero-btn {
      min-width: 160px;
      height: 44px;
      font-weight: 600;

      &.primary {
        @include neo.neo-button(neo.$neo-green, lighten(neo.$neo-green, 8%));
      }

      &.ghost {
        @include neo.neo-ghost-button;
      }
    }
  }
}

.insights-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin: 0 0 24px;

  .insight-card {
    @include neo.neo-polar-grid;
    padding: 18px 22px;

    p {
      margin: 0;
      color: neo.$neo-text-muted;
      font-size: 12px;
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }

    strong {
      display: block;
      margin: 6px 0 4px;
      font-size: 26px;
      color: neo.$neo-text-primary;
    }

    span {
      color: neo.$neo-text-secondary;
      font-size: 12px;
    }
  }
}

.filters-card,
.table-card {
  @include neo.neo-glass-card;
  margin-bottom: 24px;
}

.filters-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;

  h3 {
    margin: 0;
    color: neo.$neo-text-primary;
    font-size: 18px;
  }

  p {
    margin: 6px 0 0;
    color: neo.$neo-text-secondary;
  }
}

.filters-body {
  margin-top: 20px;
  @include neo.neo-input;
}

.filters-actions {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  justify-content: flex-start;
  flex-wrap: wrap;

  .el-button:first-child {
    @include neo.neo-button(neo.$neo-blue, lighten(neo.$neo-blue, 10%));
  }
}

.table-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h3 {
    margin: 0;
    color: neo.$neo-text-primary;
  }

  p {
    margin: 6px 0 0;
    color: neo.$neo-text-secondary;
  }
}

.header-badges {
  display: flex;
  gap: 10px;

  .badge {
    @include neo.neo-chip(neo.$neo-blue);

    i {
      margin-right: 4px;
    }
  }

  .badge-safe {
    @include neo.neo-chip(neo.$neo-green);
  }
}

.table-card__body {
  @include neo.neo-table;

  .table-toolbar {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 18px;
    color: neo.$neo-text-secondary;
    font-size: 13px;
  }
}

.category-name {
  color: neo.$neo-text-primary;
  font-weight: 600;
}

.neo-link {
  color: neo.$neo-cyan;
}

.danger-link {
  color: neo.$neo-red !important;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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
}

::v-deep .el-dialog {
  background: neo.$neo-panel-bg;
  border-radius: 24px;
  border: 1px solid neo.$neo-border;
}

@media (max-width: 960px) {
  .page-hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-actions {
    flex-direction: row;
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
