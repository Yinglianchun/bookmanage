<template>
  <div class="page-container shelf-manage">
    <section class="page-hero">
      <div class="hero-content">
        <p class="hero-eyebrow">架位布局</p>
        <h1>塑造清晰的空间坐标</h1>
        <p class="hero-description">按楼层、区域快速定位图书架位，保持线下空间与系统数据实时同步。</p>
        <div class="hero-metrics">
          <div class="metric">
            <span>架位总数</span>
            <strong>{{ totalItems }}</strong>
          </div>
          <div class="metric">
            <span>覆盖楼层</span>
            <strong>{{ distinctFloors }}</strong>
          </div>
          <div class="metric">
            <span>已选中</span>
            <strong>{{ selectedRows.length }}</strong>
          </div>
        </div>
      </div>
      <div class="hero-actions">
        <el-button class="hero-btn primary" icon="el-icon-plus" size="medium" @click="add">新增书架</el-button>
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
          <h3>位置筛选</h3>
          <p>根据楼层与区域组合检索，定位具体架位信息。</p>
        </div>
        <el-tag type="success" effect="dark">实时定位</el-tag>
      </div>
      <div class="filters-body">
        <el-form label-position="top" size="small" class="filter-form">
          <el-row :gutter="20">
            <el-col :xs="24" :md="6">
              <el-form-item label="楼层">
                <el-input
                  size="small"
                  v-model="bookshelfQueryDto.floor"
                  placeholder="例如：3"
                  clearable />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="6">
              <el-form-item label="区域">
                <el-input
                  size="small"
                  v-model="bookshelfQueryDto.area"
                  placeholder="例如：A区"
                  clearable />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="6">
              <el-form-item label="架位编号">
                <el-input size="small" v-model="bookshelfQueryDto.frame" placeholder="支持模糊搜索" clearable />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="6" class="filters-actions">
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
          <h3>架位列表</h3>
          <p>维护空间坐标体系，方便图书与物理位置的映射。</p>
        </div>
        <div class="header-badges">
          <span class="badge badge-safe"><i class="el-icon-collection"></i> 空间映射</span>
        </div>
      </div>
      <div class="table-card__body">
        <div class="table-toolbar">
          <span>已选择 {{ selectedRows.length }} 项</span>
          <span>最近刷新：{{ lastRefresh || '加载中...' }}</span>
        </div>
        <el-table
          :data="tableData"
          v-loading="loading"
          @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center"></el-table-column>
          <el-table-column prop="floor" label="楼层" width="140" align="center">
            <template slot-scope="scope">
              <el-tag size="medium" effect="plain">{{ scope.row.floor }} 层</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="area" label="区域" align="center"></el-table-column>
          <el-table-column prop="frame" label="架位编号" align="center" min-width="180">
            <template slot-scope="scope">
              <span class="frame-name">{{ scope.row.frame }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="180">
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
          :page-sizes="[10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalItems">
        </el-pagination>
      </div>
    </el-card>

    <el-dialog
      :visible.sync="dialogOperaion"
      :show-close="false"
      width="480px"
      append-to-body>
      <div slot="title" class="dialog-title">
        <span>{{ !isOperation ? '新增书架' : '编辑书架' }}</span>
        <el-button type="text" icon="el-icon-close" @click="dialogOperaion = false"></el-button>
      </div>
      <div class="dialog-body">
        <el-form :model="data" label-position="top" size="small">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="楼层" required>
                <el-input v-model="data.floor" placeholder="例如：3"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="区域" required>
                <el-input v-model="data.area" placeholder="例如：A区"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="架位名称/编号" required>
            <el-input v-model="data.frame" placeholder="例如：A1-05架"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button size="medium" @click="dialogOperaion = false">取消</el-button>
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
      cover: null,
      pageSize: 10,
      totalItems: 0,
      dialogMessageOperation: false,
      dialogOperaion: false,
      isOperation: false,
      tableData: [],
      searchTime: [],
      selectedRows: [],
      status: null,
      bookshelfQueryDto: {},
      options: [],
      loading: false,
      lastRefresh: ''
    };
  },
  computed: {
    distinctFloors() {
      return new Set(this.tableData.map(item => item.floor)).size;
    },
    analyticsCards() {
      return [
        { label: '架位总数', value: this.totalItems || 0, caption: '系统累计' },
        { label: '覆盖楼层', value: this.distinctFloors, caption: '当前页数据' },
        { label: '本页记录', value: this.tableData.length, caption: '实时展示' },
        { label: '已选中', value: this.selectedRows.length, caption: '批量管理' }
      ];
    }
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    switchChange() {
      this.fetchFreshData();
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    async batchDelete() {
      if (!this.selectedRows.length) return;
      const confirmed = await this.$swalConfirm({
        title: '确认删除?',
        text: `是否删除选中的 ${this.selectedRows.length} 条架位信息？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          const ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/bookshelf/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$message.success(response.data.msg);
            this.fetchFreshData();
          }
        } catch (e) {
          this.$message.error('删除失败');
        }
      }
    },
    resetQueryCondition() {
      this.bookshelfQueryDto = {};
      this.currentPage = 1;
      this.fetchFreshData();
    },
    async updateOperation() {
      try {
        const response = await this.$axios.put('/bookshelf/update', this.data);
        if (response.data.code === 200) {
          this.$message.success('修改成功');
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        this.$message.error('提交失败');
      }
    },
    async addOperation() {
      try {
        const response = await this.$axios.post('/bookshelf/save', this.data);
        if (response.data.code === 200) {
          this.$message.success('新增成功');
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        }
      } catch (error) {
        this.$message.error('提交失败');
      }
    },
    closeDialog() {
      this.dialogOperaion = false;
      this.isOperation = false;
    },
    clearFormData() {
      this.data = {};
      this.cover = null;
    },
    async fetchFreshData() {
      try {
        this.loading = true;
        this.tableData = [];
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          key: this.bookshelfQueryDto.floor || '',
          ...this.bookshelfQueryDto
        };
        const response = await this.$axios.post('/bookshelf/query', params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
        this.lastRefresh = new Date().toLocaleString();
      } catch (error) {
        console.error('查询书架信息异常:', error);
      } finally {
        this.loading = false;
      }
    },
    add() {
      this.data = {};
      this.dialogOperaion = true;
      this.isOperation = false;
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
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
      this.dialogOperaion = true;
      this.isOperation = true;
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

.frame-name {
  font-weight: 600;
  color: neo.$neo-text-primary;
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
}
</style>
