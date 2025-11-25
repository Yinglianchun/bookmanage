<template>
    <div class="menu-container">
        <aside class="menu-side" :class="{ 'menu-side-narrow': flag }">
            <div class="side-brand">
                <Logo name="图书管理" :flag="flag" :bag="colorLogo" />
                <p v-if="!flag" class="brand-subtitle">Premium Library Console · Aurora</p>
            </div>
            <div class="side-menu">
                <AdminMenu :flag="flag" :routes="adminRoutes" :bag="bagMenu" @select="handleRouteSelect" />
            </div>
            <div class="side-tip" v-if="!flag">
                <p>一处掌控借阅、分类与用户行为。</p>
                <span>v2.0 · Enhanced Typography</span>
            </div>
        </aside>
        <main class="main">
            <div class="header-section">
                <LevelHeader @eventListener="eventListener" :tag="tag"
                    :userInfo="userInfo" />
            </div>
            <div class="content-section">
                <router-view></router-view>
            </div>
        </main>

        <!-- 个人中心 -->
        <el-dialog
            :visible.sync="dialogOperaion"
            :show-close="false"
            width="480px"
            custom-class="modern-dialog"
            append-to-body>
            <div slot="title" class="dialog-header">
                <span class="title-text">个人中心</span>
                <button class="close-btn" @click="dialogOperaion = false"><i class="el-icon-close"></i></button>
            </div>

            <div class="dialog-body">
                <el-form label-position="top" size="small">
                    <div class="avatar-wrapper">
                        <el-upload
                            class="avatar-uploader-round"
                            action="/api/book-manage-sys-api/v1.0/file/upload"
                            :show-file-list="false"
                            :on-success="handleAvatarSuccess">
                            <img v-if="userInfo.url" :src="userInfo.url" class="avatar">
                            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                            <div class="upload-tip">点击上传头像</div>
                        </el-upload>
                    </div>

                    <el-form-item label="用户名" required>
                        <el-input v-model="userInfo.name" placeholder="请输入用户名" prefix-icon="el-icon-user"></el-input>
                    </el-form-item>

                    <el-form-item label="邮箱地址" required>
                        <el-input v-model="userInfo.email" placeholder="example@email.com" prefix-icon="el-icon-message"></el-input>
                    </el-form-item>
                </el-form>
            </div>

            <div slot="footer" class="dialog-footer">
                <el-button size="medium" @click="dialogOperaion = false">取消</el-button>
                <el-button type="primary" size="medium" icon="el-icon-check" @click="updateUserInfo">保存修改</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import request from "@/utils/request.js";
import router from "@/router/index";
import { clearToken } from "@/utils/storage"
import AdminMenu from '@/components/VerticalMenu.vue';
import Logo from '@/components/Logo.vue';
import LevelHeader from '@/components/LevelHeader.vue';
export default {
    name: "Admin",
    components: {
        Logo,
        LevelHeader,
        AdminMenu
    },
    data() {
        return {
            adminRoutes: [],
            activeIndex: '',
            userInfo: {
                id: null,
                url: '',
                name: '',
                role: null,
                email: ''
            },
            flag: false,
            tag: '可视区',
            bag: 'rgb(250, 250, 250)',
            colorLogo: '#cbd5e1',
            bagMenu: 'rgba(12,18,34,0.7)',
            dialogOperaion: false
        };
    },
    created() {
        let menus = router.options.routes.filter(route => route.path == '/admin')[0];
        this.adminRoutes = menus.children;
        this.tokenCheckLoad();
        this.menuOperationHistory();
    },

    methods: {
        async updateUserInfo() {
            try {
                const userUpdateDTO = {
                    userAvatar: this.userInfo.url,
                    userName: this.userInfo.name,
                    userEmail: this.userInfo.email
                }
                const resposne = await this.$axios.put(`/user/update`, userUpdateDTO);
                const { data } = resposne;
                if (data.code === 200) {
                    this.dialogOperaion = false;
                    this.tokenCheckLoad();
                    this.$swal.fire({
                        title: '修改个人信息',
                        text: data.msg,
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1000,
                    });
                }
            } catch (e) {
                this.dialogOperaion = false;
                this.$swal.fire({
                    title: '修改个人信息出错',
                    text: e,
                    icon: 'error',
                    showConfirmButton: false,
                    timer: 2000,
                });
                console.error(`修改个人信息出错:${e}`);
            }
        },
        handleAvatarSuccess(res, file) {
            if (res.code !== 200) {
                this.$message.error(`头像上传出错`);
                return;
            }
            this.$message.success(`头像上传成功`);
            this.userInfo.url = res.data;
        },
        eventListener(event) {
            if (event === 'center') {
                this.dialogOperaion = true;
            }
            if (event === 'loginOut') {
                this.loginOut();
            }
        },
        async loginOut() {
            const confirmed = await this.$swalConfirm({
                title: '确定退出登录吗？',
                text: `退出后需要重新登录哦~`,
                icon: 'warning',
            });
            if (confirmed) {
                this.$swal.fire({
                    title: '退出登录成功',
                    text: '1秒后返回登录页面',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: 1000,
                });
                setTimeout(() => {
                    clearToken();
                    this.$router.push("/login");
                }, 1000)
            }
        },
        menuOperationHistory() {
            this.flag = sessionStorage.getItem('flag') === 'true';
        },
        handleRouteSelect(index) {
            let ary = this.adminRoutes.filter(entity => entity.path == index);
            this.tag = ary[0].name;
            if (this.$router.currentRoute.fullPath == index) {
                return;
            }
            this.$router.push(index);
        },
        async tokenCheckLoad() {
            try {
                const res = await request.get('user/auth');
                if (res.data.code === 400) {
                    this.$message.error(res.data.msg);
                    this.$router.push('/login');
                    return;
                }
                const { id, userAvatar: url, userName: name, userRole: role, userEmail: email } = res.data.data;
                this.userInfo = { id, url, name, role, email };
                const rolePath = role === 1 ? '/admin' : '/user';
                const targetMenu = router.options.routes.find(route => route.path === rolePath);
                if (targetMenu) {
                    this.routers = targetMenu.children;
                } else {
                    console.warn(`未找到与角色对应的路由:${rolePath}`);
                }
            } catch (error) {
                console.error('获取用户认证信息时发生错误', error);
                this.$message.error('认证信息加载失败，请重试！');
            }
        },
    }
};
</script>
<style scoped lang="scss">
@use './styles/neo-theme.scss' as neo;

.menu-container {
    display: flex;
    min-height: 100vh;
    width: 100%;
    background: neo.$neo-page-bg;
    position: relative;
    overflow: hidden;
    @include neo.neo-typography;

    &::before {
        content: '';
        position: absolute;
        inset: 0;
        background:
            radial-gradient(circle at 20% 20%, rgba(96, 165, 250, 0.18), transparent 45%),
            radial-gradient(circle at 80% 80%, rgba(192, 132, 252, 0.15), transparent 50%);
        pointer-events: none;
    }
}

.menu-side {
    width: 260px;
    min-width: 120px;
    padding: 32px 18px 24px;
    box-sizing: border-box;
    transition: width 0.3s ease;
    background: rgba(8, 12, 30, 0.9);
    border-right: 1px solid rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(28px);
    display: flex;
    flex-direction: column;
    gap: 18px;
    z-index: 2;

    &.menu-side-narrow {
        width: 120px;
        padding-inline: 14px;
    }
}

.side-brand {
    display: flex;
    flex-direction: column;
    gap: 6px;

    .brand-subtitle {
        margin: 0;
        font-size: 12px;
        color: neo.$neo-text-muted;
        letter-spacing: 0.08em;
    }
}

.side-menu {
    flex: 1;
    overflow-y: auto;
    padding-right: 4px;
}

.side-tip {
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 14px;
    padding: 16px;
    background: rgba(255, 255, 255, 0.02);
    font-size: 12px;
    color: neo.$neo-text-secondary;

    span {
        display: block;
        margin-top: 6px;
        color: neo.$neo-text-muted;
    }
}

.main {
    flex: 1;
    display: flex;
    flex-direction: column;
    position: relative;
    z-index: 1;
    background: linear-gradient(135deg, rgba(15, 23, 42, 0.65), rgba(10, 15, 32, 0.85));
    backdrop-filter: blur(20px);
}

.header-section {
    padding: 20px 32px 10px 32px;
}

.content-section {
    flex: 1;
    padding: 0 32px 32px;
    overflow-x: hidden;
    overflow-y: auto;
}

.avatar-wrapper {
    display: flex;
    justify-content: center;
    margin-bottom: 24px;
}

@include neo.neo-dialog;
</style>
