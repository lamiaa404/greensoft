<template>
  <title>Last-Sequenzen</title>
  <n-h1 >Last-Sequenzen</n-h1>

  <n-tabs type="line" animated style="min-width: 100vw; min-height: 100vh" size="large">
    <n-tab-pane name="create" tab="Erstellen">
      <CreateTab></CreateTab>
    </n-tab-pane>
    <n-tab-pane name="manage-and-analyze" tab="Verwalten & Analysieren">
      <SecondTab></SecondTab>
    </n-tab-pane>
  </n-tabs>

</template>

<script>
import CreateTab from "@/components/LastSequences/CreateTab.vue";
import SecondTab from "@/components/LastSequences/SecondTab";

export default {
  name: "LastSequences",
  components: {SecondTab, CreateTab}
}
</script>

<style scoped>

</style>