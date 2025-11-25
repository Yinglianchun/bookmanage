<template>
  <div class="page-container book-manage">
    <section class="page-hero">
      <div class="hero-content">
        <p class="hero-eyebrow">图书资产</p>
        <h1>集中掌控书目、分类与库存</h1>
        <p class="hero-description">在高效的操作界面中完成检索、录入和批量维护，保障馆藏信息实时更新。</p>
        <div class="hero-metrics">
          <div class="metric">
            <span>全部图书</span>
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
        <el-button class="hero-btn primary" icon="el-icon-plus" size="medium" @click="add">新建图书</el-button>
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
          <h3>高级检索</h3>
          <p>组合名称、分类与架位条件，定位目标书籍。</p>
        </div>
        <el-tag type="success" effect="dark">实时查询</el-tag>
      </div>
      <div class="filters-body">
        <el-form label-position="top" size="small" class="filter-form">
          <el-row :gutter="20">
            <el-col :xs="24" :md="8">
              <el-form-item label="图书名称 / ISBN">
                <el-input
                  v-model="bookQueryDto.name"
                  placeholder="输入图书名称或ISBN"
                  clearable
                  prefix-icon="el-icon-search"
                  @clear="handleFilterClear"
                  @keyup.enter.native="handleFilter" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="8">
              <el-form-item label="图书分类">
                <el-select v-model="bookQueryDto.categoryId" placeholder="全部分类" clearable>
                  <el-option v-for="item in options" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="8">
              <el-form-item label="所在书架">
                <el-select v-model="bookQueryDto.bookshelfId" placeholder="全部位置" clearable filterable>
                  <el-option
                    v-for="item in bookshelfOptions"
                    :key="item.id"
                    :label="bookshelfConfig(item)"
                    :value="item.id"></el-option>
                </el-select>
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
          <h3>图书列表</h3>
          <p>全面展示封面、位置信息与库存，支持直接编辑。</p>
        </div>
        <div class="header-badges">
          <span class="badge badge-safe"><i class="el-icon-files"></i> 馆藏档案</span>
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
          <el-table-column type="selection" width="50" align="center"></el-table-column>
          <el-table-column label="封面" width="110" align="center">
            <template slot-scope="scope">
              <div class="cover-wrapper">
                <img :src="scope.row.cover" class="book-cover" @error="handleCoverFallback($event)" />
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="图书名称" min-width="160" show-overflow-tooltip>
            <template slot-scope="scope">
              <span class="book-name">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="isbn" label="ISBN" width="150"></el-table-column>
          <el-table-column prop="author" label="作者" width="140" show-overflow-tooltip></el-table-column>
          <el-table-column prop="publisher" label="出版社" width="160" show-overflow-tooltip></el-table-column>
          <el-table-column label="位置 / 库存" width="230">
            <template slot-scope="scope">
              <div class="info-tag-group">
                <el-tag size="mini" effect="plain" type="info"><i class="el-icon-collection"></i> {{ scope.row.num }} 册</el-tag>
                <el-tag size="mini" effect="light" type="primary">{{ bookshelfConfig(scope.row) }}</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="分类" width="120">
            <template slot-scope="scope">
              <el-tag size="small" type="success" effect="plain">{{ scope.row.categoryName || '未分类' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="170" fixed="right" align="center">
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
      :visible.sync="dialogUserOperaion"
      :show-close="false"
      width="780px"
      append-to-body>
      <div slot="title" class="dialog-title">
        <span>{{ !isOperation ? '入库新图书' : '编辑图书信息' }}</span>
        <el-button type="text" icon="el-icon-close" @click="dialogUserOperaion = false"></el-button>
      </div>
      <div class="dialog-body">
        <el-form ref="bookForm" :model="data" label-position="top" size="small" class="dialog-form">
          <el-row :gutter="30">
            <el-col :span="14">
              <el-form-item label="图书名称" required>
                <el-input v-model="data.name" placeholder="请输入完整书名"></el-input>
              </el-form-item>
              <el-row :gutter="15">
                <el-col :span="12">
                  <el-form-item label="作者" required>
                    <el-input v-model="data.author" placeholder="作者姓名"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="出版社" required>
                    <el-input v-model="data.publisher" placeholder="出版社名称"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="ISBN">
                <el-input v-model="data.isbn" placeholder="例如：978-7-xxx-xxxx-x"></el-input>
              </el-form-item>
              <el-form-item label="内容简介">
                <el-input type="textarea" :rows="4" v-model="data.detail" placeholder="输入图书简介..." resize="none"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="图书封面">
                <el-upload
                  class="cover-uploader"
                  action="/api/book-manage-sys-api/v1.0/file/upload"
                  :show-file-list="false"
                  :on-success="handleBookCoverSuccess">
                  <img v-if="data.cover" :src="data.cover" class="cover-img">
                  <div v-else class="uploader-placeholder">
                    <i class="el-icon-plus"></i>
                    <span>点击上传封面</span>
                  </div>
                </el-upload>
              </el-form-item>
              <el-form-item label="图书分类" required>
                <el-select v-model="data.categoryId" placeholder="请选择分类" style="width: 100%;">
                  <el-option v-for="item in options" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="所在书架" required>
                <el-select v-model="data.bookshelfId" placeholder="请选择位置" style="width: 100%;">
                  <el-option v-for="item in bookshelfOptions" :key="item.id" :label="bookshelfConfig(item)" :value="item.id"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="库存数量" required>
                <el-input-number v-model="data.num" :min="1" :max="999" style="width: 100%;" controls-position="right"></el-input-number>
              </el-form-item>
              <div class="divider"></div>
              <el-row type="flex" align="middle" justify="space-between">
                <span class="switch-label">是否加入采购计划</span>
                <el-switch v-model="data.isPlanBuy" active-color="#13ce66"></el-switch>
              </el-row>
              <el-form-item v-if="data.isPlanBuy" style="margin-top: 15px;">
                <el-date-picker v-model="data.planBuyTime" type="date" placeholder="预计到馆日期" style="width: 100%;"></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogUserOperaion = false" size="medium">取消</el-button>
        <el-button type="primary" @click="!isOperation ? addOperation() : updateOperation()" size="medium" :loading="submitLoading">
          {{ !isOperation ? '确认入库' : '保存修改' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
const defaultBookQuery = () => ({
  name: '',
  categoryId: null,
  bookshelfId: null
});

export default {
  data() {
    return {
      loading: false,
      submitLoading: false,
      userPwd: '',
      data: { userAvatar: '', isPlanBuy: false, num: 1 },
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogUserOperaion: false,
      isOperation: false,
      tableData: [],
      selectedRows: [],
      bookQueryDto: defaultBookQuery(),
      options: [],
      bookshelfOptions: [],
      lastRefresh: ''
    };
  },
  created() {
    this.fetchFreshData();
    this.fetchCategory();
    this.fetchBookshelf();
  },
  computed: {
    analyticsCards() {
      const planCount = this.tableData.filter(item => item.isPlanBuy).length;
      return [
        { label: '分类数量', value: this.options.length, caption: '可选分类' },
        { label: '架位数量', value: this.bookshelfOptions.length, caption: '可选位置' },
        { label: '采购计划', value: planCount, caption: '待入库图书' },
        { label: '已选中', value: this.selectedRows.length, caption: '批量操作' }
      ];
    }
  },
  methods: {
    handleCoverFallback(event) {
      event.target.src = 'https://placehold.co/60x80/e2e8f0/a0aec0?text=No+Img';
    },
    bookshelfConfig(item) {
      if (!item) return '暂无位置信息';
      return `${item.floor}层${item.area || ''}${item.frame || ''}`;
    },
    handleBookCoverSuccess(res) {
      if (res.code !== 200) {
        this.$message.error(res.msg || '封面上传失败');
        return;
      }
      this.$message.success('封面上传成功');
      this.$set(this.data, 'cover', res.data);
    },
    fetchCategory() {
      this.$axios.post('category/query', {}).then(res => {
        if (res.data.code === 200) this.options = res.data.data;
      }).catch(e => console.error(e));
    },
    fetchBookshelf() {
      this.$axios.post('bookshelf/query', {}).then(res => {
        if (res.data.code === 200) this.bookshelfOptions = res.data.data;
      }).catch(e => console.error(e));
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    async batchDelete() {
      if (!this.selectedRows.length) return;
      const confirmed = await this.$swalConfirm({
        title: '确认删除?',
        text: `即将删除 ${this.selectedRows.length} 本图书，该操作不可恢复。`,
        icon: 'warning',
        confirmButtonText: '确认删除',
        cancelButtonText: '取消'
      });
      if (confirmed) {
        try {
          const ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/book/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$message.success('删除成功');
            this.fetchFreshData();
          } else {
            this.$message.error(response.data.msg);
          }
        } catch (e) {
          this.$message.error('删除操作失败');
        }
      }
    },
    resetQueryCondition() {
      this.bookQueryDto = defaultBookQuery();
      this.currentPage = 1;
      this.fetchFreshData();
    },
    validateForm() {
      if (!this.data.name) { this.$message.warning('请输入图书名称'); return false; }
      if (!this.data.categoryId) { this.$message.warning('请选择图书分类'); return false; }
      if (!this.data.bookshelfId) { this.$message.warning('请选择书架位置'); return false; }
      return true;
    },
    async updateOperation() {
      if (!this.validateForm()) return;
      this.submitLoading = true;
      try {
        const response = await this.$axios.put('/book/update', this.data);
        if (response.data.code === 200) {
          this.$message.success('修改成功');
          this.dialogUserOperaion = false;
          this.fetchFreshData();
        } else {
          this.$message.error(response.data.msg);
        }
      } finally {
        this.submitLoading = false;
      }
    },
    async addOperation() {
      if (!this.validateForm()) return;
      this.submitLoading = true;
      try {
        const response = await this.$axios.post('/book/save', this.data);
        if (response.data.code === 200) {
          this.$message.success('新增成功');
          this.dialogUserOperaion = false;
          this.fetchFreshData();
        } else {
          this.$message.error(response.data.msg);
        }
      } finally {
        this.submitLoading = false;
      }
    },
    clearFormData() {
      this.data = { isPlanBuy: false, num: 1 };
    },
    async fetchFreshData() {
      this.loading = true;
      try {
        this.tableData = [];
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          key: this.bookQueryDto.name || '',
          ...this.bookQueryDto
        };
        const response = await this.$axios.post('/book/query', params);
        const resBody = response.data;
        if (resBody && resBody.data) {
          this.tableData = resBody.data;
          this.totalItems = resBody.total || 0;
          this.lastRefresh = new Date().toLocaleString();
        }
      } catch (error) {
        console.error(error);
        this.$message.error('获取数据失败');
      } finally {
        this.loading = false;
      }
    },
    add() {
      this.clearFormData();
      this.dialogUserOperaion = true;
      this.isOperation = false;
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleFilterClear() {
      this.bookQueryDto = { ...this.bookQueryDto, name: '' };
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
      this.clearFormData();
      this.data = JSON.parse(JSON.stringify(row));
      this.dialogUserOperaion = true;
      this.isOperation = true;
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
        @include neo.neo-button(neo.$neo-purple, lighten(neo.$neo-purple, 8%));
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

.cover-wrapper {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 60px;
  height: 80px;
  border-radius: 6px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.05);
}

.book-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s;

  &:hover {
    transform: scale(1.1);
  }
}

.book-name {
  font-weight: 600;
  color: neo.$neo-text-primary;
}

.info-tag-group {
  display: flex;
  gap: 8px;
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

.cover-uploader {
  ::v-deep .el-upload {
    width: 100%;
    height: 180px;
    border: 2px dashed neo.$neo-border;
    border-radius: 12px;
    cursor: pointer;
    background: rgba(255, 255, 255, 0.02);
    display: flex;
    justify-content: center;
    align-items: center;
  }
}

.uploader-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: neo.$neo-text-secondary;

  i {
    font-size: 28px;
    margin-bottom: 8px;
  }
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.divider {
  height: 1px;
  background-color: rgba(255, 255, 255, 0.08);
  margin: 20px 0;
}

.switch-label {
  font-size: 13px;
  color: neo.$neo-text-secondary;
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
