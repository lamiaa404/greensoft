<template>
  <n-card>
    <n-space vertical>
      Derzeitiger Verbrauch nach Standort
      <Pie :chart-data="chartData" :chart-options="chartOptions" style="margin-top: -35px;"/>
    </n-space>
  </n-card>
</template>

<script>

import {Pie} from 'vue-chartjs'
import {ArcElement, Chart as ChartJS, Legend, Tooltip} from 'chart.js'
import {createDiscreteApi} from "naive-ui";
import {ref} from "vue";

ChartJS.register(ArcElement, Tooltip, Legend)

// const data = {
//   labels: ['Uni Leipzig'],
//   datasets: [
//     {
//       backgroundColor: ['#fdf3c2'],
//       data: [50]
//     }
//   ]
// }

const {message} = createDiscreteApi(["message"]);

export default {
  name: 'PieChartLocation',
  components: {Pie},

  async mounted() {
    const powerByLocations = await this.calculatePowerForAllLocations()

    let labels = []
    let data = []

    powerByLocations.forEach(obj => {
      labels.push(obj.location);
      data.push(obj.power);
    });

    this.labels.value = labels
    this.data.value = data

    console.log(this.labels.value)
    console.log(this.data.value)
  },

  computed: {
    chartData() {
      return {
        labels: this.labels.value,
        datasets: [{
          backgroundColor: [
            'rgb(174,250,186)',
            'rgb(91,130,109)',
            'rgb(156,157,255)',
            'rgb(253,235,190)',
            'rgb(187,255,86)',
            'rgb(8,48,199)',
            'rgb(255,216,215)',
            'rgb(236,168,229)',
            'rgb(96,65,166)',
            'rgb(5,255,26)'
          ],
          data: this.data.value
        }]
      }
    },

  },

  methods: {


  },

  data() {
    const data = ref([50])
    const labels = ref(["Lädt..."])

    async function fetchDevices() {
      try {

        const response = await fetch('http://goeppert013.informatik.intern.uni-leipzig.de/api/devices');

        if (!response.ok) {
          throw new Error('API unreachable.');
        }

        const data = await response.json();

        // Change names of attributes so that it fits the "correct" style and into the frontend
        const devices = data.map(device => ({
          id: device.id,
          name: device.name || "edison",
          location: device.standort || "unknown",
          users: device.users || 0,
          tags: device.tags ? (device.tags).split(',') : []
        }))

        return devices

      } catch (error) {
        message.error("Fehler: " + error.message + " Es können keine Gerätedaten angezeigt werden." +
            " Bitte probieren Sie es später noch einmal.", {
          closable: true,
          duration: 10000
        })
      }
    }

    async function fetchPowerDataForID(id, timeBuffer) {
      const offsetInSeconds = timeBuffer * 60

      let start = new Date(Date.now() - offsetInSeconds * 1000).setMilliseconds(0)
      let end = new Date(Date.now()).setMilliseconds(0)


      start /= 1000;
      end /= 1000;

      const url = `http://goeppert013.informatik.intern.uni-leipzig.de/api/computers?id=${id}&start=${start}&end=${end}&precision=1`;

      try {
        const response = await fetch(url);
        const data = await response.json();


        return data.map(entry => {
          return {
            timestamp: Number(entry.timestamp * 1000),
            consumption: Number(entry.energy_value)
          }
        })

      } catch (error) {
        message.error("Daten konnten nicht geladen werden. Fehler: " + error.message, {
          closable: true,
          duration: 0
        })
        console.log(error);
      }
    }


    async function calculatePowerForLocation(devices, location, timeBuffer = 20){
      let devicesPower = 0

      for (let i = 0; i < devices.length; i++) {
        const consumption = await fetchPowerDataForID(devices[i].id, timeBuffer)

        if(!consumption || devices[i].location !== location) { continue }

        const sum = consumption.reduce((acc, entry) => acc + entry.consumption, 0)
        const avg = sum / consumption.length


        devicesPower += avg
      }

      return devicesPower
    }

     function uniqueLocationsWithKey(devices) {
      // Find all unique locations
      // Generate an array of objects with label and key attributes
      return [...new Set(devices.map(device => device.location))];
    }

    async function calculatePowerForAllLocations() {
      const devices = await fetchDevices()
      const locations = uniqueLocationsWithKey(devices)

      let powerByLocations = []

      for (let i = 0; i < locations.length; i++) {

        powerByLocations.push({
          location: locations[i],
          power: await calculatePowerForLocation(devices, locations[i])
        })
      }

      return powerByLocations

    }

    return {
      data,
      labels,
      uniqueLocationsWithKey,
      calculatePowerForLocation,
      calculatePowerForAllLocations,
      fetchPowerDataForID,
      fetchDevices,
      chartOptions: {
        responsive: true,
        plugins: {
          legend: {
            position: "right",
            display: true,
          },

          tooltip: {
            callbacks: {
              label: function(context) {
                const label = context.label + ": " + context.parsed.toFixed(2) + " Watt"

                return label;
              }
            }
          }
        },

      }
    }
  }
}
</script>

<style scoped>

</style>