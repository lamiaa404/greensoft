<template>
  <Line :chart-data="chartData"
        :chart-options="chartOptions"
        :chart-id="chartId"
        :dataset-id-key="datasetIdKey"
        :plugins="plugins"
        :css-classes="cssClasses"
        :styles="styles"
        :width="width"
        :height="height"/>
</template>

<script>
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import {Line} from 'vue-chartjs'
import {getLabels, getLineChartDatasetsForSelectedIDs} from "@/utils/AnalyzeChartHelper";

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
)

export default {
  name: 'LineChart',
  components: {
    Line
  },
  props: {
    chartId: {
      type: String,
      default: 'line-chart'
    },
    datasetIdKey: {
      type: String,
      default: 'label'
    },
    width: {
      type: Number,
      default: 1200
    },
    height: {
      type: Number,
      default: 350
    },
    cssClasses: {
      default: '',
      type: String
    },
    styles: {
      type: Object,
      default: () => {
      }
    },
    plugins: {
      type: Object,
      default: () => {
      }
    },
    deviceData: Array,
    selectedDevices: {
      type: Array,
      default: () => {
        return [0,1]
      }
    }
  },
  computed: {
    chartData() {
      return {
        labels: getLabels(this.deviceData),
        datasets: getLineChartDatasetsForSelectedIDs(this.deviceData, this.selectedDevices)
      }
    },
  },
  data() {
    return {
      chartOptions: {
        responsive: true,
        hover: {
          mode: "nearest",
          intersect: false
        },
        plugins: {
          tooltip: {
            callbacks: {
              label: function (context) {
                let label = context.dataset.label || '';

                if (label) {
                  label += ': ';
                }
                if (context.parsed.y !== null) {
                  label += Math.round(context.parsed.y * 100) / 100 + " Watt";
                }
                return label;
              }
            }
          },
          legend: {
            labels: {
              usePointStyle: true,
              boxWidth: 8,
              boxHeight: 8,
              padding: 13,
            },
            position: "right",
            display: true,
            align: "end",
          }
        },
        barPercentage: .7,
        scales: {
          y: {
            ticks: {
              callback: function (value) {
                return Math.round(value * 100) / 100 + " W";
              }
            }
          }
        }
      }
    }

  }
}
</script>
