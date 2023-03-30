<template>
  <title>Home</title>
  <n-h1>Home</n-h1>

  <div id="main">
    <HomeOverview style="width: 420px"></HomeOverview>
  </div>

  <div id= "gauge" style="margin-top: 2%;">
    <HomeGauge></HomeGauge>
  </div>

  <div style="margin-top: 55%; overflow: hidden;">
<!--    <PieChartTag style="width: 350px; float: left; height: 300px;"></PieChartTag>-->

    <PieChartLocation style="width: 350px; float: left; height: 300px"></PieChartLocation>
  </div>

</template>

<script>
import { defineComponent } from "vue";
import PieChartTag from "@/components/Home/PieChartTag";
import PieChartLocation from "@/components/Home/PieChartLocation";
import HomeOverview from "@/components/Home/HomeOverview";
import HomeGauge from "@/components/Home/HomeGauge";

export default defineComponent({
  components: {
    HomeGauge,
    HomeOverview,
    // eslint-disable-next-line vue/no-unused-components
    PieChartTag,
    PieChartLocation
  },
  // eslint-disable-next-line vue/multi-word-component-names
  name: "Home"


})
</script>

<style scoped>

</style>