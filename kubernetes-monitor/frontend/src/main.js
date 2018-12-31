import Vue from 'vue'
import App from './App.vue'
import ToggleButton from 'vue-js-toggle-button'

Vue.config.productionTip = false

Vue.use(ToggleButton)

new Vue({
  render: h => h(App),
}).$mount('#app')

