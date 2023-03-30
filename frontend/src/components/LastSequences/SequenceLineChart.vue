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
import {CategoryScale, Chart as ChartJS, Legend, LinearScale, LineElement, PointElement, Title, Tooltip} from 'chart.js'
import {Line} from 'vue-chartjs'
import {ref} from "vue";
import {createDiscreteApi} from "naive-ui";
import {getRandomColorSeeded} from "@/utils/AnalyzeChartHelper";
// eslint-disable-next-line no-unused-vars
import {de} from "date-fns/locale";

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
)

const {message} = createDiscreteApi(["message"]);

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
      default: 700
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
    selectedSequences: {
      type: Array,
      default: () => {
        return [0,1]
      }
    }
  },
  methods: {
    normalizeTimeRange(data) {
      // Step 1: Sort the array by x value in ascending order
      data.sort((a, b) => a.x - b.x);

      // Step 2: Find the minimum x value in the sorted array
      const minX = data[0].x;

      // Step 3: Subtract the minimum x value from each x value to shift the x axis
      const normalizedData = data.map(({x, y}) => ({
        x: (x - minX) / 1000,
        y
      }));

      return normalizedData;
    },


    async fetchDataForID(id, start, end) {
      const precision = 5

      const url = `http://goeppert013.informatik.intern.uni-leipzig.de/api/computers?id=${id}&start=${start}&end=${end}&precision=${precision}`;

      //console.log(url)

      const removeMessage = () => {
        if(messageReactive) {
          messageReactive.destroy();
          messageReactive = null;
        }
      };

      let messageReactive = message.loading("Lade Daten...", {duration: 0})


      try {
        const response = await fetch(url);
        const data = await response.json();

        removeMessage()
        message.success("Daten wurden geladen.", {
          duration: 100
        })


        return data.map(entry => {
          return {
            x: Number(entry.timestamp * 1000),
            y: Number(entry.energy_value)
          }
        })


      } catch (error) {
        removeMessage()
        message.error("Daten konnten nicht geladen werden. Fehler: " + error.message, {
          closable: true,
          duration: 0
        })
        console.log(error);
      }

    }

  },
  watch: {
    // eslint-disable-next-line no-unused-vars
    selectedSequences: async function (newVal, oldVal) {

      // eslint-disable-next-line no-unused-vars
      let newData = []

      for (let i = 0; i < newVal.length; i++) {
        // make request
        const start = newVal[i].start
        const end = newVal[i].end
        const label = newVal[i].name
        const id = newVal[i].id
        const deviceId = newVal[i].deviceId

        const data = this.normalizeTimeRange(await this.fetchDataForID(deviceId, start, end))

        const dataset = {
          label: id + ": " + label,
          backgroundColor: getRandomColorSeeded(i),
          borderColor: getRandomColorSeeded(i),
          tension: .1,
          pointHitRadius: 30,
          pointRadius: 2,
          hoverRadius: 10,
          pointHoverRadius: 10,
          data: data
        }

        newData.push(dataset)
      }

      this.datasets = newData

    }
  },
  computed: {
    chartData() {
      return {
        datasets: this.datasets
      }
    },
  },
  data() {
    const datasets = ref([])

    return {
      datasets,
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
            position: "bottom",
            display: true,
            align: "end",
          }
        },
        barPercentage: .7,
        scales: {
          x: {
            title: {
              display: true,
              text: 'Vergangene Zeit in Sekunden'
            },
            type: 'linear',
          },
          // x: {
          //   position: 'bottom',
          //   type: 'time',
          //   ticks: {
          //     autoSkip: true,
          //     autoSkipPadding: 50,
          //     maxRotation: 0
          //   },
          //   time: {
          //     unit: "second",
          //     // stepSize: 1,
          //     // tooltipFormat: 'HH:mm:ss',
          //     // displayFormats: {
          //     //   hour: 'HH:mm',
          //     //   minute: 'HH:mm',
          //     //   second: 'HH:mm:ss'
          //     // }
          //   },
          //   // adapters: {
          //   //   date: {
          //   //     locale: de
          //   //   }
          //   // }
          // },
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
