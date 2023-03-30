<template>
  <n-card style="width: 550px; float: left; height: 280px">
    <div :id="this.gaugeId" class="vue-gauge-item"></div>
    <vue-gauge :refid="'type-unique-id'" :options="options"></vue-gauge>
  </n-card>
  <n-card style="width: 430px; height: 280px; margin-left: 550px; float: right; position: absolute">
    <n-button style="left: 300px; top: 53px; position: absolute" :bordered="false" :focusable="false" size="large" @click="settings">
      <template #icon>
        <CreateOutline/>
      </template>
    </n-button>
    <n-button style="left: 360px; top: 53px; position: absolute" :bordered="false" :focusable="false" size="large" @click="reset">
      <template #icon>
        <RefreshOutline/>
      </template>
    </n-button>
    <n-h1 id="condition" style="color:#8ffda2; font-weight: bold; margin-top: 30px;">
      Gut
    </n-h1>
    Der Gesamtzustand des Energieverbrauches aller Geräte befindet sich im <b id="condition_desc">grünen</b> Bereich.
    <br>
    <br>
    Der Energieeinfluss liegt bei <b id="percent">{{config.needleValue}}%</b> bei einer manuellen Schranke von <b id="watt">{{watts}}W</b>.
  </n-card>

  <n-modal v-model:show="showModal">
    <n-card
        v-on:keyup.enter="editGauge"
        style="width: 350px; left: 500px; bottom: 65px"
        title="Maximaler Gesamtverbrauch"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true">
      Verbrauch (Watt)
      <n-input
          v-model:value="consumption" :allow-input="onlyNumber" type="text" placeholder="">
      </n-input>
      <div style="margin-top: 15%">
        <n-button @click="closeEditWindow">Abbrechen</n-button>
        <n-button @click="editGauge" style="float: right" type="primary">Übernehmen</n-button>
      </div>
    </n-card>
  </n-modal>

</template>

<script>
import VueGauge from 'vue-gauge';
const devices = ref([]);


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




let watts = 0;

if(checkCookie("watts")) {
  watts = getCookie("watts");
}

//average consumption *HERE* from API
let avg = 0;


let GaugeChart = require('vue-gauge/assets/bundle.js');

import { CreateOutline, RefreshOutline } from "@vicons/ionicons5";
import {ref} from "vue";
import {createDiscreteApi} from "naive-ui";
const {message} = createDiscreteApi(["message"]);
import {setCookie, eraseCookie, getCookie, checkCookie} from "@/utils/Cookies";

if(!checkCookie("watts")) {
  message.info("Bitte für das volle Benutzererlebnis mithilfe vom Bearbeiten-Button eine manuelle Schranke für den Stromverbrauch setzen.",
      {duration: 10000, closable: true});
}

const showModalRef = ref(false);

const consumptionRef = ref(false);

let config = {
  hasNeedle: true,
  needleColor: '#bababb',
  needleValue: 0,
  arcColors: ['#8ffda2', '#fdf772', '#fd9798'],
  arcDelimiters: [60, 90],
  arcPadding: 2,
  arcPaddingColor: '#ffffff',
  rangeLabel: [],
  chartWidth: 500,
};

let gauge = 0;

export default {
  name: "HomeGauge",
  props:['refid','options'],
  mounted() {
    this.initPlugin(this.options);
  },
  components: { VueGauge, CreateOutline, RefreshOutline },
  methods: {
    async initPlugin(options = {}){
      gauge = GaugeChart.gaugeChart(document.querySelector("#"+this.gaugeId), config.chartWidth, config);
      if(this.gaugeId){
        config = { ...config, ...options };

        await fetchDevices()
        avg = devices.value.length

        // Calculates the average power output of all devices since the last 20 minutes
        // Returns array of objects in the form of { id: deviceID, power: averagePower }
        const devicesPower = await calculateCurrentConsumptionAllDevices(20)

        for (let i = 0; i < devicesPower.length; i++) {
          avg += devicesPower[i].power
        }

        avg = avg.toFixed(2)
        // Element inside which you want to see the chart
        // Drawing and updating the chart
        if(checkCookie("watts")) {
          const w = getCookie("watts");
          const needleValue = (avg/w*100).toFixed(2);
          gauge.updateNeedle(needleValue);
          this.textUpdater(needleValue);
        } else {
          gauge.updateNeedle(0);
        }
      }
    },
    reset() {
      if(checkCookie("watts")) {
        eraseCookie("watts");
        watts = 0;
        gauge.updateNeedle(0);
        this.textUpdater(0);
        message.success("Cookies wurden erfolgreich zurückgesetzt.");
      } else {
        message.error("Keine Cookies vorhanden");
      }
    },
    textUpdater(needleValue) {
      document.getElementById("watt").innerHTML = watts + "W";
      if(needleValue <= 60) {
        document.getElementById("condition").innerHTML = "Gut"
        document.getElementById("condition").style.color = "#a6f6aa";
        document.getElementById("condition_desc").innerHTML = "grünen"
      } else if(needleValue > 60 && needleValue <= 90) {
        document.getElementById("condition").innerHTML = "Mittelmäßig"
        document.getElementById("condition").style.color = "#fcf182";
        document.getElementById("condition_desc").innerHTML = "gelben"
      } else {
        document.getElementById("condition").innerHTML = "Suboptimal"
        document.getElementById("condition").style.color = "#f09a99";
        document.getElementById("condition_desc").innerHTML = "roten"
      }
      if(needleValue <= 100) {
        document.getElementById("percent").innerHTML = needleValue + "%";
      } else {
        document.getElementById("percent").innerHTML = "100%";
      }
    },
    editGauge() {
      if(watts === consumptionRef.value) {
        showModalRef.value = true;
        message.error("Diese manuelle Schranke ist bereits eingestellt.");
      } else {
        if(consumptionRef.value.toString() === "0") {
          showModalRef.value = true;
          message.error("Schranke muss größer als 0W sein.");
          return;
        }
        watts = consumptionRef.value;
        const needleValue = (avg/watts*100).toFixed(2);
        gauge.updateNeedle(needleValue);
        this.textUpdater(needleValue);
        setCookie("watts", watts, 100);
        console.log(getCookie("watts"));
        console.log(checkCookie("watts"))
        showModalRef.value = false;
        message.success("Manuelle Schranke wurde angepasst.");
      }
    }
  },
  computed: {
    gaugeId(){
      if(typeof this.refid != 'undefined'){
        return this.refid;
      }
      return "vue-gauge";
    }
  },

  data() {
    let w = getCookie("watts");
    return {
      showModal: showModalRef,
      consumption: consumptionRef,
      watts,
      w,
      config,
      onlyNumber: (value) => !value || /^\d+$/.test(value),
      settings() {
        consumptionRef.value = watts;
        showModalRef.value = true;
      },
      closeEditWindow() {
        showModalRef.value = false;
        message.info("Keine Änderungen");
      }
    }
  }
}
</script>

<style scoped>

</style>