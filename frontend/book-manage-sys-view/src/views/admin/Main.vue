<template>
    <div class="dashboard-container">
        <!-- 顶部统计卡片区域 -->
        <div class="stats-section">
            <div class="stats-grid">
                <div class="stat-card" v-for="(stat, index) in statsData" :key="index">
                    <div class="stat-icon" :style="{ background: stat.gradient }">
                        <i :class="stat.icon" class="stat-icon-inner"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-value">{{ stat.value }}</div>
                        <div class="stat-label">{{ stat.label }}</div>
                        <div class="stat-trend" :class="stat.trendClass">
                            <i :class="stat.trendIcon"></i>
                            {{ stat.trend }}%
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 图表区域 -->
        <div class="charts-section">
            <div class="charts-grid">
                <!-- 用户增长趋势 -->
                <div class="chart-card chart-card-wide">
                    <div class="chart-header">
                        <h3>用户增长趋势</h3>
                        <div class="chart-controls">
                            <el-button-group size="mini">
                                <el-button :type="timeRange === '7' ? 'primary' : 'default'"
                                    @click="userDatesSelected(7)">7天</el-button>
                                <el-button :type="timeRange === '30' ? 'primary' : 'default'"
                                    @click="userDatesSelected(30)">30天</el-button>
                                <el-button :type="timeRange === '365' ? 'primary' : 'default'"
                                    @click="userDatesSelected(365)">一年</el-button>
                            </el-button-group>
                        </div>
                    </div>
                    <!-- 关键修改：style直接绑定高度，确保传给子组件有据可依 -->
                    <div class="chart-body">
                        <LineChart height="100%" tag="用户数" @on-selected="userDatesSelected" :values="userValues"
                            :date="userDates" />
                    </div>
                </div>

                <!-- 数据分布 -->
                <div class="chart-card chart-card-spotlight">
                    <div class="chart-header">
                        <h3>数据分布概览</h3>
                    </div>
                    <div class="chart-body">
                        <PieChart fontColor="#fff" tag="基础数据" height="100%" :values="pieValues" :types="pieTypes" />
                    </div>
                </div>
            </div>
        </div>

        <!-- 最新公告区域 -->
        <div class="notices-section">
            <div class="notices-card">
                <div class="notices-header">
                    <h3><i class="el-icon-bell"></i> 最新公告</h3>
                    <el-button type="text" size="small" @click="$router.push('/admin/noticeManage')">
                        查看全部 <i class="el-icon-arrow-right"></i>
                    </el-button>
                </div>
                <div class="notices-list">
                    <div class="notice-item" v-for="(notice, index) in noticeList" :key="index">
                        <div class="notice-dot" :style="{ background: getNoticeColor(index) }"></div>
                        <div class="notice-content">
                            <div class="notice-title">{{ notice.name }}</div>
                            <div class="notice-time">
                                <i class="el-icon-time"></i>
                                {{ formatTime(notice.createTime) }}
                            </div>
                        </div>
                        <div class="notice-action">
                            <el-button type="text" size="mini" @click="viewNotice(notice)">
                                <i class="el-icon-view"></i>
                            </el-button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import LineChart from "@/components/LineChart"
import PieChart from "@/components/PieChart"
export default {
    components: { LineChart, PieChart },
    data() {
        return {
            userValues: [],
            userDates: [],
            pieValues: [],
            pieTypes: [],
            noticeList: [],
            timeRange: '365',
            statsData: [
                {
                    label: '总用户数',
                    value: '0',
                    icon: 'el-icon-user-solid',
                    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    trend: '+12.5',
                    trendIcon: 'el-icon-top',
                    trendClass: 'trend-up'
                },
                {
                    label: '总书籍数',
                    value: '0',
                    icon: 'el-icon-reading',
                    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                    trend: '+8.2',
                    trendIcon: 'el-icon-top',
                    trendClass: 'trend-up'
                },
                {
                    label: '借阅次数',
                    value: '0',
                    icon: 'el-icon-s-order',
                    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
                    trend: '+15.3',
                    trendIcon: 'el-icon-top',
                    trendClass: 'trend-up'
                },
                {
                    label: '活跃用户',
                    value: '0',
                    icon: 'el-icon-star-on',
                    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
                    trend: '-2.1',
                    trendIcon: 'el-icon-bottom',
                    trendClass: 'trend-down'
                }
            ]
        }
    },
    created() {
        this.userDatesSelected(365);
        this.loadPieCharts();
        this.loadMessage();
        this.loadStatsData();
    },
    methods: {
        // 加载统计数据
        async loadStatsData() {
            try {
                // 获取用户总数 - 通过查询接口获取总数
                const userQueryDto = { current: 1, size: 1 };
                const userResponse = await this.$axios.post('/user/query', userQueryDto);
                if (userResponse.data.code === 200) {
                    this.statsData[0].value = (userResponse.data.total || 0).toLocaleString();
                }

                // 获取书籍总数 - 通过查询接口获取总数
                const bookQueryDto = { current: 1, size: 1 };
                const bookResponse = await this.$axios.post('/book/query', bookQueryDto);
                if (bookResponse.data.code === 200) {
                    this.statsData[1].value = (bookResponse.data.total || 0).toLocaleString();
                }

                // 获取借阅总数 - 通过查询接口获取总数
                const borrowQueryDto = { current: 1, size: 1 };
                const borrowResponse = await this.$axios.post('/bookOrderHistory/query', borrowQueryDto);
                if (borrowResponse.data.code === 200) {
                    this.statsData[2].value = (borrowResponse.data.total || 0).toLocaleString();
                }

                // 获取活跃用户数 - 暂时使用用户总数（可以后续添加活跃用户判断逻辑）
                this.statsData[3].value = this.statsData[0].value;
            } catch (error) {
                console.error('加载统计数据失败:', error);
                // 如果API不存在，使用默认值
                this.statsData[0].value = '0';
                this.statsData[1].value = '0';
                this.statsData[2].value = '0';
                this.statsData[3].value = '0';
            }
        },

        // 加载消息数据
        loadMessage() {
            const messageQueryDto = {
                current: 1,
                size: 5
            }
            this.$axios.post(`/notice/query`, messageQueryDto).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.noticeList = data.data;
                }
            })
        },

        loadPieCharts() {
            this.$axios.get(`/views/staticControls`).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.pieValues = data.data.map(entity => entity.count);
                    this.pieTypes = data.data.map(entity => entity.name);
                }
            })
        },

        userDatesSelected(time) {
            this.timeRange = time.toString();
            this.$axios.get(`/user/daysQuery/${time}`).then(response => {
                const { data } = response;
                if (data.code === 200) {
                    this.userValues = data.data.map(entity => entity.count);
                    this.userDates = data.data.map(entity => entity.name);
                }
            })
        },

        // 获取公告颜色
        getNoticeColor(index) {
            const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe'];
            return colors[index % colors.length];
        },

        // 格式化时间
        formatTime(timeStr) {
            if (!timeStr) return '';
            const date = new Date(timeStr);
            const now = new Date();
            const diff = now - date;
            const days = Math.floor(diff / (1000 * 60 * 60 * 24));

            if (days === 0) return '今天';
            if (days === 1) return '昨天';
            if (days < 7) return `${days}天前`;
            return date.toLocaleDateString();
        },

        // 查看公告详情
        viewNotice(notice) {
            // 这里可以跳转到公告详情或弹窗显示
            this.$message.info(`查看公告：${notice.name}`);
        }
    },
};
</script>
<style scoped lang="scss">
/* 深色主题设计系统 */
$primary-bg: #0f0f23;
$secondary-bg: #1a1a2e;
$card-bg: #16213e;
$text-primary: #ffffff;
$text-secondary: #a0aec0;
$text-muted: #718096;
$border-color: #2d3748;
$success-color: #48bb78;
$warning-color: #ed8936;
$error-color: #f56565;
$info-color: #4299e1;

/* 动画变量 */
$transition-fast: 0.2s ease;
$transition-normal: 0.3s ease;
$transition-slow: 0.5s ease;

/* 主容器 */
.dashboard-container {
    min-height: 100vh;
    background: linear-gradient(135deg, $primary-bg 0%, $secondary-bg 100%);
    padding: 20px;
    position: relative;

    &::before {
        content: '';
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background:
            radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.1) 0%, transparent 50%),
            radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.1) 0%, transparent 50%),
            radial-gradient(circle at 40% 40%, rgba(120, 219, 226, 0.1) 0%, transparent 50%);
        pointer-events: none;
        z-index: -1;
    }
}

/* 统计卡片区域 */
.stats-section {
    margin-bottom: 30px;

    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
        gap: 20px;

        .stat-card {
            background: linear-gradient(135deg, $card-bg 0%, rgba(22, 33, 62, 0.8) 100%);
            border-radius: 16px;
            padding: 24px;
            display: flex;
            align-items: center;
            gap: 20px;
            border: 1px solid rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            transition: all $transition-normal;
            box-shadow:
                0 8px 32px rgba(0, 0, 0, 0.3),
                inset 0 1px 0 rgba(255, 255, 255, 0.1);

            &:hover {
                transform: translateY(-4px);
                background: linear-gradient(135deg, rgba(22, 33, 62, 0.95) 0%, rgba(22, 33, 62, 0.9) 100%);
                border-color: rgba(96, 165, 250, 0.3);
                box-shadow:
                    0 12px 40px rgba(0, 0, 0, 0.4),
                    inset 0 1px 0 rgba(255, 255, 255, 0.2);
            }

            .stat-icon {
                width: 60px;
                height: 60px;
                border-radius: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
                position: relative;

                .stat-icon-inner {
                    font-size: 24px;
                    color: white;
                    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
                }

                &::before {
                    content: '';
                    position: absolute;
                    inset: -2px;
                    border-radius: 14px;
                    background: inherit;
                    opacity: 0.3;
                    filter: blur(8px);
                }
            }

            .stat-content {
                flex: 1;

                .stat-value {
                    font-size: 28px;
                    font-weight: 700;
                    color: $text-primary;
                    margin-bottom: 4px;
                    background: linear-gradient(135deg, #ffffff 0%, #e2e8f0 100%);
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                    background-clip: text;
                }

                .stat-label {
                    font-size: 14px;
                    color: $text-secondary;
                    margin-bottom: 8px;
                    font-weight: 500;
                }

                .stat-trend {
                    display: flex;
                    align-items: center;
                    gap: 4px;
                    font-size: 12px;
                    font-weight: 600;

                    &.trend-up {
                        color: $success-color;
                    }

                    &.trend-down {
                        color: $error-color;
                    }

                    i {
                        font-size: 14px;
                    }
                }
            }
        }
    }
}

/* 图表区域 */
.charts-section {
    margin-bottom: 30px;

    .charts-grid {
        display: grid;
        grid-template-columns: 1.2fr 0.8fr;
        grid-auto-rows: 520px;
        gap: 24px;

        .chart-card {
            height: 100%;
            background: linear-gradient(135deg, $card-bg 0%, rgba(22, 33, 62, 0.8) 100%);
            border-radius: 16px;
            border: 1px solid rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            overflow: hidden;
            transition: all $transition-normal;
            box-shadow:
                0 8px 32px rgba(0, 0, 0, 0.3),
                inset 0 1px 0 rgba(255, 255, 255, 0.1);
            display: flex;
            flex-direction: column;

            &.chart-card-wide {
                grid-column: span 1;
                min-height: 520px;
            }

            &:hover {
                transform: translateY(-2px);
                background: linear-gradient(135deg, rgba(22, 33, 62, 0.95) 0%, rgba(22, 33, 62, 0.9) 100%);
                border-color: rgba(96, 165, 250, 0.3);
                box-shadow:
                    0 12px 40px rgba(0, 0, 0, 0.4),
                    inset 0 1px 0 rgba(255, 255, 255, 0.2);
            }

            .chart-header {
                padding: 20px 24px 10px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-shrink: 0;

                h3 {
                    margin: 0;
                    color: #f0f4f8;
                    font-size: 18px;
                    font-weight: 600;
                    display: flex;
                    align-items: center;
                    gap: 8px;

                    i {
                        color: #4299e1;
                    }
                }

                .chart-controls {
                    display: flex;
                    gap: 8px;
                }
            }

            .chart-body {
                padding: 14px 18px 18px;
                flex: 1;
                height: 100%; /* 明确高度 */
                min-height: 320px;
                width: 100%;
                position: relative;
                display: flex;
                flex-direction: column;
                gap: 6px;
                justify-content: flex-start;
                background: radial-gradient(ellipse at top left, rgba(96, 165, 250, 0.08), transparent 40%),
                    radial-gradient(ellipse at bottom right, rgba(67, 233, 123, 0.08), transparent 45%);
                border-radius: 12px;
                border: 1px solid rgba(255, 255, 255, 0.06);
            }

            &.chart-card-spotlight {
                .chart-body {
                    padding: 16px 24px 24px;
                }
            }
        }
    }
}

/* 公告区域 */
.notices-section {
    .notices-card {
        background: linear-gradient(135deg, $card-bg 0%, rgba(22, 33, 62, 0.8) 100%);
        border-radius: 16px;
        border: 1px solid rgba(255, 255, 255, 0.1);
        backdrop-filter: blur(10px);
        overflow: hidden;
        box-shadow:
            0 8px 32px rgba(0, 0, 0, 0.3),
            inset 0 1px 0 rgba(255, 255, 255, 0.1);

        .notices-header {
            padding: 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid $border-color;

            h3 {
                margin: 0;
                color: #f0f4f8;
                font-size: 18px;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 8px;

                i {
                    color: #ed8936;
                }
            }

            .el-button {
                color: $text-secondary;

                &:hover {
                    color: #4299e1;
                }
            }
        }

        .notices-list {
            padding: 0;

            .notice-item {
                display: flex;
                align-items: center;
                padding: 16px 24px;
                border-bottom: 1px solid rgba(255, 255, 255, 0.05);
                transition: all $transition-fast;

                &:hover {
                    background: rgba(96, 165, 250, 0.12);
                    transform: translateX(4px);
                    border-left: 3px solid rgba(96, 165, 250, 0.5);
                }

                &:last-child {
                    border-bottom: none;
                }

                .notice-dot {
                    width: 8px;
                    height: 8px;
                    border-radius: 50%;
                    margin-right: 16px;
                    flex-shrink: 0;
                }

                .notice-content {
                    flex: 1;

                    .notice-title {
                        color: $text-primary;
                        font-size: 14px;
                        font-weight: 500;
                        margin-bottom: 4px;
                        cursor: pointer;
                        transition: color $transition-fast;

                        &:hover {
                            color: #4299e1;
                        }
                    }

                    .notice-time {
                        color: $text-muted;
                        font-size: 12px;
                        display: flex;
                        align-items: center;
                        gap: 4px;

                        i {
                            font-size: 10px;
                        }
                    }
                }

                .notice-action {
                    opacity: 0;
                    transition: opacity $transition-fast;

                    .el-button {
                        color: $text-secondary;

                        &:hover {
                            color: #4299e1;
                        }
                    }
                }

                &:hover .notice-action {
                    opacity: 1;
                }
            }
        }
    }
}

/* 响应式设计 */
@media (max-width: 1200px) {
    .charts-grid {
        grid-template-columns: 1fr; /* 小屏幕下改为单列 */
        grid-template-rows: auto;
        gap: 20px;
    }

    .chart-card {
        .chart-body {
            min-height: 360px;
        }
    }
}

@media (max-width: 768px) {
    .dashboard-container {
        padding: 15px;
    }

    .stats-grid {
        grid-template-columns: 1fr !important;
    }

    .stat-card {
        padding: 20px !important;
        flex-direction: column;
        text-align: center;
        gap: 16px !important;
    }

    .charts-grid {
        grid-template-columns: 1fr !important;
        gap: 15px !important;
    }

    .chart-card {
        .chart-header {
            flex-direction: column;
            align-items: flex-start !important;
            gap: 12px;

            .chart-controls {
                align-self: flex-end;
            }
        }
    }
}

/* 动画增强 */
@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.stat-card,
.chart-card,
.notices-card {
    animation: fadeInUp 0.6s ease-out;
    animation-fill-mode: both;
}

.stat-card:nth-child(1) {
    animation-delay: 0.1s;
}

.stat-card:nth-child(2) {
    animation-delay: 0.2s;
}

.stat-card:nth-child(3) {
    animation-delay: 0.3s;
}

.stat-card:nth-child(4) {
    animation-delay: 0.4s;
}

.chart-card:nth-child(1) {
    animation-delay: 0.2s;
}

.chart-card:nth-child(2) {
    animation-delay: 0.3s;
}
</style>