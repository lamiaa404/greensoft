<template>
  <Bar
      :chart-options="chartOptions"
      :chart-data="chartData"
      :chart-id="chartId"
      :dataset-id-key="datasetIdKey"
      :plugins="plugins"
      :css-classes="cssClasses"
      :styles="styles"
      :width="width"
      :height="height"
  />
</template>

<script>
import {Bar} from 'vue-chartjs'
import {Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale} from 'chart.js'
import {getBarChartDatasets, getLabels} from "@/utils/AnalyzeChartHelper";

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default {
  name: 'BarChart',
  components: {Bar},
  props: {
    chartId: {
      type: String,
      default: 'bar-chart'
    },
    datasetIdKey: {
      type: String,
      default: 'label'
    },
    width: {
      type: Number,
      default: 700
    },
    height: {
      type: Number,
      default: 200
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
    selectedDevices: Array,
    type: {
      default: "mean",
      type: String
    }

  },
  computed: {
    chartData() { return {
      labels: getLabels(this.deviceData),
      datasets: getBarChartDatasets(this.deviceData, this.selectedDevices, this.type)//getDatasets(this.deviceData, this.selectedDevices)
    } },
  },
  data() {
    return {
      chartOptions: {
        responsive: true,
        plugins: {
          tooltip: {
            callbacks: {
              label: function(context) {
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
            position: "right",
            display: true,
            align: "end",
          }
        },
        barPercentage: .7,
        scales: {
          x: {
            stacked: true,
          },
          y: {
            stacked: true,
            ticks: {
              precision: 0,
              callback: function(value) {
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
