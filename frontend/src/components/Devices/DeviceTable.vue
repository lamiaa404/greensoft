<template>

  <div id="section1">
    <n-input-group>
      <n-input v-model:value="searchBar" v-on:keyup.enter="handleSearchInput(searchBar)" type="text"  placeholder="Suchen..." style="margin-bottom: 5px" clearable @clear="handleSearchInput()"></n-input>
      <n-button @click="handleSearchInput(searchBar)">Suchen</n-button>
      <n-button @click="fetchDevices">
        <template #icon>
          <n-icon>
            <refresh-outline />
          </n-icon>
        </template>
      </n-button>
    </n-input-group>
    <n-config-provider :locale="deDE" :date-locale="dateDeDE">
      <n-data-table
          ref="table"
          :columns="columns"
          :data="tableData"
          v-model:checked-row-keys="checkedRowKeys"
          striped
          :style="{ height: `737px` }"
          flex-height
          scroll-x="900"
          table-layout="fixed">
      </n-data-table>
    </n-config-provider>
  </div>
  <div id="section2">
          <EditDeviceWindow v-bind:device="this.devices[checkedRowKeys[0]]" @deviceUpdated="onDeviceUpdated"></EditDeviceWindow>
  </div>


</template>

<script>

import {defineComponent, h, onMounted, reactive, ref} from "vue";
import { RefreshOutline } from '@vicons/ionicons5'
import {createDiscreteApi, dateDeDE, deDE, NTag} from "naive-ui";
import EditDeviceWindow from "@/components/Devices/EditDeviceWindow.vue";





// eslint-disable-next-line no-unused-vars
const { message, notification, dialog, loadingBar } = createDiscreteApi(
    ["message", "dialog", "notification", "loadingBar"]
);



// Get all stored devices from the API
async function fetchDevices() {
  try {
    const response = await fetch('http://goeppert013.informatik.intern.uni-leipzig.de/api/devices');

    if (!response.ok) {
      throw new Error('API unreachable.');
    }

    let data = await response.json();

    // Change names of attributes so that it fits the "correct" style and into the frontend
    data = data.map(device => ({
      id: device.id,
      name: device.name || "edison",
      location: device.standort || "unknown",
      power: "?",
      users: device.users || 0,
      tags: device.tags ? (device.tags).split(',') : []
    }))

    return data
  } catch (error) {
    message.error("Fehler: " + error.message + " Es können keine Daten Gerätedaten angezeigt werden. Bitte probieren Sie es später noch einmal.", {
      closable: true,
      duration: 10000
    })
  }
}

function uniqueTagsWithKey(devices) {
  return Array.from(new Set(devices.flatMap(device => device.tags)))
      .map((label) => ({ label: label, value: label }));
}

const tagsColumn =  {
  title: 'Tags',
  key: 'tag',
  width: 250,
  render(row) {
    const tags = row.tags.map((tagKey) => {
      return h(
          NTag,
          {
            style: {
              marginRight: "6px"
            },
            type: "primary",
            bordered: false
          },
          {
            default: () => tagKey
          }
      );
    });
    return tags;
  },
  // filter idea for tags
  filterOptions: [{
    label: "test",
    value: "test"
  }],
  filter (value, row) {
    return ~row.tags.indexOf(value)
  }
}

const columns = [
  {
    type: 'selection',
    multiple: false,
  },
  {
    title: 'ID',
    key: 'id',
    sorter: 'default',
    width: 100,
  },
  {
    title: 'Name',
    key: 'name',
    sorter: 'default',
    width: 150,
    resizable: true,
    defaultFilterOptionValues: [''],
    // eslint-disable-next-line no-unused-vars
    filter (value, row) {
      return row.name.includes(String(value));
    }
  },
  /*{
    title: 'Verbrauch',
    key: 'power',
    resizable: true,
    sorter (rowA, rowB) {
      return rowA.power - rowB.power
    },
    width: 150
  },*/
  {
    title: 'Standort',
    key: 'location',
    resizable: true,
    sorter: 'default',
    width: 150,

  },
  {
    title: 'Nutzer',
    key: 'users',
    resizable: true,
    sorter (rowA, rowB) {
      return rowA.users - rowB.users
    },
    width: 150

  },
  tagsColumn
]

export default defineComponent({
  name: "DeviceTable",
  components: {EditDeviceWindow, RefreshOutline},

  // eslint-disable-next-line no-unused-vars
  setup() {
    const tableRef = ref(null);
    const checkedRowKeysRef = ref([]);
    const devices = ref([])
    const tableData = ref([]);
    const tagsOptionsRef = ref([])
    const tagsOptions = tagsOptionsRef.value

    const columnsRef = reactive(columns)
    const tagsColumnRef = reactive(tagsColumn)


    function prepareTable() {
      // The table needs an additional key attribute.
      tableData.value = devices.value.map((device, index) => ({
        ...device,
        key: index
      }));
    }

    // Fetch devices when first loading the page
    onMounted(async ()=>{
      devices.value = await fetchDevices()
      prepareTable()

      const tags = uniqueTagsWithKey(devices.value)
      tagsColumnRef.filterOptions = tags
      console.log(tags)
    })

    function filterName(str) {
      tableRef.value.filter({
        name: [str]
      })
    }

    function handleSearchInput(v) {
      // If v is not null we will filter
      if (v) {
        message.info("Suche nach relevanten Geräten...");
        filterName(v);
        // otherwise we will "filter" by an empty string, so we get all rows.
      } else {
        filterName("");
      }
    }


    return {
      table: tableRef,
      checkedRowKeys: checkedRowKeysRef,
      devices,
      columns: columnsRef,
      tagsColumn: tagsColumnRef,
      tableData,
      deDE,
      tagsOptionsRef,
      dateDeDE,
      tagsOptions,
      searchBar: ref(null),
      handleSearchInput,
      fetchDevices,
      renderIcon () {
        return h(RefreshOutline)
      },
      uniqueTagsWithKey,

      onDeviceUpdated(updatedDevice) {
        console.log("Updated device.");

        const index = tableData.value.findIndex(device => device.id === updatedDevice.id);
        if (index === -1) {
          console.error(`Device with id ${updatedDevice.id} not found in tableData`);
          return;
        }

        // Update the device in our datatable tableData and devices array
        tableData.value[index] = { ...tableData.value[index], ...updatedDevice };
        devices.value[index] = { ...devices.value[index], ...updatedDevice };

        console.log(tableData.value[index]);
      },
    };
  }
});
</script>

<style scoped>

#section1 {float: left; width: 60%}
#section2 {float: left; width: 30%; margin-left: 1%}

</style>"