<template>
  <div>
    <div>

    </div>
    <n-space style="float: left">
      <n-config-provider :locale="deDE" :date-locale="dateDeDE">
        <n-date-picker
            format="dd.MM.yyyy"
            v-model:value="timestamp"
            type="date"
            @update-value="onConfirm"
            :is-date-disabled="disableDate"
            style="width: 300px; margin-left: 10px"/>
      </n-config-provider>

      <n-config-provider  :locale="deDE" :date-locale="dateDeDE">
        <n-input-number v-model:value="precision" :min="5" :max="3600" placeholder="Auflösung in Sekunden" style="width: 150px">
          <template #prefix>
            Sek.
          </template>
        </n-input-number>
      </n-config-provider>

      <n-button @click="refresh">
        <template #icon>
          <n-icon>
            <refresh-outline />
          </n-icon>
        </template>
      </n-button>
    </n-space>

    <n-button-group style="float: right; padding-right: 10px">
      <n-button @click="resetZoom">
        <n-icon>
          <scan-outline />
        </n-icon>
      </n-button>
      <n-button @click="zoomIn">
        <template #icon>
          <n-icon>
            <add-outline />
          </n-icon>
        </template>
      </n-button>

      <n-button @click="zoomOut">
        <template #icon>
          <n-icon>
            <remove-outline />
          </n-icon>
        </template>
      </n-button>

      <n-button @click="markSequence">
        <template #icon>
          <n-icon>
            <color-wand-outline />
          </n-icon>
        </template>
      </n-button>
    </n-button-group>


    <Line ref="chartRef"
          :chart-data="chartData"
          :chart-options="chartOptions"
          :chart-id="chartId"
          :dataset-id-key="datasetIdKey"
          :plugins="plugins"
          :css-classes="cssClasses"
          :styles="styles"
          :width="width"
          :height="height"
    />
  </div>
</template>

<script>
import {CategoryScale, Chart as ChartJS, Legend, LineElement, PointElement, TimeScale, Title, Tooltip} from 'chart.js'
import {Line} from 'vue-chartjs'
import 'chartjs-adapter-date-fns';
// eslint-disable-next-line no-unused-vars
import {de} from 'date-fns/locale';
import annotationPlugin from 'chartjs-plugin-annotation'
import {ref} from "vue";
import {createDiscreteApi, dateDeDE, deDE} from "naive-ui";
import zoomPlugin from 'chartjs-plugin-zoom'
import {RefreshOutline, ColorWandOutline, AddOutline, RemoveOutline, ScanOutline} from "@vicons/ionicons5";

ChartJS.register(
    CategoryScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
    annotationPlugin,
    zoomPlugin,
    TimeScale
)

const {message} = createDiscreteApi(["message"]);

export default {
  name: 'RawDataLineChart',
  components: {
    Line, RefreshOutline, ColorWandOutline, AddOutline, RemoveOutline, ScanOutline
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
      default: 500
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
    device: {
      type: Number
    }
  },
  computed: {
    chartData() {
      return {
        datasets: [
          {
            label: "ID: " + this.device,
            borderColor: 'darkgrey',
            backgroundColor: 'black',
            pointBorderColor: "rgb(91,130,109)",
            pointBackgroundColor: "rgb(166,240,200)",
            borderWidth: .5,
            pointRadius: 2,
            pointHoverRadius: 2,
            //pointHitRadius: 5,
            //pointBorderWidth: .2,
            data: this.deviceData
          }
        ],
      }
    },
  },

  methods: {
    resetZoom() {
      const chartInstance = this.$refs.chartRef.chart;

      chartInstance.resetZoom()
    },
    disableDate(date) {
      return date < 1675724400*1000 || date > Date.now()
    },
    onConfirm(value){
      if(!this.device) {
        message.error("Bitte wählen Sie ein Gerät aus.")
        return
      }

      // Disable all previous marks
      const chartInstance = this.$refs.chartRef.chart;

      chartInstance.options.plugins.annotation.annotations.box.display = false

      chartInstance.options.plugins.annotation.annotations.start.display = false
      chartInstance.options.plugins.annotation.annotations.start.label.display = false
      chartInstance.options.plugins.annotation.annotations.end.label.display = false
      chartInstance.options.plugins.annotation.annotations.end.display = false

      this.fetchDataForID(this.device, value,this.precision)
    },
    refresh(){
      //this.onConfirm(this.timestamp)

      this.fetchDataForID(this.device, this.timestamp, this.precision)

    },
    zoomIn(){
      this.$refs.chartRef.chart.zoom({x: 1.25});
    },
    zoomOut(){
      this.$refs.chartRef.chart.zoom({x: 0.75});
    },
    markSequence(){
      const chartInstance = this.$refs.chartRef.chart;

      if(this.device === null || !chartInstance.data.datasets[0].data.length) {
        message.error("Es muss zuerst ein Gerät ausgewählt werden, damit eine Sequenz markiert werden kann.")
        return
      }


      // I kinda hate ChartJS:
      // When adding or modifying annotations, the chart will get redrawn, as it should be and as is expected.
      // But when this happens, the "zoom" is reset as well, and the chart gets zoomed out again.
      // This is bad and undesirable.
      // To circumvent this, we will zoom in  again immediately after the chart gets redrawn.
      // But in order to let this look "okay" we will have to deactivate animations and activate them again after that.
      // The result is ugly as hell in my opinion, but hey it works kinda? Maybe just disable annotations if this is too bad...

      let min = this.$refs.chartRef.chart.scales.x.min
      let max = this.$refs.chartRef.chart.scales.x.max

      const zoomDuration = chartInstance.options.transitions.zoom.animation.duration
      const generalDuration = chartInstance.options.animation.duration

      chartInstance.options.transitions.zoom.animation.duration = 0
      chartInstance.options.animation.duration = 0


      setTimeout(function() {
        chartInstance.options.plugins.annotation.annotations.box.xMax = max
        chartInstance.options.plugins.annotation.annotations.box.xMin = min

        chartInstance.options.plugins.annotation.annotations.start.xMin = min
        chartInstance.options.plugins.annotation.annotations.start.xMax = min
        chartInstance.options.plugins.annotation.annotations.end.xMin = max
        chartInstance.options.plugins.annotation.annotations.end.xMax = max

        chartInstance.options.plugins.annotation.annotations.box.display = true

        chartInstance.options.plugins.annotation.annotations.start.display = true
        chartInstance.options.plugins.annotation.annotations.start.label.display = true
        chartInstance.options.plugins.annotation.annotations.end.label.display = true
        chartInstance.options.plugins.annotation.annotations.end.display = true

        setTimeout(function() {
          chartInstance.zoomScale('x',{min: min, max: max}, "active")
        }, 1)
      }, 1);


      setTimeout(function() {
        chartInstance.options.transitions.zoom.animation.duration = zoomDuration
        chartInstance.options.animation.duration = generalDuration
      }, 1)


      // Inform parent class about new sequence stuff

      const sequence = {
        start: min,
        end: max
      }

      this.$emit('sequenceUpdated', sequence);
    }


  },

  watch: {
    // eslint-disable-next-line no-unused-vars
    device: async function(newval, oldvalue) {
      // Disable all previous marks
      const chartInstance = this.$refs.chartRef.chart;

      chartInstance.options.plugins.annotation.annotations.box.display = false

      chartInstance.options.plugins.annotation.annotations.start.display = false
      chartInstance.options.plugins.annotation.annotations.start.label.display = false
      chartInstance.options.plugins.annotation.annotations.end.label.display = false
      chartInstance.options.plugins.annotation.annotations.end.display = false

      await this.fetchDataForID(this.device, this.timestamp, this.precision)
    }
  },

  data() {
    const deviceData = ref(null)
    const timestamp = ref(new Date(Date.now()).setHours(0,0,0,0))

    async function fetchDataForID(id, timestamp, precision) {
      let start = new Date(timestamp).setHours(0,0,0)
      let end = new Date(timestamp).setHours(23,59,59)

      start /= 1000;
      end /= 1000;

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
        message.success("Daten für Gerät mit ID " + id  + " wurden geladen.", {
          duration: 1000
        })


        const convertedData = data.map(entry => {
          return {
            x: Number(entry.timestamp * 1000),
            y: Number(entry.energy_value)
          }
        })

        deviceData.value = convertedData

        const chartInstance = this.$refs.chartRef.chart;
        chartInstance.options.plugins.zoom.limits.x.min = start * 1000
        chartInstance.options.plugins.zoom.limits.x.max = end * 1000



      } catch (error) {
        removeMessage()
        message.error("Daten konnten nicht geladen werden. Fehler: " + error.message, {
          closable: true,
          duration: 0
        })
        console.log(error);
      }
    }



    return {
      dateDeDE,
      deDE,
      precision: ref(30),
      deviceData,
      fetchDataForID,
      timestamp,
      chartOptions: {
        transitions: {
          zoom: {
            animation: {
              duration: 50
            }
          }
        },
        animation: {
          duration: 250,
        },
        // onClick: (evt) => {
        //   if(!markingModeActive.value) {
        //     return
        //   }
        //
        //   const chartInstance = this.$refs.chartRef.chart;
        //   //const points = chartInstance.getElementsAtEventForMode(evt, 'index', { intersect: false }, false);
        //   const points = chartInstance.getElementsAtEventForMode(evt, 'nearest', { intersect: true }, true);
        //
        //   //console.log(points)
        //
        //   if (points.length) {
        //     const firstPoint = points[0];
        //
        //
        //     const label = this.chartData.datasets[0].data[firstPoint.index].x;
        //
        //     if(flipFlop) {
        //       x1.value = label
        //       x2.value = label
        //     } else {
        //       x2.value = label
        //     }
        //
        //     if(x1.value > x2.value) {
        //       start.value = x2.value
        //       end.value = x1.value
        //     } else {
        //       start.value = x1.value
        //       end.value = x2.value
        //     }
        //
        //     flipFlop = !flipFlop
        //   }
        //
        //
        // },
        responsive: true,
        hover: {
          mode: "nearest",
          intersect: false
        },
        plugins: {
          annotation: {
            annotations: {
              box: {
                type: 'box',
                backgroundColor: 'rgba(211,255,201,0.5)',
                borderWidth: 0,
                display: false
              },
              start: {
                label: {
                  type: "label",
                  display: false,
                  content: "Start",
                },
                type: 'line',
                borderColor: 'rgba(91,130,109,0.84)',
                borderWidth: 2,
                display: false
              },
              end: {
                label: {
                  type: "label",
                  display: false,
                  content: "Ende",
                },
                type: 'line',
                borderColor: 'rgba(91,130,109,0.84)',
                borderWidth: 2,
                display: false
              },

            }
          },
          zoom: {
            limits: {
              x: {},
              y: {}
            },
            pan: {
              enabled: true,
              modifierKey: 'ctrl',
              mode: "x",
              // onPanComplete: () => {
              //   console.log(this.$refs.chartRef.chart.scales.x.min)
              // }
            },
            zoom: {
              wheel: {
                enabled: true,
                modifierKey: 'alt'
              },
              drag: {
                enabled: true
              },
              mode: 'x',
            },
          },
          tooltip: {
            mode: "index",
            intersect: false,
            callbacks: {
              label: function (context) {
                let label = context.dataset.label || '';

                if (label) {
                  label += ': ';
                }
                if (context.parsed.y !== null) {
                  label += parseFloat(context.parsed.y).toFixed(2) + " Watt";
                }
                return label;
              }
            }
          },
          legend: {
            position: "right",
            display: false,
            align: "end",
          }
        },
        barPercentage: .7,
        scales: {
          x: {
            position: 'bottom',
            type: 'time',
            ticks: {
              autoSkip: true,
              autoSkipPadding: 50,
              maxRotation: 0
            },
            time: {
              // unit: "day",
              // stepSize: 1,
              tooltipFormat: 'dd. MMM yyyy, HH:mm:ss',
              displayFormats: {
                hour: 'HH:mm',
                minute: 'HH:mm',
                second: 'HH:mm:ss'
              }
            },
            adapters: {
              date: {
                locale: de
              }
            }
          },
          y: {
            ticks: {
              callback: function (value) {
                return Math.round(value * 100) / 100 + " W";
              }
            }
          }

        },
      }
    }

  },

}
</script>
