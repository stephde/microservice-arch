<template>
  <div class="global-container">
    <h1>Kubernetes Consumer</h1>
    <div class="property-container" v-bind:class="{ connected: isKubeConsumerConnected, disconnected: !isKubeConsumerConnected }">
      <md-field>
        <label>API Url</label>
        <md-input v-model="apiUrl" placeholder="Set Base Url" @change="() => handleUrlUpdate(apiUrl)"></md-input>
      </md-field>
    </div>
    <div class="property-container">
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

    <!--------------- ---------------->
    <h1>Zipkin Consumer</h1>
    <div class="property-container" v-bind:class="{ connected: isZipkinConsumerConnected, disconnected: !isZipkinConsumerConnected }">
      <md-field>
        <label>API Url</label>
        <md-input v-model="zipkinApiUrl" placeholder="Set Base Url" @change="() => handleZipkinUrlUpdate(zipkinApiUrl)"></md-input>
      </md-field>
    </div>
    <div>
        <div class="watcher-name">Fetch Zipkin Updates :</div>
        <toggle-button @change="() => handleZipkinToggle()" v-model="zipkinConsumerIsActive"/>
    </div>
  </div>
</template>

<script>
import kubeApi from './api-kube-consumer'
import zipkinApi from './api-zipkin-consumer'

export default {
  name: 'Monitor',
  data () {
      return {
          namespace: null ,
          watchers: [],
          apiUrl: null,
          isKubeConsumerConnected: false,
          zipkinApiUrl: null,
          zipkinConsumerIsActive: false,
          isZipkinConsumerConnected: false,
      }
  },
  created () {
    // fetch the data when the view is created and the data is
    // already being observed
    this.fetchData()
  },
  methods: {
    async fetchData () {
      this.apiUrl = kubeApi.getBaseUrl()
      this.zipkinApiUrl = zipkinApi.getBaseUrl()
      this.zipkinConsumerIsActive = zipkinApi.getIsConsumerActive()


      try {
        this.namespace = await kubeApi.getNamespace()
        this.watchers = await kubeApi.getWatchers()
        this.isKubeConsumerConnected = true
      } catch (e) {
          console.error(e)
          this.isKubeConsumerConnected = false
      }

      try {
          zipkinApi.getDependencies()
          this.zipkinConsumerIsActive = true
      } catch (e) {
          console.error(e)
          this.isZipkinConsumerConnected = false
      }
    },
    handleToggle(watcher) {
      kubeApi.setWatcher(watcher.type, watcher.active)
    },
    handleZipkinToggle() {
      this.zipkinConsumerIsActive
        ? zipkinApi.startLoop()
        : zipkinApi.stopLoop()
    },
    handleNamespaceUpdate(namespace) {
      kubeApi.setNamespace(namespace)
    },
    handleUrlUpdate(url) {
      kubeApi.setBaseUrl(url)
      this.fetchData()
    },
    handleZipkinUrlUpdate(url) {
      zipkinApi.setBaseUrl(url)
      this.fetchData()
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
.global-container {
  max-width: 500px;
  margin: auto;
}
.property-container {
  display: inline-flex;
  width: 100%;
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

.disconnected {
  border-right: 5px solid darkred;
}

.connected {
  border-right: 5px solid darkgreen;
}
</style>
