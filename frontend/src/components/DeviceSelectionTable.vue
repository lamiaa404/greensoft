<template>
  <n-space vertical >
    <n-button-group>
      <n-dropdown trigger="hover" :options="tagsOptions" @select="handleSelectTags">
        <n-button>Tags</n-button>
      </n-dropdown>
      <n-dropdown trigger="hover" :options="locationOptions" @select="handleSelectLocation">
        <n-button>Standort</n-button>
      </n-dropdown>
      <n-button  @click="unfilterOnClick">Reset</n-button>
      <n-button @click="refreshDevices" style="width: 30px">
        <template #icon>
          <n-icon>
            <refresh-outline />
          </n-icon>
        </template>
      </n-button>
    </n-button-group>

    <n-config-provider :locale="deDE" :date-locale="dateDeDE">
      <n-data-table
          @update-checked-row-keys="handleSelectionChange"
          :ref="table"
          :columns="columns"
          :data="tableData"
          v-model:checked-row-keys="checkedRowKeys"
          striped
          :style="{ height: $props.tableHeight, width: `300px`}"
          flex-height
          table-layout="fixed">
      </n-data-table>
    </n-config-provider>
  </n-space>
</template>

<script>
import {h, onMounted, reactive, ref} from "vue";
import {createDiscreteApi, dateDeDE, deDE, NTag} from "naive-ui";
import {RefreshOutline} from "@vicons/ionicons5";

const {message} = createDiscreteApi(["message"]);

export default {
  name: "DeviceSelectionTable",
  components: {RefreshOutline},


  props: {
    multipleAllowed: Boolean,
    tableHeight: String
  },


  // eslint-disable-next-line no-unused-vars
  setup(props, context) {
    const tableRef = ref(null);
    const checkedRowKeysRef = ref([]);
    const devices = ref([]);
    const tableData = ref([]);

    const tagsOptions = ref([]);
    const locationOptions = ref([]);

    // Get all stored devices from the API
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
          power: "?",
          users: device.users || 0,
          tags: device.tags ? (device.tags).split(',') : []
        }))


        // The table needs an additional key attribute.
        tableData.value = devices.value.map((device) => ({
          ...device,
          key: device.id
        }));

        tagsOptions.value = uniqueTagsWithKey(devices.value)
        locationOptions.value = uniqueLocationsWithKey(devices.value)


      } catch (error) {
        message.error("Fehler: " + error.message + " Es können keine Gerätedaten angezeigt werden." +
            " Bitte probieren Sie es später noch einmal.", {
          closable: true,
          duration: 10000
        })
      }
    }

    // Used to get all possible existing locations a device can have
    function uniqueLocationsWithKey(devices) {
      // Find all unique locations
      const uniqueLocations = [...new Set(devices.map(device => device.location))];

      // Generate an array of objects with label and key attributes
      return uniqueLocations.map(location => ({
        label: location,
        key: location
      }));
    }

    // Used to get all possible existing tags a device can have
    function uniqueTagsWithKey(devices) {
      return Array.from(new Set(devices.flatMap(device => device.tags)))
          .map((label, index) => ({ label, key: label, id: index }));
    }

    onMounted(()=>{
      fetchDevices()
    })


    const location = reactive({
      key: 'location',
      ellipsis: true,
      filterOptionValue: null,
      filter(value, row) {
        return !!~row.location.indexOf(value.toString());
      }
    });

    const tags = reactive({
      key: 'tags',
      ellipsis: true,
      render(row) {
        return row.tags.map((tagKey) => {
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
      },
      filterOptionValue: null,
      filter(value, row) {
        return ~row.tags.indexOf(value);
      }
    });

    const columns = reactive([
      {
        type: 'selection',
        multiple: props.multipleAllowed
      },
      {
        title: "ID",
        key: "id",
        sorter: 'default',
        width: 60
      },
      {
        title: 'Name',
        key: 'name',
        sorter: 'default',
        width: 190,
      },
      /*{             // Disabled for now as it requires more work to get this working. Usefulness is also questionable.
        title: 'Verbrauch',
        key: 'power',
        sorter (rowA, rowB) {
          return rowA.power - rowB.power
        },
        width: 110,
      },*/
      location,
      tags
    ]);

    function refreshDevices() {
      message.info("Aktualisiere...");
      fetchDevices();
      message.success("Aktualisierung erfolgreich!");
    }

    return {
      table: tableRef,
      checkedRowKeys: checkedRowKeysRef,
      columns,
      deDE,
      dateDeDE,
      locationOptions,
      devices,
      tableData,
      tagsOptions,
      fetchDevices,
      refreshDevices,
      renderIcon () {
        return h(RefreshOutline)
      },
      handleSelectTags(key) {
        message.info("Suche nach Geräten mit dem Tag " + key.toString());
        tags.filterOptionValue = key;
      },

      handleSelectLocation(key) {
        message.info("Suche nach Geräten mit dem Standort " + key.toString());
        location.filterOptionValue = key;
      },

      unfilterOnClick() {
        message.info("Filter zurückgesetzt");
        tags.filterOptionValue = null;
        location.filterOptionValue = null;
      },

      handleSelectionChange(v) {
        context.emit("selectionChanged", v)
      }
    }
  }
}
</script>


<style scoped>

.n-button {
  width: 90px;
}

</style>