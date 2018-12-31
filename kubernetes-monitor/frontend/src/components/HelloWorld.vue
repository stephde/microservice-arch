<template>
  <div class="hello">
    <h1>Kubernetes Monitor</h1>
    <div>
      Monitored Namespace: <div class="bold">{{ namespace }}</div>
    </div>
  </div>
</template>

<script>
import api from './api'

export default {
  name: 'HelloWorld',
  data () {
      return { namespace: null }
  },
  created () {
    // fetch the data when the view is created and the data is
    // already being observed
    this.fetchData()
  },
  methods: {
    async fetchData () {
      this.namespace = await api.getNamespace()
      console.log(this.namespace)
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
</style>
