<template>
  <n-layout-sider
      style="min-height: 100vh"
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      show-trigger
      @collapse="hide"
      @expand="show"
  >
    <n-menu
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        class="menu"
        default-value="start"
    />
    <div id="theme" style="margin-top: 600px">
      <div style="margin-left: 60px">Light/Dark Mode</div>
      <n-switch :rail-style="railStyle" size="large" style="margin-left: 90px; margin-top: 5px"
                checked-value="dark" unchecked-value="light" :default-value="osTheme" @update:value="handleSwitch">
        <template #checked-icon>
          <n-icon>
            <MoonOutline />
          </n-icon>
        </template>
        <template #unchecked-icon>
          <n-icon>
            <SunnyOutline />
          </n-icon>
        </template>
      </n-switch>
    </div>
  </n-layout-sider>
</template>

<script>
import {h, defineComponent} from "vue";
import { NIcon, useOsTheme} from "naive-ui";
import { RouterLink } from "vue-router";
// eslint-disable-next-line no-unused-vars
import { HomeOutline, SettingsOutline, BarChartOutline, DesktopOutline, BarcodeOutline, SunnyOutline, MoonOutline } from "@vicons/ionicons5";

function renderIcon(icon) {
  return () => h(NIcon, null, { default: () => h(icon) });
}

const menuOptions = [
  {
    label: () => h(
        RouterLink,
        {
          to: {
            name: "Home",
          }
        },
        { default: () => "Home" }
    ),
    key: "start",
    icon: renderIcon(HomeOutline)
  },
  {
    label: () => h(
        RouterLink,
        {
          to: {
            name: "Analyze",
          }
        },
        { default: () => "Analysieren" }
    ),
    key: "analysieren",
    icon: renderIcon(BarChartOutline)
  },
  {
    label: () => h(
        RouterLink,
        {
          to: {
            name: "Devices",
          }
        },
        { default: () => "Geräte" }
    ),
    key: "devices",
    icon: renderIcon(DesktopOutline)
  },
  {
    label: () => h(
        RouterLink,
        {
          to: {
            name: "LastSequences",
          }
        },
        { default: () => "Last-Sequenzen" }
    ),
    key: "last-sequences",
    icon: renderIcon(BarcodeOutline)
  },
  /*{
    label: () => h(
        RouterLink,
        {
          to: {
            name: "Settings",
          }
        },
        { default: () => "Einstellungen" }
    ),
    key: "einstellungen",
    icon: renderIcon(SettingsOutline)
  }*/
];

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

export default defineComponent({
  components: {
    SunnyOutline,
    MoonOutline,
  },

  setup(props, context) {
    const osThemeRef = useOsTheme();
    return {
      menuOptions,
      hide() {
        document.getElementById("theme").style.display = "none";
      },
      show() {
        sleep(150).then(() => {document.getElementById("theme").style.display = "block";});
      },
      railStyle: ({checked}) => {
        const style = {};
        if (checked) {
          style.background = "#454549";
        } else {
          style.background = "#d9d9da";
        }
        return style;
      },
      handleSwitch(v) {
        context.emit("updateSwitch", v)
      },
      osTheme: osThemeRef
    };
  }
});
</script>

<style>


</style>