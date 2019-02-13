<template>
  <div class="hello">
    <h1>Kubernetes Monitor</h1>
    <div class="namespace-container">
      <md-field>
        <label>Monitored Namespace</label>
        <md-input v-model="namespace" placeholder="Set Namespace" @change="() => handleNamespaceUpdate(namespace)"></md-input>
      </md-field>
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
      console.info(this.watchers)
    },
    handleToggle(watcher) {
      api.setWatcher(watcher.type, watcher.active)
    },
    handleNamespaceUpdate(namespace) {
      api.setNamespace(namespace)
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
input {
  border-radius: 20px;
  border-color: rgb(191, 203, 217);
  margin: 0 20px;
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
