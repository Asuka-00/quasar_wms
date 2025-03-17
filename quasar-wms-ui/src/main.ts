import { createApp } from 'vue'
import { Quasar,Notify,Dialog } from 'quasar'
import quasarLang from 'quasar/lang/zh-CN'

// Import icon libraries
import '@quasar/extras/material-icons/material-icons.css'

// Import Quasar css
import 'quasar/dist/quasar.css'


// Assumes your root component is App.vue
// and placed in same folder as main.js
import App from './App.vue'


import router from './router'

import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const myApp = createApp(App)

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

myApp.use(Quasar, {
  plugins: {Notify,Dialog}, // import Quasar plugins and add here
  lang: quasarLang,
})

myApp.use(router)
myApp.use(pinia)
// Assumes you have a <div id="app"></div> in your index.html
myApp.mount('#app')