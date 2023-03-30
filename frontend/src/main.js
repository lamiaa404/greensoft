import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // <---
import naive from 'naive-ui'
// General Font
import 'vfonts/Lato.css'
// Monospace Font
import 'vfonts/FiraCode.css'

createApp(App).use(router).use(naive).mount('#app')