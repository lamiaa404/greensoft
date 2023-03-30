<template>
  <!-- Empfehlenswert https://css-tricks.com/snippets/css/a-guide-to-flexbox/ -->
  <div class="container">
    <div class="Table">
      <DeviceSelectionTable @selectionChanged="OnUpdatedSelection" :multiple-allowed="false" table-height="664px"></DeviceSelectionTable>
    </div>
    <div class="Create">
      <n-space vertical>
        <n-button-group>
          <n-button @click="save">Erstellen</n-button>
        </n-button-group>
      </n-space>

    </div>
    <div class="Diagram-1">
      <RawDataLineChart @sequenceUpdated="onUpdatedSequence" :device="selectedDevice" style="width: 1200px;"></RawDataLineChart>
    </div>
  </div>

  <n-modal v-model:show="showModal">
    <n-card
        style="width: 500px"
        :bordered="false"
        role="dialog"
        aria-modal="true">
      <n-h2>{{ modalTitle }}</n-h2>
      Sequenz Name:
      <n-space vertical>
        <n-input
            v-model:value="sequenceName" :allow-input="allowNameInput" type="text" placeholder="Name der Sequenz">
        </n-input>
        <n-input
            style="max-width: 500px"
            :allow-input="() => {return false}"
            pair
            separator="bis"
            :placeholder="placeholder"
            v-model:value="displaySequence"
        />
        ({{time}})
      </n-space>

      <div style="margin-top: 15%">
        <n-button @click="showModal = false">Abbrechen</n-button>
        <n-button @click="sendSequence" style="float: right" type="primary">Übernehmen</n-button>
      </div>
    </n-card>
  </n-modal>

</template>

<script>
import RawDataLineChart from "@/components/LastSequences/RawDataLineChart.vue";
import DeviceSelectionTable from "@/components/DeviceSelectionTable.vue";
import {ref} from "vue";
import {createDiscreteApi} from "naive-ui";

// eslint-disable-next-line no-unused-vars
const {message, dialog} = createDiscreteApi(["message", "dialog"]);


export default {
  name: "CreateTab",
  components: {DeviceSelectionTable, RawDataLineChart},

  setup() {
    const selectedDevice = ref(null)
    const displaySequence = ref(null)
    const sequence = ref(null)
    const time = ref("0 Sekunden")
    const placeholder = ['Start', 'Ende']
    const showModal = ref(false)
    const canSave = ref(false)
    const sequenceName = ref('manual sequence')
    const modalTitle = ref("Sequenz für unbekanntes Gerät erstellen")

    async function saveSequence(start, end, id, label) {
      const url = 'http://goeppert013.informatik.intern.uni-leipzig.de/api/computers/lastsequence/save';

      const sequence = {
        start: start,
        ende: end,
        deviceId: id,
        label: label
      };

      const removeMessage = () => {
        if(messageReactive) {
          messageReactive.destroy();
          messageReactive = null;
        }
      };

      let messageReactive = message.loading("Speichere Sequenz...", {duration: 0})
      console.log(JSON.stringify(sequence))

      try {
        const response = await fetch(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(sequence)
        });

        if (!response.ok) {
          throw new Error('Failed to save sequence');
        }

        removeMessage()
        message.success('Sequence saved successfully');

        showModal.value = false


      } catch (error) {
        removeMessage()

        console.error(error);
        message.error(error.message);
      }
    }


    function sendSequence() {
      if(canSave.value) {

        const cleanText = (text) => text.replace(/^\s+|\s+$/g, '').replace(/\W+/g, ' ').trim().replace(/\s+/g, ' ');

        sequenceName.value = cleanText(sequenceName.value)


        const start = Math.round(sequence.value.start / 1000)
        const end = Math.round(sequence.value.end / 1000)

        saveSequence(start, end, selectedDevice.value, sequenceName.value)

      } else {
        message.error("Bitte vergeben Sie der Sequenz einen Namen und stellen Sie sicher, dass eine Sequenz markiert wurde.")
      }
    }

    function create(){
      showModal.value = true
      modalTitle.value = "Sequenz für Gerät mit ID " + selectedDevice.value + " erstellen:"

      if(sequence.value !== null && sequenceName.value !== "" && sequenceName.value !== " ") {
        console.log(sequence.value)
        canSave.value = true
      } else canSave.value = false
    }

    function onUpdatedSequence(range){
      sequence.value = range

      const startTime = new Date(range.start).toLocaleString("de-DE")
      const endTime = new Date(range.end).toLocaleString("de-DE")

      time.value = ((range.end - range.start) / 1000).toFixed(2) + " Sekunden"

      displaySequence.value = [startTime, endTime]
    }

    function OnUpdatedSelection(value){
      selectedDevice.value = value[0]
    }

    return {
      selectedDevice,
      OnUpdatedSelection,
      onUpdatedSequence,
      displaySequence,
      sequenceName,
      canSave,
      saveSequence,
      showModal,
      modalTitle,
      sendSequence,
      allowNameInput: (value) => !value || /^[a-zA-Z0-9\s_?!]+$/.test(value),
      placeholder,
      time,
      save: create,
      sequence
    }
  }
}
</script>

<style scoped>

.container {  display: grid;
  grid-template-columns: .5fr 2.4fr;
  grid-template-rows: 2fr 0.8fr;
  gap: 20px 20px;
  grid-auto-flow: row;
  grid-template-areas:
    "Table Diagram-1"
    "Table Create";
}

.Table { grid-area: Table; }

.Create {
  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: 1fr;
  gap: 0px 0px;
  grid-auto-flow: row;
  grid-template-areas: ".";
  grid-area: Create;
}

.Diagram-1 { grid-area: Diagram-1; }


</style>