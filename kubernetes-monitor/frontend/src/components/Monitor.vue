<template>
  <div class="hello">
    <h1>Kubernetes Monitor</h1>
    <div>
      Monitored Namespace: <div class="bold">{{ namespace }}</div>
    </div>
    <div>
      <ul id="watcherList">
        <li v-for="watcher in watchers">
          {{ watcher.type}} : <toggle-button @change="() => handleToggle(watcher)" v-model="watcher.active"/>
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
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
.bold {
  font-weight: bold;
}
#watcherList {
  display: inline-grid
}
</style>
