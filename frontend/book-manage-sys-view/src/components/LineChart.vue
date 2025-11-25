<template>
  <div class="line-main" :style="{ height }">
    <div ref="chart" class="chart-container"></div>
    <!-- 仅在没有数据时显示占位符 -->
    <div v-if="!hasData" class="empty-chart">
      <i class="el-icon-data-line"></i>
      <p>暂无数据</p>
      <small>数据加载中或暂无相关记录</small>
    </div>
  </div>
</template>
<script>
// 折线图组件
import * as echarts from 'echarts';
export default {
  name: 'DialogLine',
  props: {
    tag: {
      type: String,
      default: '折线图'
    },
    values: {
      type: Array,
      required: true
    },
    date: {
      type: Array,
      required: true
    },
    height: {
      type: String,
      default: '100%' 
    },
  },
  computed: {
    hasData() {
      return this.values && this.values.length > 0 && this.values.some(v => v !== null && v !== undefined);
    }
  },
  watch: {
    values(v1, v2) {
      this.$nextTick(() => {
        this.initChart();
      });
    },
    date() {
      this.$nextTick(() => {
        this.initChart();
      });
    }
  },
  data() {
    return {
      chart: null,
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart();
      window.addEventListener('resize', this.handleResize);
    });
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose();
    }
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    handleResize() {
      if (this.chart) {
        this.chart.resize();
      }
    },
    // 图表初始化
    initChart() {
      if (!this.$refs.chart) return;
      
      if (!this.chart) {
        this.chart = echarts.init(this.$refs.chart);
      }

      // 即使没有数据，也可以初始化一个空网格，或者保持清空状态
      if (!this.hasData) {
        this.chart.clear();
        return;
      }

      const option = {
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '40px', 
          containLabel: true, 
          borderWidth: 0,
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: 'rgba(96, 165, 250, 0.5)',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          },
          formatter: (params) => {
            const param = params[0];
            return `${param.name}<br/>${this.tag}: ${param.value}`;
          }
        },
        xAxis: {
          type: 'category',
          data: this.date || [],
          boundaryGap: false, 
          axisLine: { 
            show: true,
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.1)'
            }
          },
          axisTick: { show: false },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.6)',
            fontSize: 12
          },
        },
        yAxis: {
          type: 'value',
          axisLine: { 
            show: false, 
          },
          axisTick: { show: false },
          splitLine: {
            show: true,
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.05)',
              type: 'dashed' 
            }
          },
          axisLabel: {
            color: 'rgba(255, 255, 255, 0.6)',
            fontSize: 12,
            formatter: '{value}'
          },
        },
        series: [{
          name: this.tag,
          type: 'line',
          smooth: true,
          data: this.values || [],
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(96, 165, 250, 0.3)' },
                { offset: 1, color: 'rgba(96, 165, 250, 0.05)' }
              ]
            }
          },
          lineStyle: {
            color: '#60a5fa',
            width: 3, 
            shadowColor: 'rgba(96, 165, 250, 0.3)',
            shadowBlur: 10
          },
          itemStyle: {
            color: '#60a5fa',
            borderColor: '#fff',
            borderWidth: 2
          },
          symbol: 'circle',
          symbolSize: 8,
          showSymbol: false, 
          label: {
            show: false
          },
        }]
      };
      this.chart.setOption(option, true);
    },
  },
};  
</script>
<style scoped lang="scss">
.line-main {
  width: 100%;
  height: 100%; 
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden; 

  .chart-container {
      width: 100%;
      height: 100%;
  }

  .empty-chart {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: rgba(255, 255, 255, 0.4);
    z-index: 1;
    pointer-events: none; 

    i {
      font-size: 48px;
      display: block;
      margin-bottom: 12px;
      opacity: 0.5;
    }

    p {
      margin: 0 0 6px;
      font-size: 16px;
      font-weight: 500;
    }

    small {
      font-size: 12px;
      opacity: 0.6;
    }
  }
}
</style>