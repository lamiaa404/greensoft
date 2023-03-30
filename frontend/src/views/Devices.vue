<template>
  <title>Geräte</title>
  <n-h1 >Geräte</n-h1>
  <n-space>
  <DeviceTable></DeviceTable>
  </n-space>
</template>

<script>
import DeviceTable from "@/components/Devices/DeviceTable";

export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: "Devices",
  components: {DeviceTable},
  data(){ // data(){} is used for storing private data
    return {
    }
  }
}
</script>

<style scoped>

</style>
