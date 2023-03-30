<template>
  <title>Analysieren</title>
  <n-h1>Analysieren</n-h1>

  <div class="container">
    <div class="Table">
      <DeviceSelectionTable :multiple-allowed="true" table-height="737px" @selectionChanged="OnUpdatedSelection"></DeviceSelectionTable>
    </div>
    <div class="Diagram-1">

      <div style="min-width: 500px; float: left; margin-bottom: 30px">
        <n-config-provider :locale="deDE" :date-locale="dateDeDE">
          <n-space justify="space-around">
            <n-date-picker :is-date-disabled="disableDate" format="dd.MM.yyyy" v-model:value="dateRange" type="daterange" @confirm="onConfirm" clearable style="width: 300px"/>
            <n-select :render-option="renderTypeOption" @update-value="typeChange" v-model:value="typeSelection" :options="options" style="width: 200px"/>
          </n-space>
        </n-config-provider>

      </div>

      <BarChart style="width: 1100px" :type="typeSelection" :device-data="deviceData" :selected-devices="selectedDevices"/>
    </div>
    <div class="Diagram-2" style="margin-top: 20px">
<!--      TODO fix stupid bug/warning/error in the console: Caused by having barchart and linechart at the same time.
               When having one or the other it is somehow fine again? -->

<!--      <div style="min-width: 250px; float: left; margin: 10px 10px 10px 65px;"></div>-->

      <LineChart style="width: 1100px" :device-data="deviceData" :selected-devices="selectedDevices"></LineChart>
    </div>
  </div>

</template>

<script>
import BarChart from "@/components/Analyze/BarChart";
import LineChart from "@/components/Analyze/LineChart"
import {createDiscreteApi, dateDeDE, deDE} from 'naive-ui';
import {defineComponent, ref, h, onMounted} from "vue";
import DeviceSelectionTable from "@/components/DeviceSelectionTable.vue";
import { NTooltip } from "naive-ui";

//generateDummyBarChartStatistics(1669335354, 1670335354, 2);

const {message} = createDiscreteApi(["message"]);

export default defineComponent({
  // eslint-disable-next-line vue/multi-word-component-names
  name: "Analyze",
  // eslint-disable-next-line vue/no-unused-components
  components: {DeviceSelectionTable, BarChart, LineChart},
  setup() {
    const dateRange = ref([
      new Date((Date.now() - 7*86400000)).setHours(23,0,0,0),
      new Date((Date.now() - 86400*1000)).setHours(23,0,0,0)
    ]);
    const deviceData = ref([])//ref(generateDummyBarChartStatistics(range.value[0] / 1000, range.value[1] / 1000, 20));
    const selectedDevices = ref([]);
    const typeSelection = ref("mean");

    onMounted(()=>{
      fetchChartData(getCorrectDate(dateRange.value)[0], getCorrectDate(dateRange.value)[1], "mean")
    })

    // eslint-disable-next-line no-unused-vars
    async function fetchChartData(start, end, dataType) {
      const url = `http://goeppert013.informatik.intern.uni-leipzig.de/api/computers/${dataType}?start=${start}&end=${end}`;
      try {
        const response = await fetch(url);
        if (!response.ok) {
          throw new Error("Request failed.");
        }
        const data = await response.json();

        const convertedData = data.map(item => {
          return {
            id: parseInt(item.id),
            name: "edison", //TODO change this to the right value
            consumption: parseFloat(item.meanValue) || parseFloat(item.maxValue) || parseFloat(item.idleValue),
            date: Number(item.timestamp) * 1000
          }
        })

        deviceData.value = convertedData

        return data;
      } catch (error) {
        message.error("Fehler beim Laden der Daten: " + error.message, {
          duration: 10000,
          closable: true
        });
        console.error(error);
      }
    }

    function disableDate(date) {
      return date < 1677538800*1000 || date > Date.now()
    }

    // eslint-disable-next-line no-unused-vars
    function typeChange(v) {
      fetchChartData(getCorrectDate(dateRange.value)[0], getCorrectDate(dateRange.value)[1], v)
    }

    function getCorrectDate(range){
      let start = 0
      let end = 0

      start = new Date(range[0]).setHours(0,0,0,0)
      end = new Date(range[1] + 86400*1000).setHours(0,0,0,0)

      return [start / 1000, end / 1000]
    }

    return {
      dateDeDE,
      options: [
        {
          label: "Durchschnitt",
          value: "mean",
        },
        {
          label: "Maximum",
          value: "max",
        },
        {
          label: "Idle",
          value: "idle",
        },
      ],
      deDE,
      selectedDevices,
      getCorrectDate,
      dateRange,
      deviceData,
      typeSelection,
      typeChange,
      disableDate,
      onConfirm(value) {

        if(typeSelection.value != null) {
          console.log(getCorrectDate(value))
          fetchChartData(getCorrectDate(value)[0], getCorrectDate(value)[1], typeSelection.value)
        }
      },
      OnUpdatedSelection(v) {
        selectedDevices.value = v;
      },
      renderTypeOption: ({node, option}) => h(NTooltip, {placement: "right-start"}, {
        trigger: () => node,
        default: () => {
          switch (option.value) {
            case "max":
              return "Ermittelt den maximalen Verbrauch der selektierten und nicht-selektierten Geräte pro Tag."
            case "mean":
              return "Ermittelt den durchschnittlichen Verbrauch der selektierten und nicht-selektierten Geräte pro Tag."
            case "idle":
              return "Ermittelt den durchschnittlichen Idle Verbrauch der selektieren und nicht-selektierten Geräte pro Tag."
            default:
              return "Bitte wählen Sie eine Option aus."
          }
        }
      })
    }
  }

})
</script>

<style scoped>

.container {  display: grid;
  grid-template-columns: 0.8fr 1.8fr;
  grid-template-rows: 1.4fr 1.4fr;
  gap: 0 0;
  grid-auto-flow: row;
  grid-template-areas:
    "Table Diagram-1"
    "Table Diagram-2";
}

.Table { grid-area: Table; }

.Diagram-2 { grid-area: Diagram-2; }

.Diagram-1 { grid-area: Diagram-1; }



</style>