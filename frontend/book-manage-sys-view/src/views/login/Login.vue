<template>
    <div class="login-page">
        <div class="login-grid">
            <section class="login-showcase">
                <div class="showcase-brand">
                    <Logo name="图书馆管理系统" :bag="colorLogo" />
                    <p>Smart Library Management · 更清晰的字体、更沉浸的体验。</p>
                </div>
                <div class="showcase-metrics">
                    <div class="metric-card">
                        <span>今日借阅</span>
                        <strong>268</strong>
                        <small>+12% DoD</small>
                    </div>
                    <div class="metric-card">
                        <span>活跃用户</span>
                        <strong>1,482</strong>
                        <small>实时监控</small>
                    </div>
                    <div class="metric-card">
                        <span>反馈响应</span>
                        <strong>98%</strong>
                        <small>平均 23 分钟</small>
                    </div>
                </div>
            </section>
            <section class="login-card">
                <div class="card-head">
                    <h1>欢迎回来</h1>
                    <p>输入账号和密码，继续你的管理旅程。</p>
                </div>
                <el-form label-position="top" @submit.native.prevent>
                    <el-form-item label="账号">
                        <el-input v-model="act" placeholder="输入账号" clearable prefix-icon="el-icon-user" @keyup.enter.native="login" />
                    </el-form-item>
                    <el-form-item label="密码">
                        <el-input v-model="pwd" type="password" placeholder="输入密码" prefix-icon="el-icon-lock" show-password @keyup.enter.native="login" />
                    </el-form-item>
                </el-form>
                <el-button class="action-button" type="primary" size="large" @click="login">立即登录</el-button>
                <div class="card-foot">
                    <span>还没有账号？</span>
                    <button class="link-button" @click="toDoRegister">去注册</button>
                </div>
            </section>
        </div>
    </div>
</template>

<script>
const ADMIN_ROLE = 1;
const USER_ROLE = 2;
const DELAY_TIME = 1300;
import request from "@/utils/request.js";
import { setToken } from "@/utils/storage.js";
import md5 from 'js-md5';
import Logo from '@/components/Logo.vue';
export default {
    name: "Login",
    components: { Logo },
    data() {
        return {
            act: '',
            pwd: '',
            colorLogo: 'rgb(38,38,38)',
        }
    },
    created(){
        this.defaultLoad();
    },
    methods: {
        defaultLoad() {
            const token = sessionStorage.getItem('token');
            if (token === undefined || token === null || token === '') {
                return;
            }
            this.$axios.get('user/auth').then(response => {
                const { data } = response;
                if (data.code === 400) {
                    return;
                }
                if(data.data.userRole === 1){
                    this.$router.push('/admin');
                }else{
                    this.$router.push('/user');
                }
            })
        },
        toDoRegister() {
            this.$router.push('/register');
        },
        async login() {
            if (!this.act || !this.pwd) {
                this.$swal.fire({
                    title: '请填写校验',
                    text: '账号或密码不能为空',
                    icon: 'error',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                return;
            }
            const hashedPwd = md5(md5(this.pwd));
            const paramDTO = { userAccount: this.act, userPwd: hashedPwd };
            try {
                const { data } = await request.post(`user/login`, paramDTO);
                if (data.code !== 200) {
                    this.$swal.fire({
                        title: '登录失败',
                        text: data.msg,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: DELAY_TIME,
                    });
                    return;
                }
                setToken(data.data.token);
                this.$swal.fire({
                    title: '登录成功',
                    text: '即将进入系统...',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                setTimeout(() => {
                    const { role } = data.data;
                    this.navigateToRole(role);
                }, DELAY_TIME);
            } catch (error) {
                console.error('登录请求出错:', error);
                this.$message.error('登录请求失败，请稍后重试');
            }
        },
        navigateToRole(role) {
            switch (role) {
                case ADMIN_ROLE:
                    this.$router.push('/admin');
                    break;
                case USER_ROLE:
                    this.$router.push('/user');
                    break;
                default:
                    console.warn('未知的用户角色类型?', role);
                    break;
            }
        },
    }
};
</script>

<style lang="scss" scoped>
@use '../admin/styles/neo-theme.scss' as neo;

.login-page {
    min-height: 100vh;
    background: neo.$neo-page-bg;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px 24px;
    @include neo.neo-typography;
}

.login-grid {
    width: 100%;
    max-width: 1100px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 32px;
}

.login-showcase {
    border-radius: 32px;
    padding: 40px;
    background: linear-gradient(140deg, rgba(56, 189, 248, 0.25), rgba(129, 140, 248, 0.15));
    border: 1px solid rgba(255, 255, 255, 0.08);
    backdrop-filter: blur(30px);
    color: neo.$neo-text-primary;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-height: 420px;
}

.showcase-brand p {
    margin-top: 12px;
    color: neo.$neo-text-secondary;
}

.showcase-metrics {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
    gap: 16px;
    margin-top: 32px;
}

.metric-card {
    padding: 18px;
    border-radius: 18px;
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(255, 255, 255, 0.05);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.12);

    span {
        font-size: 12px;
        color: neo.$neo-text-muted;
    }

    strong {
        display: block;
        margin: 10px 0 4px;
        font-size: 28px;
    }

    small {
        color: neo.$neo-text-secondary;
    }
}

.login-card {
    @include neo.neo-glass-card;
    padding: 36px;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.card-head h1 {
    margin: 0;
    font-size: 28px;
    color: neo.$neo-text-primary;
}

.card-head p {
    margin-top: 8px;
    color: neo.$neo-text-secondary;
}

::v-deep .el-form-item__label {
    color: neo.$neo-text-secondary;
    font-weight: 500;
}

::v-deep .el-input__inner {
    height: 48px;
}

.action-button {
    width: 100%;
    height: 50px;
    font-size: 16px;
    @include neo.neo-button(#34d399, #2dd4bf);
}

.card-foot {
    text-align: center;
    color: neo.$neo-text-secondary;
    font-size: 14px;
}

.link-button {
    background: transparent;
    border: none;
    color: #5ef0d4;
    margin-left: 6px;
    cursor: pointer;
}

@media (max-width: 768px) {
    .login-card {
        padding: 28px;
    }
}
</style>
