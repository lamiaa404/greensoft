<template>
  <n-space vertical>
    <div style="width: 300px">
      <n-button  @click="deleteOnClick">Löschen</n-button>
    </div>
    <n-data-table
        :ref="table"
        :columns="columns"
        :data="data"
        @update:checked-row-keys="handleSelectionChange"
        striped
        flex-height
        table-layout="fixed"
        style="height: 683px; width: 450px">
    </n-data-table>
  </n-space>

  <n-modal v-model:show="showModal">
    <n-card
        v-on:keyup.enter="editSequence"
        style="width: 400px"
        title="Sequenz bearbeiten"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true">
      Identifikation
      <n-space vertical size="large">
        <n-input v-model:value="sequenceID" :disabled="true"></n-input>
        Name
      </n-space>
      <n-input
          v-model:value="sequenceName" type="text" placeholder="">
      </n-input>
      <div style="margin-top: 8%">
        <n-button @click="closeEditWindow">Abbrechen</n-button>
        <n-button @click="editSequence" style="float: right" type="primary">Übernehmen</n-button>
      </div>
    </n-card>
  </n-modal>
</template>
<script>

import {generateDummySequencesData} from "@/utils/DummyData";
import {h, reactive, ref} from "vue";
import {createDiscreteApi, NButton, NIcon} from "naive-ui";
const {message} = createDiscreteApi(["message"]);
import { CreateOutline } from "@vicons/ionicons5";

export default {
  name: "AnalyzeTable",

  // eslint-disable-next-line no-unused-vars
  setup(props, context) {

    const showModalRef = ref(false);

    const sequenceNameRef = ref(null);

    const sequenceIDRef = ref(null);

    const columns = reactive([
      {
        type: 'selection',
        multiple: true
      },
      {
        title: 'Name',
        key: 'name',
        sorter: 'default',
        width: 130,
      },
      {
        title: 'Gerätename',
        key: 'device',
        sorter: 'default',
        width: 120,
      },
      {
        key: 'edit',
        width: 50,
        render(row) {
          function editWindow(row) {
            sequenceNameRef.value = row.name;
            sequenceIDRef.value = row.id;
            showModalRef.value = true;
          }

          return h(
              NButton,
              {
                size: 'tiny',
                bordered: false,
                onClick: () => editWindow(row),
              },
              { default: () => h(NIcon, {size: 19}, { default: () => h(CreateOutline)})}
          )
        }
      }
    ]);


    const sequences = generateDummySequencesData(15);
    const sequenceData = Array.from({ length: 15 }).map((_, index) => ({
      id: sequences[index].id,
      name: sequences[index].name,
      device: sequences[index].device,
      size: sequences[index].size,
      key: index
    }));

    const tableRef = ref(null);
    const checkedRowKeysRef = ref([]);

    const data = ref(sequenceData);

    return {
      table: tableRef,
      checkedRowKeys: checkedRowKeysRef,
      sequences,
      sequenceData,
      data,
      columns,
      showModal: showModalRef,
      sequenceName: sequenceNameRef,
      sequenceID: sequenceIDRef,

      deleteOnClick() {
        const sequence = [];

        checkedRowKeysRef.value.forEach(item => {
          sequence.push(sequences.find(i => i.id === item));
        });

        const difference = sequences.filter(x => !sequence.includes(x));

        console.log(difference);

        data.value = Array.from({ length: difference.length }).map((_, index) => ({
          id: difference[index].id,
          name: difference[index].name,
          device: difference[index].device,
          size: difference[index].size,
          key: index
        }));

      },
      handleCheck(rowKeys) {
        checkedRowKeysRef.value = rowKeys;
      },
      closeEditWindow() {
        showModalRef.value = false;
      },
      editSequence() {

        const sequence = sequences.findIndex(x => x.id === sequenceIDRef.value);

        if(sequenceData[sequence].name === sequenceNameRef.value) {
          message.info("Keine Änderungen");
          showModalRef.value = false;
          return;
        }

        if(sequences.find(x => x.name === sequenceNameRef.value)) {
          message.error("Dieser Sequenzname ist bereits vergeben.");
          return;
        }

        data.value[sequence].name = sequenceNameRef.value;

        sequenceData[sequence].name = sequenceNameRef.value;

        sequences[sequence].name = sequenceNameRef.value;

        message.success("Erfolgreich umbenannt zu " + sequenceData[sequence].name);

        showModalRef.value = false;
      },
      handleSelectionChange(v) {
        context.emit("selectionChanged", v)
      }
    }
  }
}
</script>


<style scoped>

</style>