<template>
  <div class="pie-insight">
    <div class="tag-row">
      <span class="tag" :style="{ color: fontColor }">{{ tag }}</span>
      <span class="total" v-if="totalValue">总计 {{ totalValue.toLocaleString() }}</span>
    </div>
    <div class="content">
      <!-- 占比最高的卡片 -->
      <div class="focus-card" v-if="focusItem">
        <p>占比最高</p>
        <strong>{{ focusItem.name || '未分类' }}</strong>
        <span class="focus-value">{{ focusItem.value.toLocaleString() }}</span>
        <small>{{ focusShare }}%</small>
      </div>

      <!-- 图表主体 -->
      <div class="chart-container">
          <div class="chart-wrap" ref="chart" :style="{ background: bag }"></div>
          <div class="empty-state" v-if="!hasData">
            <span>暂无数据</span>
            <span class="hint">暂无统计数据或接口未返回内容</span>
          </div>
      </div>

      <!-- 图例列表 -->
      <ul class="legend" v-if="chartData.length">
        <li v-for="(item, index) in chartData" :key="item.name + index">
          <span class="dot" :style="{ background: colorList[index % colorList.length] }"></span>
          <div class="legend-meta">
            <p>{{ item.name || '未分类' }}</p>
            <small>{{ item.value.toLocaleString() }} · {{ item.percent }}%</small>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>
<script>
import * as echarts from 'echarts'
export default {
  name: 'PieChart',
  props: {
    types: {
      type: Array,
      default: () => []
    },
    values: {
      type: Array,
      default: () => []
    },
    width: {
      type: String,
      default: '100%'
    },
    tag: {
      type: String,
      default: '数据分布'
    },
    height: {
      type: String,
      default: '100%'
    },
    bag: {
      type: String,
      default: 'transparent'
    },
    fontColor: {
      type: String,
      default: '#fff'
    }
  },
  data() {
    return {
      chart: null,
      colorList: ['#4f9dff', '#43e97b', '#fbd249', '#fb7185', '#a78bfa', '#67e8f9', '#ff9f7f']
    }
  },
  computed: {
    chartData() {
      const mapped = this.values.map((value, index) => ({
        name: this.types[index],
        value: typeof value === 'number' ? value : 0
      }))
      const total = mapped.reduce((sum, item) => sum + item.value, 0)
      return mapped.map(item => ({
        ...item,
        percent: total ? ((item.value / total) * 100).toFixed(1) : 0
      }))
    },
    totalValue() {
      return this.chartData.reduce((sum, item) => sum + item.value, 0)
    },
    hasData() {
      return this.totalValue > 0
    },
    focusItem() {
      if (!this.chartData.length || !this.hasData) return null
      return this.chartData.reduce((max, item) => (item.value > max.value ? item : max), this.chartData[0])
    },
    focusShare() {
      if (!this.focusItem || !this.totalValue) return 0
      return ((this.focusItem.value / this.totalValue) * 100).toFixed(1)
    },
  },
  watch: {
    chartData: {
      handler() {
        this.$nextTick(this.initChart)
      },
      deep: true
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
      window.addEventListener('resize', this.handleResize)
    })
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
    }
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    handleResize() {
      if (this.chart) {
        this.chart.resize()
      }
    },
    initChart() {
      if (!this.$refs.chart) return
      if (!this.chart) {
        this.chart = echarts.init(this.$refs.chart, null, {
          renderer: 'canvas',
          useDirtyRect: false
        })
      }
      const data = this.chartData.map((item, index) => ({
        ...item,
        value: item.value,
        name: item.name || '未分类',
        itemStyle: {
          color: this.colorList[index % this.colorList.length]
        }
      }))
      const hasRealData = data.some(item => item.value > 0)
      const seriesData = hasRealData
        ? data
        : [{
            value: 1,
            name: '暂无数据',
            itemStyle: {
              color: 'rgba(255, 255, 255, 0.08)'
            },
            label: {
              show: true,
              color: '#8fa0c2',
              formatter: '暂无数据',
              fontSize: 14,
              fontWeight: '600'
            }
          }]
      const option = {
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: 'rgba(96, 165, 250, 0.5)',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          },
          formatter: '{b}<br/>数量: {c}<br/>占比: {d}%'
        },
        series: [
          {
          type: 'pie',
          radius: ['55%', '85%'],
          center: ['50%', '50%'],
          avoidLabelOverlap: true,
          label: {
              show: false,
              position: 'center'
          },
          labelLine: {
            show: false
          },
          data: seriesData,
          emphasis: {
            scale: true,
            scaleSize: 10,
            label: {
              show: hasRealData,
              formatter: '{b}\n{d}%',
              color: '#fff',
              fontSize: 16,
              fontWeight: '600'
            }
          }
          }
        ]
      }
      this.chart.setOption(option, true)
      this.$nextTick(() => {
        if (this.chart) {
          this.chart.resize()
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.pie-insight {
  padding: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;

  .tag-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 4px;

    .tag {
      font-size: 16px;
      font-weight: 600;
      letter-spacing: 0.04em;
    }

    .total {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.7);
    }
  }

  .content {
    /* 关键修复：移除 align-items: center，使用默认的 stretch */
    /* display: flex; align-items: center; justify-content: space-between; */
    display: flex;
    justify-content: space-between;
    gap: 20px;
    flex: 1;
    width: 100%;
    min-height: 0; 
    
    @media (max-width: 960px) {
      flex-direction: column;
      gap: 16px;
      align-items: center; /* 手机端可以居中 */
    }
  }

  .focus-card {
    background: rgba(255, 255, 255, 0.03);
    border-radius: 16px;
    padding: 20px;
    border: 1px solid rgba(255, 255, 255, 0.08);
    flex-shrink: 0;
    width: 140px; 
    align-self: center; /* 自身居中 */

    p {
      margin: 0;
      color: rgba(255, 255, 255, 0.6);
      font-size: 12px;
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }

    strong {
      display: block;
      margin: 12px 0 6px;
      font-size: 18px;
      color: #fff;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .focus-value {
      font-size: 22px;
      font-weight: 600;
      color: #fff;
    }

    small {
      display: block;
      margin-top: 6px;
      color: rgba(255, 255, 255, 0.6);
    }
  }

  .chart-container {
      flex: 1;
      height: 100%;
      min-height: 0;
      /* 确保内部 canvas 能撑开 */
      position: relative;
      display: flex;
      justify-content: center;
      align-items: center;
  }

  .chart-wrap {
    width: 100%;
    height: 100%;
    position: relative;
    overflow: hidden;
  }

  .empty-state {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    color: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(1px);
    text-align: center;

    .hint {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.5);
    }
  }

  .legend {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
    flex-shrink: 0;
    width: 140px; 
    align-self: center; /* 自身居中 */

    li {
      display: flex;
      align-items: flex-start;
      gap: 10px;
      color: rgba(255, 255, 255, 0.75);

      .dot {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        flex-shrink: 0;
        margin-top: 5px;
      }

      .legend-meta {
        flex: 1;
        min-width: 0; 
        
        p {
          margin: 0 0 2px;
          font-weight: 600;
          font-size: 13px;
          line-height: 1.4;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        small {
          color: rgba(255, 255, 255, 0.55);
          font-size: 12px;
          line-height: 1.4;
        }
      }
    }
  }
}
</style>