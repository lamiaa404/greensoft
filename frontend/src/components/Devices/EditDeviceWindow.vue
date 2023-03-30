<template>
  <n-card>
    <n-space vertical >
      <n-space justify="space-between">
        <n-h3>Ausgewähltes Gerät</n-h3>
        <n-tag>ID: {{deviceId}}</n-tag>
      </n-space>

      <n-space vertical>
        Gerätename
        <n-input v-model:value="newName" type="text" placeholder="Gerätename hier eingeben..."/>
        Standort
        <n-input v-model:value="newLocation" type="text" placeholder="Standort hier eingeben..." />
        Nutzer
        <n-input-number v-model:value="newUser" type="text" placeholder="Useranzahl hier eingeben..." @update:value="handleNumberChange"/>
        Tags
        <n-dynamic-tags v-model:value="newTags" @update:value="handleCreate" />
      </n-space>
    </n-space>

    <n-space justify="space-between" style="padding-top: 2vh;">
      <n-button @click="accept" :disabled="isLoading">Übernehmen</n-button>

      <n-message-provider>
        <n-button @click="cancel">Abbrechen</n-button>
      </n-message-provider>
    </n-space>
  </n-card>
</template>

<script>
import {defineComponent, ref} from "vue";
import {createDiscreteApi} from "naive-ui";
import {Device} from "@/utils/Device";

// We must use DiscreteAPI when we are using the Options API to write components in Vue https://www.naiveui.com/en-US/light/components/discrete
// eslint-disable-next-line no-unused-vars
const { message, notification, dialog, loadingBar } = createDiscreteApi(
    ["message", "dialog", "notification", "loadingBar"]
);


export default defineComponent ({
  name: "EditDeviceWindow",

  methods: {
    // Used to reset device's data to its original.
    reset: function() {
      this.newName = this.device.name;
      this.newLocation = this.device.location;
      this.newUser = this.device.users;
      this.newTags = this.device.tags;
      this.deviceId = this.device.id;
    },

    // Called when "cancel" was pressed.
    cancel: function() {
      this.reset();
      message.info("Alle Eingaben wurden zurückgesetzt.");
    },

    // Is called when a new tag is created. Filters out duplicates. If duplicates were removed, a message is displayed.
    handleCreate: function (labels){
      this.newTags = [...new Set(this.newTags)]; // Convert array to a set; A set cannot have duplicates.
      this.newTags = this.sanitizeTags(this.newTags);

      if(this.newTags.length !== labels.length){
        message.info("Tag existiert bereits.");
      }
    },

    // Remove this function later on maybe, and just validate when clicking on "accept".
    handleNumberChange: function (number) {
      if(number == null) {
        this.newUser = 0; // reset to 0 instead of blank
        message.error("Bitte gebe eine Nummer an.");
      }
    },

    /**
     * Sanitizes an array of strings by removing any non-alphanumeric characters that are not spaces, underscores, question marks, or exclamation points.
     * @param {string[]} tags - The array of tags to sanitize
     * @returns {string[]} - A new array of sanitized tags
     */
    sanitizeTags: function (tags) {
      // Regular expression that matches any alphanumeric character as well as spaces, underscores, question marks, and exclamation points
      const allowedCharsRegex = /^[a-zA-Z0-9\s_?!]+$/;

      // Use the Array.map() function to iterate through each tag and replace any non-alphanumeric characters with an empty string, then trim any leading or trailing spaces
      // Use Array.filter() to remove any tags that don't match the allowed characters regex
      return tags.map(tag => tag.replace(/[^a-zA-Z0-9\s_?!]/g, '').trim()).filter(tag => allowedCharsRegex.test(tag));
    },

    // Validates all input fields
    validate: function() {
      let success = true;

      if(!this.device) { message.error("Bitte wählen Sie ein Gerät aus."); success = false } else
      if(this.newName === "") { message.error("Das Gerät muss einen Namen besitzen."); success = false; } else
      if(this.newUser === null) { message.error("Das Gerät muss eine Anzahl an Benutzern besitzen."); success = false; }

      return success;
    },

    // Called when pressing accept button
    accept: function () {
      if(this.validate()){
        message.info("Gerät Anpassung ist in Bearbeitung...");

        const updatedDevice = new Device(
            this.deviceId,
            this.newName,
            this.device.power,
            this.newLocation,
            parseInt(this.newUser),
            this.newTags
        )


        fetch("http://goeppert013.informatik.intern.uni-leipzig.de/api/devices/edit", {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            id: updatedDevice.id,
            name: updatedDevice.name,
            standort: updatedDevice.location,
            users: updatedDevice.users,
            tags: updatedDevice.tags.toString()
          })
        })
            .then(response => {
              if (!response.ok) {
                throw new Error('Error editing device');
              } else {
                message.success("Gerät wurde erfolgreich geändert.");

                // We will now emit an event. The parent (the deviceTable) can listen to it and make changes.
                console.log("emitted");
                this.$emit('deviceUpdated', updatedDevice);
              }
              return response.json();
            })
            .catch(error => message.error('Error editing device: ' + error.message, {
              duration: 10000,
              closable: true
            }));



        // Disable accept button
        //this.isLoading = true;
        //loadingBar.start();

        /*
        // enable accept button again
        // For timeouts, "this" means something different, so we either have to use bind
        setTimeout(() => {

          // Do cosmetic stuff
          this.isLoading = false;
          loadingBar.finish();


          }, 800);
         */

      }
    }
  },

  // We will watch if our device object changes. If it does change e.g. the user selects a different device, we will update accordingly
  // This can be solved way cleaner, but it works for now.
  watch: {
    device: function() {
      this.reset();

      if(this.device) {
        let msg = `${this.device.name} ausgewählt.`;
        message.info(msg);
      } else {
        console.log("nein")
      }


    }
  },

  props: {
    device: Object // Parent gives us device object
  },

  data(){
    return {
      deviceId: -1,
      newName: '',
      newLocation: '',
      newUser: 0,
      newTags: [],
      isLoading: ref(false),
    }
  },

  created() {
    //this.reset();
  }
})
</script>

<style scoped>
.n-card {
  min-width: 500px;
}

</style>
