<template>
  <div class="hello">
    <h1>Kubernetes Monitor</h1>
    <div class="namespace-container">
      <div class="namespace-title">Monitored Namespace: </div>
      <div class="namespace">{{ namespace }}</div>
    </div>
    <div>
      <ul id="watcherList">
        <li v-for="watcher in watchers">
          <div class="watcher-name">{{ watcher.type}} :</div>
          <toggle-button @change="() => handleToggle(watcher)" v-model="watcher.active"/>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import api from './api'

export default {
  name: 'Monitor',
  data () {
      return { namespace: null , watchers: []}
  },
  created () {
    // fetch the data when the view is created and the data is
    // already being observed
    this.fetchData()
  },
  methods: {
    async fetchData () {
      this.namespace = await api.getNamespace()
      this.watchers = await api.getWatchers()
      console.log(this.watchers)
    },
    handleToggle(watcher) {
      console.log(`Setting watcher ${watcher.type} to ${watcher.active}`)
      api.setWatcher(watcher.type, watcher.active)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: flex;
  padding: 10px;
  margin: 0 10px;
  text-align: left;
}
a {
  color: #42b983;
}
.namespace {
  font-weight: bold;
  display: inline-block;
  padding-left: 20px;
}
.namespace-container {
  display: inline-flex;
}
.watcher-name {
  flex-grow: 1;
}
#watcherList {
  display: inline-grid
}
.vue-js-switch {
  padding-left: 20px;
}
</style>
