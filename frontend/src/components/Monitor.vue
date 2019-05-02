<template>
  <div class="global-container">
    <h1>Kubernetes Sensor</h1>
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
      <ul class="item-list">
        <li v-for="watcher in watchers" class="toggleItem">
          <div class="item-name">{{ watcher.type}} :</div>
          <toggle-button @change="() => handleToggle(watcher)" v-model="watcher.active"/>
        </li>
      </ul>
    </div>

    <!--------------- ---------------->

    <h1>Zipkin Sensor</h1>
    <div class="property-container" v-bind:class="{ connected: isZipkinConsumerConnected, disconnected: !isZipkinConsumerConnected }">
      <md-field>
        <label>Sensor API Url</label>
        <md-input v-model="zipkinApiUrl" placeholder="Set Base Url" @change="() => handleZipkinUrlUpdate(zipkinApiUrl)"></md-input>
      </md-field>
    </div>
    <div class="property-container">
      <md-field>
        <label>Zipkin Collector Service Url</label>
        <md-input v-model="zipkinCollectorUrl" placeholder="Set Base Url" @change="() => handleZipkinCollectorUrlUpdate(zipkinCollectorUrl)"></md-input>
      </md-field>
    </div>
    <div class="property-container">
      <md-field>
        <label>Zipkin Fetch Interval in ms</label>
        <md-input type="number" v-model.number="zipkinInterval" placeholder="Set Fetch Interval" @change="() => handleZipkinIntervalUpdate(zipkinInterval)"></md-input>
      </md-field>
    </div>
    <div class="item-list">
      <div class="toggleItem">
        <div class="item-name">Fetch Zipkin Updates :</div>
        <toggle-button @change="() => handleZipkinToggle()" v-model="zipkinConsumerIsActive"/>
      </div>
    </div>

    <!--------------- ---------------->

    <h1>Metrics Sensor</h1>
    <div class="property-container" v-bind:class="{ connected: isMetricsConsumerConnected, disconnected: !isMetricsConsumerConnected }">
      <md-field>
        <label>Metrics Sensor API Url</label>
        <md-input v-model="metricsApiUrl" placeholder="Set Base Url" @change="() => handleMetricsApiUrlUpdate(metricsApiUrl)"></md-input>
      </md-field>
    </div>
    <div class="property-container">
      <md-field>
        <label>Metrics Service Url</label>
        <md-input v-model="metricsUrl" placeholder="Set Fetch Interval" @change="() => handleMetricsUrlUpdate(metricsUrl)"></md-input>
      </md-field>
    </div>
    <div class="property-container">
      <md-field>
        <label>Metrics Fetch Interval in ms</label>
        <md-input type="number" v-model.number="metricsInterval" placeholder="Set Fetch Interval" @change="() => handleMetricsIntervalUpdate(metricsInterval)"></md-input>
      </md-field>
    </div>
    <div class="item-list">
      <div class="toggleItem">
        <div class="item-name">Fetch Metrics Updates :</div>
        <toggle-button @change="() => handleMetricsToggle()" v-model="metricsConsumerIsActive"/>
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
    <div class="item-list">
      <div class="toggleItem">
        <div class="item-name">Emulate Workload :</div>
        <toggle-button @change="() => handleWorkloadToggle()" v-model="isWorkloadRunning"/>
      </div>
      <div class="toggleItem">
        <div class="item-name">Services under load:</div>
        <div>{{ this.servicesUnderLoad }}</div>
      </div>
    </div>

    <ul class="item-list">
      <li v-for="service in activeServices" class="toggleItem">
        <div class="item-name">{{ service.name}} :</div>
        <toggle-button @change="() => handleServiceWorkloadToggle(service)" v-model="service.active"/>
      </li>
    </ul>

    <!----------------- --------------->

    <h1>Model</h1>
    <div class="property-container">
      <md-field>
        <label>API Url</label>
        <md-input v-model="modelApiUrl" placeholder="Set Model Url" @change="() => handleModelUrlUpdate(modelApiUrl)"></md-input>
      </md-field>
    </div>
    <md-button @click="getModel" class="md-accent md-raised">Reload Model</md-button>
    <div>
      <tree-view :data="model" :options="{maxDepth: 4, rootObjectKey: 'model'}"></tree-view>
    </div>

  </div>
</template>

<script>
import kubeApi from './api-kube-consumer'
import zipkinApi from './api-zipkin-consumer'
import workloadApi from './api-workload'
import metricsApi from './api-metrics-consumer'
import modelApi from './api-model'

export default {
  name: 'Monitor',
  data () {
      return {
          namespace: null ,
          watchers: [],
          apiUrl: null,
          zipkinApiUrl: null,
          zipkinCollectorUrl: null,
          zipkinInterval: null,
          metricsApiUrl: null,
          metricsUrl: null,
          metricsInterval: null,
          workloadApiUrl: null,
          isKubeConsumerConnected: false,
          isZipkinConsumerConnected: false,
          isMetricsConsumerConnected: false,
          isWorkloadEmulatorConnected: false,
          zipkinConsumerIsActive: false,
          metricsConsumerIsActive: false,
          isWorkloadRunning: false,
          servicesUnderLoad: 'NONE',
          activeServices: [],
          model: { EMPTY: true},
          modelApiUrl: null,
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
      this.metricsApiUrl = metricsApi.getBaseUrl()
      this.metricsUrl = metricsApi.getMetricsUrl()
      this.metricsConsumerIsActive.getIsConsumerActive()
      this.workloadApiUrl = workloadApi.getBaseUrl()
      this.modelApiUrl = modelApi.getBaseUrl()

      try {
        this.namespace = await kubeApi.getNamespace()
        this.watchers = await kubeApi.getWatchers()
        this.isKubeConsumerConnected = true
      } catch (e) {
          console.error(e)
          this.isKubeConsumerConnected = false
      }

      try {
          this.zipkinCollectorUrl = await zipkinApi.getCollectorUrl()
          this.zipkinInterval = await zipkinApi.getInterval()
          this.isZipkinConsumerConnected = true
      } catch (e) {
          console.error(e)
          this.isZipkinConsumerConnected = false
      }

      try {
          this.metricsUrl = await metricsApi.getMetricsUrl()
          this.metricsInterval = await metricsApi.getInterval()
          this.isMetricsConsumerConnected = true
      } catch (e) {
          console.error(e)
          this.isMetricsConsumerConnected = false
      }

      try {
          this.servicesUnderLoad = await workloadApi.getServices();
          this.activeServices = await workloadApi.getActiveServices();
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
    handleMetricsToggle() {
      this.metricsConsumerIsActive
        ? metricsApi.startLoop()
        : metricsApi.stopLoop()
    },
    handleWorkloadToggle() {
      this.isWorkloadRunning
        ? workloadApi.startLoop()
        : workloadApi.stopLoop()
    },
    handleServiceWorkloadToggle(service) {
      workloadApi.setServiceActive(service.name, service.active)
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
    handleMetricsApiUrlUpdate(url) {
      metricsApi.setBaseUrl(url)
      this.fetchData()
    },
    handleZipkinCollectorUrlUpdate(url) {
      zipkinApi.setCollectorUrl(url)
    },
    handleZipkinIntervalUpdate(interval) {
      zipkinApi.setInterval(interval)
    },
    handleMetricsUrlUpdate(url) {
      metricsApi.setMetricsUrl(url)
    },
    handleMetricsIntervalUpdate(interval) {
      metricsApi.setInterval(interval)
    },
    handleWorkloadUrlUpdate(url) {
      workloadApi.setBaseUrl(url)
      this.fetchData()
    },
    getModel() {
      this.model = modelApi.getModel()
    },
    handleModelUrlUpdate(url) {
      modelApi.setBaseUrl(url)
    },
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
.toggleItem {
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
  margin-bottom: 6em;
}
.property-container {
  display: inline-flex;
  width: 100%;
}
.item-name {
  flex-grow: 1;
}
.item-list {
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
