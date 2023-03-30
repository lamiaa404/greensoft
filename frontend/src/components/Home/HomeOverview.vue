<template>
  <n-card title="Übersicht">
    <n-row>
      <n-col :span="30">
        <n-statistic label="Derzeitige Online Geräte" :value="devicesOnline">
          <template #prefix>
            <n-icon style="margin-right:8px">
              <DesktopOutline/>
            </n-icon>
          </template>
          <template #suffix>
            / {{maxDevices}}
          </template>
        </n-statistic>
      </n-col>
      <n-col :span="50" style="margin-left:70px">
        <n-statistic label="Gesamtverbrauch">
          {{ maxPower }} Watt
        </n-statistic>
      </n-col>
    </n-row>
  </n-card>
</template>

<script>
import {DesktopOutline} from "@vicons/ionicons5";
import {onMounted, ref} from "vue";
import {createDiscreteApi} from "naive-ui";

const {message} = createDiscreteApi(["message"]);

export default {
  components: {
    DesktopOutline
  },
  name: "HomeOverview",

  setup() {
    const devices = ref([])
    const maxPower = ref(0)
    const devicesOnline = ref(-1)
    const maxDevices = ref(-1)

    onMounted(async () =>{
      await fetchDevices()

      maxDevices.value = devices.value.length
      devicesOnline.value = 0

      // Calculates the average power output of all devices since the last 20 minutes
      // Returns array of objects in the form of { id: deviceID, power: averagePower }
      const devicesPower = await calculateCurrentConsumptionAllDevices(20)

      for (let i = 0; i < devicesPower.length; i++) {
        if(isDeviceOnline(devicesPower[i], 3)) {
          devicesOnline.value += 1
        }

        maxPower.value += devicesPower[i].power
      }

      maxPower.value = maxPower.value.toFixed(2)


    })

    async function fetchDevices() {
      try {

        const response = await fetch('http://goeppert013.informatik.intern.uni-leipzig.de/api/devices');

        if (!response.ok) {
          throw new Error('API unreachable.');
        }

        const data = await response.json();

        // Change names of attributes so that it fits the "correct" style and into the frontend
        devices.value = data.map(device => ({
          id: device.id,
          name: device.name || "edison",
          location: device.standort || "unknown",
          users: device.users || 0,
          tags: device.tags ? (device.tags).split(',') : []
        }))

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

    async function calculateCurrentConsumptionAllDevices(timeBuffer = 20){
      const devicesPower = []

      for (let i = 0; i < devices.value.length; i++) {
        const consumption = await fetchPowerDataForID(devices.value[i].id, timeBuffer)

        if(!consumption) { continue }

        const sum = consumption.reduce((acc, entry) => acc + entry.consumption, 0)
        const avg = sum / consumption.length


        devicesPower.push({
          id: devices.value[i].id,
          power: avg
        })
      }

      return devicesPower
    }


    function isDeviceOnline(devicePowerEntry, thresholdInWatt = 3) {
      return devicePowerEntry.power > thresholdInWatt;
    }




    return {
      maxDevices,
      devicesOnline,
      maxPower

    }
  }
}
</script>

<style scoped>

</style>