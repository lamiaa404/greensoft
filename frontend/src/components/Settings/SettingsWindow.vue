<template>
  <n-card>
    <n-space vertical size="large">
      <n-input-group>
        <n-space vertical>
          MQTT Host
          <n-input v-model:value="host" type="text" style="min-width: 500px;" placeholder=""/>

          Port
          <n-input v-model:value="port" type="text" :allow-input="onlyNumber" placeholder=""/>

          User
          <n-input v-model:value="user" type="text" placeholder=""/>

          Passwort
          <n-input v-model:value="password" type="password" show-password-on="click" placeholder="">

          </n-input>
        </n-space>
      </n-input-group>

      <n-space justify="space-between">
        <n-button @click="accept">Übernehmen</n-button>
        <n-button @click="reset">Zurücksetzen</n-button>
      </n-space>
    </n-space>
  </n-card>
</template>

<script>

import {createDiscreteApi} from "naive-ui";
const {message, dialog} = createDiscreteApi(["message", "dialog"]);

export default {
  name: "SettingsWindow",

  methods: {
    reset() {
      this.host = null;
      this.port = null;
      this.user = null;
      this.password = null;
      message.info("Alle Eingaben wurden zurückgesetzt.");
    },
    accept() {
      //todo: second if-statement for checking if host,port,user,password equal to originally data
      if(!(this.host === "" || this.port === "" || this.user === "" || this.password === ""
           || this.host == null || this.port == null || this.user == null || this.password == null)) {
        dialog.warning({
          title: 'Bestätigen',
          content: 'Sind Sie sicher diese Einstellungen zu übernehmen?',
          positiveText: "Übernehmen",
          negativeText: "Abbrechen",
          onPositiveClick: () => {
            message.success("Alle Eingaben wurden erfolgreich übernommen.");
          },
          onNegativeClick: () => {
            message.error("Die Eingaben wurden nicht übernommen.");
          }
        })
      } else {
        message.error("Bitte füllen sie alle Felder aus.")
      }
    }
  },

  data() {
    return {

      onlyNumber: (value) => !value || /^\d+$/.test(value),

      host: 'test@test.de',
      port: '8865',
      user: 'Test',
      password: 'Test',
    }
  }
}
</script>

<style scoped>

.n-card {
  min-width: 500px;
}

</style>