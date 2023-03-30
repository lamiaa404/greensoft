<template>
  <div class="container">
    <div class="table">
      <SequencesTable @selectionChanged="OnUpdatedSelection"></SequencesTable>
    </div>
    <div class="diagram">
      <SequenceLineChart style="float: top; width: 1000px" :selected-sequences="selectedSequences"></SequenceLineChart>
<!--      <n-button @click="jo">Test</n-button>-->
    </div>
  </div>
</template>

<script>
import SequencesTable from "@/components/LastSequences/SequencesTable";
import SequenceLineChart from "@/components/LastSequences/SequenceLineChart";
import {ref} from "vue";
import {createDiscreteApi} from "naive-ui";

const {message} = createDiscreteApi(["message"]);

// const range = ref([
//   Date.now(),
//   Date.now() + 7*86400000
// ]);


const selectedSequences = ref([]);


export default {
  name: "SecondTab",
  components: {SequenceLineChart, SequencesTable},
  setup() {
    return {
      selectedSequences,
      OnUpdatedSelection(v) {
        selectedSequences.value = v;
      },
      async jo() {
        try {
          const response = await fetch('http://goeppert013.informatik.intern.uni-leipzig.de/api/computers/lastsequence?id=34&start=1678000000&end=1678122531');
          const data = response.json();
          console.log(data);
          console.log("Test")
        } catch (error) {
          message.error("Fehler: " + error.message + " Es können keine Sequenzdaten angezeigt werden." +
              " Bitte probieren Sie es später noch einmal.", { closable: true, duration: 10000 })
        }

      }
    }
  }
}
</script>

<style scoped>
.container {
  display: flex;
}

.diagram {
  margin-left: 4%;
  margin-top: 4%;
}

</style>