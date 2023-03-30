<template>
  <n-card>
    <n-space vertical>
      Derzeitiger Verbrauch nach Tag
      <Pie :chart-data="data" :chart-options="chartOptions" style="margin-top: -35px;"/>
    </n-space>
  </n-card>
</template>

<script>

import { Pie } from 'vue-chartjs'
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
ChartJS.register(ArcElement, Tooltip, Legend)

const data = {
  labels: ['Computer','Server'],
  datasets: [
    {
      backgroundColor: ['#16bdd5', '#f564a3'],
      data: [50,50]
    }
  ],
}


export default {
  name: 'PieChartTag',
  components: {Pie},
  data() {
    return {
      data,
      chartOptions: {
        responsive: true,
        plugins: {
          legend: {
            position: "right",
            display: true,
          }
        },
      }

    }
  }
}
</script>

<style scoped>
</style>