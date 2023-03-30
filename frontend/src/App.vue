<template>
  <n-config-provider :theme="theme">
    <n-space vertical>
     <n-layout has-sider>
        <Sidebar @updateSwitch="changeTheme"/>
        <n-layout className="content">
          <router-view  v-slot="{ Component }">
            <keep-alive>
             <component :is="Component" />
            </keep-alive>
          </router-view>
        </n-layout>
      </n-layout>
    </n-space>
  </n-config-provider>
</template>

<script>
import Sidebar from "./components/TheSidebar";
import {defineComponent, ref} from "vue";
import {darkTheme, useOsTheme} from "naive-ui";

const osThemeRef = useOsTheme();

const themeRef = checkOsTheme() ? ref(darkTheme) : ref(null);

function checkOsTheme() {
  if (osThemeRef.value === "dark") {
    return true;

  } else {
    return false;
  }
}


export default defineComponent({
  components: {
    Sidebar
  },
  setup() {
    return {
      theme: themeRef,
      osTheme: osThemeRef,
      changeTheme(v) {
        if(v === "dark") {
          themeRef.value = darkTheme;
        } else {
          themeRef.value = null;
        }
      }
    }
  }
})
</script>

<style>
.content {
  display: flex;
  margin-left: 20px;
}

</style>
