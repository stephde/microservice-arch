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
      <ul class="watcherList">
        <li v-for="watcher in watchers" class="watcherItem">
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
    <div class="watcherList">
      <div class="watcherItem">
        <div class="watcher-name">Fetch Zipkin Updates :</div>
        <toggle-button @change="() => handleZipkinToggle()" v-model="zipkinConsumerIsActive"/>
      </div>
    </div>

    <!--------------- ---------------->

    <h1>Workload Emulator</h1>
    <div class="property-container" v-bind:class="{ connected: isWorkloadEmulatorConnected, disconnected: !isWorkloadEmulatorConnected }">
      <md-field>
        <label>API Url</label>
        <md-input v-model="workloadApiUrl" placeholder="Set Base Url" @change="() => handleWorkloadUrlUpdate(workloadApiUrl)"></md-input>
      </md-field>
    </div>
    <div class="watcherList">
      <div class="watcherItem">
        <div class="watcher-name">Emulate Workload :</div>
        <toggle-button @change="() => handleWorkloadToggle()" v-model="isWorkloadRunning"/>
      </div>
      <div class="watcherItem">
        <div class="watcher-name">Services under load:</div>
        <div>{{ this.servicesUnderLoad }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import kubeApi from './api-kube-consumer'
import zipkinApi from './api-zipkin-consumer'
import workloadApi from './api-workload'

export default {
  name: 'Monitor',
  data () {
      return {
          namespace: null ,
          watchers: [],
          apiUrl: null,
          zipkinApiUrl: null,
          workloadApiUrl: null,
          isKubeConsumerConnected: false,
          isZipkinConsumerConnected: false,
          isWorkloadEmulatorConnected: false,
          zipkinConsumerIsActive: false,
          isWorkloadRunning: false,
          servicesUnderLoad: 'NONE',
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
      this.workloadApiUrl = workloadApi.getBaseUrl()
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
          await zipkinApi.getDependencies()
          this.isZipkinConsumerConnected = true
      } catch (e) {
          console.error(e)
          this.isZipkinConsumerConnected = false
      }

      try {
          this.servicesUnderLoad = await workloadApi.getServices();
          this.isWorkloadEmulatorConnected = true
      } catch (e) {
          console.error(e)
          this.isWorkloadEmulatorConnected = false
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
    handleWorkloadToggle() {
      this.isWorkloadRunning
        ? workloadApi.startLoop()
        : workloadApi.stopLoop()
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
    },
    handleWorkloadUrlUpdate(url) {
      workloadApi.setBaseUrl(url)
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
.watcherItem {
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
.watcherList {
  display: inline-grid
}
.vue-js-switch {
  padding-left: 20px;
}

.disconnected {
  border-right: 5px solid darkred;
}

.connected {
  border-right: 5px solid #4ac08e;
}
</style>
