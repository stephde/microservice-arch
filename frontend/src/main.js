import Vue from 'vue'
import App from './App.vue'
import ToggleButton from 'vue-js-toggle-button'
import { MdField, MdButton } from 'vue-material/dist/components'
import 'vue-material/dist/vue-material.min.css'
import 'vue-material/dist/theme/default.css'
import TreeView from "vue-json-tree-view"
import VueFoldable from 'vue-foldable'
import 'vue-foldable/dist/vue-foldable.css'

Vue.component('foldable', VueFoldable)

Vue.config.productionTip = false

Vue.use(ToggleButton)
Vue.use(MdField)
Vue.use(MdButton)
Vue.use(TreeView)

new Vue({
  render: h => h(App),
}).$mount('#app')

