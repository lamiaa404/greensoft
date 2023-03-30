<template>
  <n-config-provider :locale="deDE" :date-locale="dateDeDE">

    <n-date-picker :is-date-disabled="disableDate" format="dd.MM.yyyy" v-model:value="dateRange"
                   type="daterange" @confirm="onConfirm" clearable style="width: 300px; margin-bottom: 20px"/>

    <n-data-table
        remote
        ref="table"
        :columns="columns"
        :data="data"
        :loading="loading"
        :pagination="pagination"
        :row-key="rowKey"
        @update:checked-row-keys="handleSelectionChange"
        @update:sorter="handleSorterChange"
        @update:page="handlePageChange"
    />
  </n-config-provider>
</template>

<script>
// eslint-disable-next-line no-unused-vars
import {defineComponent, h, onMounted, reactive, ref} from 'vue'
// eslint-disable-next-line no-unused-vars
import {createDiscreteApi, dateDeDE, deDE, NButton, NIcon} from "naive-ui";
// eslint-disable-next-line no-unused-vars
import {CreateOutline} from "@vicons/ionicons5";

const {message} = createDiscreteApi(["message"]);

const id = {
  title: 'ID',
  key: 'id',
  sorter: true,
  sortOrder: false,
  width: 80
}

const name = {
  title: 'Name',
  key: 'name',
  width: 120
}

const columns = [
  {
    type: 'selection'
  },
  id,
  name,
  {
    title: 'Gerätename',
    key: 'device',
    width: 120
  },
  // {
  //   key: 'edit',
  //   width: 50,
  //   render(row) {
  //     function editWindow(row) {
  //       console.log(row);
  //     }
  //     return h(
  //         NButton,
  //         {
  //           size: 'tiny',
  //           bordered: false,
  //           onClick: () => editWindow(row),
  //         },
  //         {default: () => h(NIcon, {size: 19}, {default: () => h(CreateOutline)})}
  //     )
  //   }
  // }
]

function getCorrectDate(range){
  let start = 0
  let end = 0

  start = new Date(range[0]).setHours(0,0,0,0)
  end = new Date(range[1] + 86400*1000).setHours(0,0,0,0)

  return [start / 1000, end / 1000]
}

//FOR SCHLEIFE ALLE IDS

const dateRange = ref([
  Date.now() - 2*86400000,
  Date.now() - 86400*1000
]);

let data = [];

//fetchAllSequences(Math.ceil(dateRange.value[0]/1000), Math.ceil(dateRange.value[1]/1000));

async function fetchAllSequences(start, end) {
  try {
    let sequences = [];

    let allDevices = await fetchDevices();

    //allDevices = allDevices.slice(1,2)

    console.log(allDevices);
    for(let i = 0; i < allDevices.length; i++) {
      const temp = await fetchSequences(allDevices[i].id, start, end, allDevices[i].name);
      sequences = sequences.concat(temp)
    }
    await console.log(sequences);

    data = sequences;
  } catch(error) {
    console.log(error);
  }
}

async function fetchDevices() {
  try {
    const response = await fetch('http://goeppert013.informatik.intern.uni-leipzig.de/api/devices');
    const data = await response.json();


    return data.map(device => ({
      id: device.id,
      name: device.name || "edison"
    }));

  } catch(error) {
    console.log(error);
  }

}


async function fetchSequences(deviceid, start, end, name = 'edison') {
  try {
    const sequences = ref([]);

    const response = await fetch(`http://goeppert013.informatik.intern.uni-leipzig.de/api/computers/lastsequence?id=${deviceid}&start=${start}&end=${end}`);

    if (!response.ok) {
      throw new Error('API unreachable.');
    }

    const d = await response.json();



    // Change names of attributes so that it fits the "correct" style and into the frontend
    sequences.value = d.map(sequences => ({
      id: sequences.id,
      name: sequences.label,
      device: name,
      start: sequences.start,
      end: sequences.ende,
      deviceId: sequences.deviceId
    }))

    // The table needs an additional key attribute.
    return sequences.value.map((sequence) => ({
      ...sequence,
      key: id
    }));


  } catch (error) {
    message.error("Fehler: " + error.message + " Es können keine Sequenzdaten angezeigt werden." +
        " Bitte probieren Sie es später noch einmal.", {
      closable: true,
      duration: 10000
    })
  }
}

function query (page, pageSize = 10, order = 'ascend', filterValues = []) {
  return new Promise((resolve) => {
    const copiedData = data.map((v) => v)
    const orderedData = order === 'descend' ? copiedData.reverse() : copiedData
    const filteredData = filterValues.length
        ? orderedData.filter((row) => filterValues.includes(row.name))
        : orderedData
    const pagedData = filteredData.slice((page - 1) * pageSize, page * pageSize)
    const total = filteredData.length
    const pageCount = Math.ceil(filteredData.length / pageSize)
    setTimeout(
        () =>
            resolve({
              pageCount,
              data: pagedData,
              total
            })
    )
  })
}



const dataRef = ref([]);

function disableDate(date) {
  return date < 1675724400*1000 || date > Date.now()
}

export default defineComponent({
  // eslint-disable-next-line no-unused-vars
  setup (props, context) {
    const loadingRef = ref(true)
    const columnsRef = ref(columns)
    const column1Reactive = reactive(id)
    const column2Reactive = reactive(name)
    const paginationReactive = reactive({
      page: 1,
      pageCount: 1,
      pageSize: 9,
      suffix ({ itemCount }) {
        return `${itemCount} Ergebnisse`
      }
    })



    async function onConfirm(value) {
      if (!loadingRef.value) {
        loadingRef.value = true
        await fetchAllSequences(getCorrectDate(value)[0], getCorrectDate(value)[1])
        query(
            paginationReactive.page,
            paginationReactive.pageSize,
        ).then((data) => {
          dataRef.value = data.data
          paginationReactive.pageCount = data.pageCount
          paginationReactive.itemCount = data.total
          loadingRef.value = false
        })
      }
    }

    onMounted(async () => {
      await fetchAllSequences(getCorrectDate(dateRange.value)[0], getCorrectDate(dateRange.value)[1])


      query(
          paginationReactive.page,
          paginationReactive.pageSize,
          column1Reactive.sortOrder,
      ).then((data) => {
        dataRef.value = data.data;
        paginationReactive.pageCount = data.pageCount
        paginationReactive.itemCount = data.total
        loadingRef.value = false
      })
    })

    return {
      data: dataRef,
      columns: columnsRef,
      id: column1Reactive,
      name: column2Reactive,
      pagination: paginationReactive,
      loading: loadingRef,
      dateDeDE, deDE,
      dateRange,
      disableDate,
      onConfirm,
      rowKey (rowData) {
        return rowData.id
      },
      handleSorterChange (sorter) {
        if (!sorter || sorter.columnKey === 'id') {
          if (!loadingRef.value) {
            loadingRef.value = true
            query(
                paginationReactive.page,
                paginationReactive.pageSize,
                !sorter ? false : sorter.order,
            ).then((data) => {
              column1Reactive.sortOrder = !sorter ? false : sorter.order
              dataRef.value = data.data
              paginationReactive.pageCount = data.pageCount
              paginationReactive.itemCount = data.total
              loadingRef.value = false
            })
          }
        }
      },
      handlePageChange (currentPage) {
        if (!loadingRef.value) {
          loadingRef.value = true
          query(
              currentPage,
              paginationReactive.pageSize,
              column1Reactive.sortOrder,
          ).then((data) => {
            dataRef.value = data.data
            paginationReactive.page = currentPage
            paginationReactive.pageCount = data.pageCount
            paginationReactive.itemCount = data.total
            loadingRef.value = false
          })
        }
      },
      // eslint-disable-next-line no-unused-vars
      handleSelectionChange(v) {

        let selectedSequences = data.filter(item => v.includes(item.id));
        context.emit("selectionChanged", selectedSequences)
      }
    }
  }
})
</script>