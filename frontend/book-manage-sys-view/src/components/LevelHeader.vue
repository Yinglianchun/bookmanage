<template>
    <div class="main">
        <span>
            <span class="operation-span-tag">
                {{ tag == '' ? '元数据' : tag }}
            </span>
        </span>
        <span class="user-block">
            <el-dropdown class="user-dropdown">
                <span class="el-dropdown-link" style="display: flex; align-items: center;">
                    <el-avatar :size="35" :src="userInfo.url" style="margin-top: 0;"></el-avatar>
                    <span class="userName" style="margin-left: 5px;font-size: 16px;">{{ userInfo.name }}</span>
                    <i class="el-icon-arrow-down el-icon--right" style="margin-left: 5px;"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-user-solid" @click.native="userCenterPanel">个人资料</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-s-fold" @click.native="loginOut">退出登录</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </span>
    </div>
</template>
<script>
export default {
    name: "LevelHeader",
    data() {
        return {};
    },
    props: {
        tag: {
            type: String,
            required: true,
            default: ''
        },
        userInfo: {
            type: Object,
            required: true,
            default: {}
        },
        bag: {
            type: String,
            default: ''
        },
    },
    methods: {
        // 个人中心，传回父组件处理
        userCenterPanel() {
            this.$emit('eventListener', 'center');
        },
        // 退出登录，传回父组件处理
        loginOut() {
            this.$emit('eventListener', 'loginOut');
        },
    }
};
</script>
<style scoped lang="scss">
@use '@/views/admin/styles/neo-theme.scss' as neo;

.main {
    padding: 20px 30px 18px 10px;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    width: 100%;
    position: relative;
    background: transparent;
    color: neo.$neo-text-primary;
    font-family: neo.$neo-font-stack;

    &::after {
        content: '';
        position: absolute;
        inset: 0 20px;
        height: 1px;
        bottom: 0;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.18), transparent);
        opacity: 0.7;
    }

    .operation-span-tag {
        padding: 8px 12px;
        border-radius: 999px;
        font-size: 20px;
        user-select: none;
        margin-top: 10px;
        margin-left: 20px;
        color: neo.$neo-text-primary;
        background: rgba(255, 255, 255, 0.06);
        letter-spacing: 0.05em;
    }

    span {
        color: neo.$neo-text-primary;
    }

    .user-block {
        position: absolute;
        right: 40px;

        .userName {
            display: inline-block;
            vertical-align: middle;
            font-size: 16px;
            cursor: pointer;
            user-select: none;
            color: neo.$neo-text-secondary;
            transition: color 0.2s ease;

            &:hover {
                color: neo.$neo-text-primary;
            }
        }
    }
}
</style>
