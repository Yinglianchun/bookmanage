<template>
  <div class="page-container user-manage">
    <section class="page-hero">
      <div class="hero-content">
        <p class="hero-eyebrow">用户管理控制台</p>
        <h1>洞察用户行为，构建精细化体验</h1>
        <p class="hero-description">实时感知用户状态、批量联动操作，打造更具未来感的管理体验。</p>
        <div class="hero-metrics">
          <div class="metric">
            <span>全部用户</span>
            <strong>{{ totalItems }}</strong>
          </div>
          <div class="metric">
            <span>本页显示</span>
            <strong>{{ tableData.length }}</strong>
          </div>
          <div class="metric">
            <span>已选中</span>
            <strong>{{ selectedRows.length }}</strong>
          </div>
        </div>
      </div>
      <div class="hero-actions">
        <el-button class="hero-btn primary" icon="el-icon-plus" size="medium" @click="add">新增用户</el-button>
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
          <h3>高级筛选</h3>
          <p>组合条件快速定位目标用户，支持实时筛选与联动。</p>
        </div>
        <el-tag type="success" effect="dark">智能推荐</el-tag>
      </div>
      <div class="filters-body">
        <el-form label-position="top" size="small" class="filter-form">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="6">
              <el-form-item label="用户名 / 昵称">
                <el-input
                  v-model="userQueryDto.userName"
                  placeholder="输入用户名"
                  clearable
                  prefix-icon="el-icon-user"
                  @clear="handleFilterClear"
                  @keyup.enter.native="handleFilter" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-form-item label="账号关键词">
                <el-input
                  v-model="userQueryDto.userAccount"
                  placeholder="输入账号或邮箱"
                  clearable
                  prefix-icon="el-icon-mobile-phone"
                  @keyup.enter.native="handleFilter" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="4">
              <el-form-item label="角色">
                <el-select v-model="userQueryDto.userRole" placeholder="全部" clearable>
                  <el-option label="管理员" :value="1"></el-option>
                  <el-option label="普通用户" :value="0"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="4">
              <el-form-item label="账号状态">
                <el-select v-model="userQueryDto.isLogin" placeholder="全部" clearable>
                  <el-option label="启用" :value="false"></el-option>
                  <el-option label="封禁" :value="true"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :md="8">
              <el-form-item label="注册时间范围">
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
            <el-col :xs="24" :md="8" class="filters-actions">
              <el-button type="primary" icon="el-icon-search" @click="handleFilter">查询</el-button>
              <el-button type="default" plain icon="el-icon-refresh-right" @click="resetQueryCondition">重置</el-button>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div class="table-card__header">
        <div>
          <h3>用户数据表</h3>
          <p>实时同步账号状态，可直接在列表中联动控制。</p>
        </div>
        <div class="header-badges">
          <span class="badge badge-online"><i class="el-icon-s-opportunity"></i> 实时同步</span>
          <span class="badge badge-safe"><i class="el-icon-lock"></i> 安全校验</span>
        </div>
      </div>
      <div class="table-card__body">
        <div class="table-toolbar">
          <span>已选择 {{ selectedRows.length }} 项</span>
          <span>最近刷新：{{ lastRefresh || '加载中...' }}</span>
        </div>
        <el-table
          :data="tableData"
          stripe
          v-loading="loading"
          @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center"></el-table-column>
          <el-table-column label="用户" min-width="220">
            <template slot-scope="scope">
              <div class="user-info">
                <el-avatar shape="square" :size="46" :src="scope.row.userAvatar" icon="el-icon-user-solid"></el-avatar>
                <div class="user-meta">
                  <span class="user-name">{{ scope.row.userName }}</span>
                  <span class="user-account">ID · {{ scope.row.userAccount }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="userEmail" label="邮箱" min-width="180" show-overflow-tooltip>
            <template slot-scope="scope">
              <span class="text-muted">{{ scope.row.userEmail || '未绑定' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="角色" width="120" align="center">
            <template slot-scope="scope">
              <span :class="['role-chip', scope.row.userRole === 1 ? 'role-admin' : 'role-user']">
                {{ scope.row.userRole === 1 ? '管理员' : '普通用户' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="账号状态" width="180" align="center">
            <template slot-scope="scope">
              <div class="status-wrapper">
                <el-switch
                  v-model="scope.row.isLogin"
                  @change="handleSwitchChange(scope.row.id, scope.row.isLogin, true)">
                </el-switch>
                <span :class="['status-chip', scope.row.isLogin ? 'status-offline' : 'status-online']">
                  {{ scope.row.isLogin ? '已禁用' : '正常' }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="注册时间" width="180" sortable></el-table-column>
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template slot-scope="scope">
              <div class="table-actions">
                <el-tooltip content="编辑" placement="top">
                  <button class="action-btn" @click="handleEdit(scope.row)">
                    <i class="el-icon-edit"></i>
                  </button>
                </el-tooltip>
                <el-tooltip content="消息" placement="top">
                  <button class="action-btn" @click="messagePush(scope.row)">
                    <i class="el-icon-chat-dot-square"></i>
                  </button>
                </el-tooltip>
              </div>
              <el-button type="text" class="danger-link" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
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
      width="520px"
      append-to-body>
      <div slot="title" class="dialog-title">
        <span>{{ !isOperation ? '新增用户' : '编辑用户' }}</span>
        <el-button type="text" icon="el-icon-close" @click="dialogUserOperaion = false"></el-button>
      </div>
      <div class="dialog-body">
        <el-form :model="data" label-position="top" size="small">
          <div class="avatar-upload">
            <el-upload
              class="avatar-uploader"
              action="/api/book-manage-sys-api/v1.0/file/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess">
              <img v-if="data.userAvatar" :src="data.userAvatar" class="avatar" />
              <div v-else class="avatar-placeholder">
                <i class="el-icon-camera"></i>
                <span>上传头像</span>
              </div>
            </el-upload>
          </div>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名" required>
                <el-input v-model="data.userName" placeholder="请输入用户名"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="账号" required>
                <el-input v-model="data.userAccount" placeholder="登录账号"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="邮箱">
            <el-input v-model="data.userEmail" placeholder="example@mail.com" prefix-icon="el-icon-message"></el-input>
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="userPwd" type="password" placeholder="留空则不修改密码" show-password prefix-icon="el-icon-lock"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogUserOperaion = false">取消</el-button>
        <el-button type="primary" @click="!isOperation ? addOperation() : updateOperation()">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :visible.sync="dialogMessageOperation"
      :show-close="false"
      width="420px"
      append-to-body>
      <div slot="title" class="dialog-title">
        <span>发送系统消息</span>
        <el-button type="text" icon="el-icon-close" @click="dialogMessageOperation = false"></el-button>
      </div>
      <div class="dialog-body">
        <el-input
          type="textarea"
          :rows="5"
          placeholder="请输入要推送给该用户的消息..."
          v-model="data.content"
          resize="none">
        </el-input>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogMessageOperation = false">取消</el-button>
        <el-button type="primary" icon="el-icon-s-promotion" @click="messagePushOperation">立即推送</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
const createUserQuery = () => ({
  userName: '',
  userAccount: '',
  userRole: null,
  isLogin: null,
});

export default {
  data() {
    return {
      loading: false,
      userPwd: '',
      data: { userAvatar: '' },
      filterText: '',
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      dialogMessageOperation: false,
      dialogUserOperaion: false,
      isOperation: false,
      tableData: [],
      searchTime: [],
      selectedRows: [],
      status: null,
      userQueryDto: createUserQuery(),
      messsageContent: '',
      lastRefresh: ''
    };
  },
  watch: {
    dialogUserOperaion(newVal, oldVal) {
      if (!newVal && oldVal) {
        this.isOperation = false;
        this.data = {};
        this.userPwd = '';
      }
    },
  },
  created() {
    this.fetchFreshData();
  },
  computed: {
    analyticsCards() {
      const adminCount = this.tableData.filter(item => item.userRole === 1).length;
      const lockedCount = this.tableData.filter(item => item.isLogin).length;
      return [
        { label: '管理员', value: adminCount, caption: '当前页权限账号' },
        { label: '封禁账号', value: lockedCount, caption: lockedCount ? '需关注' : '全部正常' },
        { label: '列表数据', value: this.tableData.length, caption: '当前页记录' },
        { label: '已选中', value: this.selectedRows.length, caption: '准备批量操作' },
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
          this.$message.success('推送成功');
          this.dialogMessageOperation = false;
          this.data = {};
        }
      });
    },
    handleAvatarSuccess(res) {
      if (res.code !== 200) {
        this.$message.error('上传失败');
        return;
      }
      this.$message.success('上传成功');
      this.data.userAvatar = res.data;
    },
    async handleSwitchChange(id, status, operation) {
      try {
        const param = { id };
        if (operation) {
          param.isLogin = status;
        } else {
          param.isWord = status;
        }
        const response = await this.$axios.put(`/user/backUpdate`, param);
        if (response.data.code === 200) {
          this.$message.success(status ? '已禁用该用户' : '账号状态已恢复正常');
        }
      } catch (e) {
        console.error(`更新异常: ${e}`);
      }
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection;
    },
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message.warning('请先选择数据');
        return;
      }
      const confirmed = await this.$swalConfirm({
        title: '删除确认',
        text: `确认删除选中的 ${this.selectedRows.length} 位用户吗？`,
        icon: 'warning',
      });
      if (confirmed) {
        try {
          const ids = this.selectedRows.map(entity => entity.id);
          const response = await this.$axios.post(`/user/batchDelete`, ids);
          if (response.data.code === 200) {
            this.$message.success('删除成功');
            this.fetchFreshData();
          }
        } catch (e) {
          this.$message.error('删除失败');
        }
      }
    },
    resetQueryCondition() {
      this.userQueryDto = createUserQuery();
      this.searchTime = [];
      this.currentPage = 1;
      this.fetchFreshData();
    },
    async updateOperation() {
      if (this.userPwd !== '') {
        const pwd = this.$md5(this.$md5(this.userPwd));
        this.data.userPwd = pwd;
      } else {
        this.data.userPwd = null;
      }
      try {
        const response = await this.$axios.put('/user/backUpdate', this.data);
        if (response.data.code === 200) {
          this.$message.success('修改成功');
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        } else {
          this.$message.error(response.data.msg);
        }
      } catch (error) {
        this.$message.error('提交失败');
      }
    },
    async addOperation() {
      if (this.userPwd !== '') {
        this.data.userPwd = this.$md5(this.$md5(this.userPwd));
      } else {
        this.data.userPwd = null;
      }
      try {
        const response = await this.$axios.post('/user/insert', this.data);
        if (response.data.code === 200) {
          this.$message.success('新增成功');
          this.closeDialog();
          this.fetchFreshData();
          this.clearFormData();
        } else {
          this.$message.error(response.data.msg);
        }
      } catch (error) {
        this.$message.error('提交失败');
      }
    },
    closeDialog() {
      this.dialogUserOperaion = false;
    },
    clearFormData() {
      this.data = {};
      this.userPwd = '';
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
          key: this.userQueryDto.userName || '',
          startTime,
          endTime,
          ...this.userQueryDto
        };
        const response = await this.$axios.post('/user/query', params);
        const { data } = response;
        this.tableData = data.data;
        this.totalItems = data.total;
        this.lastRefresh = new Date().toLocaleString();
      } catch (error) {
        console.error('查询异常:', error);
      } finally {
        this.loading = false;
      }
    },
    add() {
      this.isOperation = false;
      this.data = {};
      this.userPwd = '';
      this.dialogUserOperaion = true;
    },
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    handleFilterClear() {
      this.userQueryDto = { ...this.userQueryDto, userName: '' };
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
  margin-bottom: 26px;
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
        background: transparent;
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

    .hero-btn:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
}

.filters-card,
.table-card {
  @include neo.neo-glass-card;
  margin-bottom: 24px;

  // 统一卡片与表单背景，避免出现分层割裂感
  background: linear-gradient(145deg, rgba(14, 18, 35, 0.92), rgba(20, 27, 48, 0.96));
  border: 1px solid rgba(255, 255, 255, 0.08);

  ::v-deep .el-card__body {
    background: transparent;
  }
}

.insights-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin: 0 0 28px;

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

.filter-form {
  background: linear-gradient(145deg, rgba(15, 23, 42, 0.92), rgba(26, 35, 58, 0.98));
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 16px;

  ::v-deep .el-form-item__label {
    color: neo.$neo-text-secondary;
    letter-spacing: 0.02em;
  }

  ::v-deep .el-input__inner,
  ::v-deep .el-textarea__inner,
  ::v-deep .el-select .el-input__inner,
  ::v-deep .el-date-editor {
    background: rgba(30, 41, 59, 0.7);
    border-color: rgba(255, 255, 255, 0.06);
    color: neo.$neo-text-primary;

    &:hover,
    &.is-focus,
    &:focus {
      border-color: rgba(96, 165, 250, 0.6);
      box-shadow: 0 0 0 1px rgba(96, 165, 250, 0.2);
    }
  }
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
    font-weight: 600;

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

  // 修复表格悬停效果，确保文字清晰可见
  ::v-deep .el-table {
    .el-table__body-wrapper {
      .el-table__body {
        tr {
          td {
            color: neo.$neo-text-primary !important;
            // 使用统一的基础背景色，避免颜色冲突
            background: rgba(15, 23, 42, 0.4) !important;
            border-bottom: 1px solid rgba(255, 255, 255, 0.04) !important;
            // 优化过渡动画，移除 all 避免性能问题和延迟感
            transition: background-color 0.15s ease, box-shadow 0.15s ease;
          }
        }

        // stripe行的样式（偶数行）- 调整为更柔和的差异色
        tr.el-table__row--striped {
          td {
            background: rgba(22, 33, 62, 0.4) !important;
          }
        }

        // 悬停效果 - 简化阴影逻辑，提升响应速度
        tr:hover {
          td {
            background: rgba(30, 41, 59, 0.9) !important;
            // 移除过于复杂的 box-shadow 计算，或简化它
            box-shadow: inset 0 0 0 1px rgba(96, 165, 250, 0.2);
            z-index: 1; // 确保悬浮时层级正确
          }
        }

        // stripe行悬停时的样式 - 保持一致性
        tr.el-table__row--striped:hover {
          td {
            background: rgba(30, 41, 59, 0.9) !important;
            box-shadow: inset 0 0 0 1px rgba(96, 165, 250, 0.2);
          }
        }

        // 选中行的样式
        tr.current-row {
          td {
            background: rgba(54, 207, 201, 0.15) !important;
          }
        }
      }
    }

    // 确保表头文字清晰 - 略微增加对比度
    .el-table__header-wrapper {
      th {
        color: neo.$neo-text-secondary !important;
        background: rgba(8, 13, 28, 0.95) !important;
      }
    }
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;

  .user-meta {
    display: flex;
    flex-direction: column;

    .user-name {
      color: neo.$neo-text-primary;
      font-weight: 600;
    }

    .user-account {
      color: neo.$neo-text-muted;
      font-size: 12px;
    }
  }
}

.text-muted {
  color: neo.$neo-text-secondary;
}

.role-chip {
  @include neo.neo-chip(neo.$neo-blue);
  &.role-admin {
    @include neo.neo-chip(neo.$neo-purple);
  }
}

.status-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;

  ::v-deep .el-switch {
    --el-switch-on-color: #fb7185;
    --el-switch-off-color: #34d399;
  }

  .status-chip {
    @include neo.neo-chip(neo.$neo-green);
    min-width: 70px;
    text-align: center;

    &.status-offline {
      @include neo.neo-chip(neo.$neo-red);
    }
  }
}

.table-actions {
  display: inline-flex;
  gap: 10px;

  .action-btn {
    width: 34px;
    height: 34px;
    border-radius: 10px;
    border: 1px solid neo.$neo-border;
    background: transparent;
    color: neo.$neo-text-secondary;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      color: neo.$neo-cyan;
      border-color: neo.$neo-cyan;
    }
  }
}

.danger-link {
  color: #ff7875 !important;
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

.avatar-upload {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;

  .avatar-uploader {
    ::v-deep .el-upload {
      border: 1px dashed neo.$neo-border;
      border-radius: 18px;
      padding: 12px;
      width: 120px;
      height: 120px;
      background: rgba(255, 255, 255, 0.02);
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .avatar-placeholder {
    text-align: center;
    color: neo.$neo-text-secondary;

    i {
      font-size: 32px;
      display: block;
      margin-bottom: 6px;
    }
  }

  .avatar {
    width: 100%;
    height: 100%;
    border-radius: 14px;
    object-fit: cover;
  }
}

@include neo.neo-dialog;

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