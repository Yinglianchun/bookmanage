<template>
    <div class="page-container">
        <el-card shadow="never" class="editor-card">
            <div slot="header" class="card-header">
                <el-page-header @back="$router.go(-1)" :content="noticeOperation === 'save' ? '发布新公告' : '编辑公告'">
                </el-page-header>
            </div>
            
            <div class="form-body">
                <div class="title-input-wrapper">
                    <input 
                        type="text" 
                        class="title-input" 
                        placeholder="请输入公告标题..." 
                        v-model="notice.name"
                    >
                </div>
                
                <div class="editor-wrapper">
                    <!-- 假设 Editor 组件支持 style 或 class 传递，这里保持原样但包裹容器 -->
                    <Editor height="calc(100vh - 350px)" :receiveContent="notice.content" @on-receive="receiveData" />
                </div>
            </div>

            <div class="action-footer">
                <el-button @click="$router.go(-1)">取消</el-button>
                <el-button type="primary" icon="el-icon-s-promotion" @click="operation">
                    {{ noticeOperation === 'save' ? '立即发布' : '保存修改' }}
                </el-button>
            </div>
        </el-card>
    </div>
</template>

<script>
// 逻辑保持不变
import Editor from "@/components/Editor"
export default {
    components: { Editor },
    data() {
        return {
            notice: {},
            saveApi: '/notice/save',
            updateApi: '/notice/update',
            noticeOperation: ''
        }
    },
    created() {
        this.loadOperation();
    },
    methods: {
        operation() {
            if (!this.notice.name) {
                this.$message.warning('请输入公告标题');
                return;
            }
            if (this.noticeOperation === 'save') {
                this.save();
                return;
            }
            this.update();
        },
        loadOperation() {
            const operation = sessionStorage.getItem('noticeOperation');
            if (operation === 'update') {
                const notice = sessionStorage.getItem('noticeInfo');
                this.notice = JSON.parse(notice);
            }
            this.noticeOperation = sessionStorage.getItem('noticeOperation');
        },
        receiveData(html) {
            this.notice.content = html;
        },
        update() {
            this.$axios.put(this.updateApi, this.notice).then(response => {
                if (response.data.code === 200) {
                    this.$message.success('修改成功');
                    this.$router.go(-1);
                }
            });
        },
        save() {
            this.$axios.post(this.saveApi, this.notice).then(response => {
                if (response.data.code === 200) {
                    this.$message.success('新增成功');
                    this.$router.go(-1);
                }
            });
        },
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    background-color: #f5f7fa;
    min-height: 100vh;
    padding: 20px;
    display: flex;
    justify-content: center;
}

.editor-card {
    width: 100%;
    max-width: 1000px;
    border-radius: 8px;
    border: none;
    display: flex;
    flex-direction: column;
    min-height: calc(100vh - 40px);

    ::v-deep .el-card__body {
        flex: 1;
        display: flex;
        flex-direction: column;
        padding: 0;
    }
}

.card-header {
    padding: 5px 0;
}

.form-body {
    flex: 1;
    padding: 30px 40px;
    display: flex;
    flex-direction: column;
}

.title-input-wrapper {
    margin-bottom: 20px;
    border-bottom: 1px solid #ebeef5;
}

.title-input {
    width: 100%;
    border: none;
    outline: none;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    padding: 15px 0;
    background: transparent;

    &::placeholder {
        color: #c0c4cc;
    }
}

.editor-wrapper {
    flex: 1;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    overflow: hidden;
}

.action-footer {
    padding: 20px 40px;
    border-top: 1px solid #ebeef5;
    display: flex;
    justify-content: flex-end;
    background-color: #fff;
    border-radius: 0 0 8px 8px;
}
</style>