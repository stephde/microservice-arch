import * as Axios from 'axios'

let DEFAULT_API_URL = process.env.IS_DEV
    ? 'http://localhost:8002'
    : 'http://modelmaintainer.dm.svc.cluster.local';

const ORIGIN = process.env.IS_DEV
    ? 'http://localhost:8081'
    : 'http://fb14srv7.hpi.uni-potsdam.de:1801';

const AXIOS_CONF = {
    baseURL: DEFAULT_API_URL,
    headers: {
        'Access-Control-Allow-Origin': ORIGIN,
        'Content-Type': 'application/json'
    }
}

let axios = Axios.create(AXIOS_CONF)

export default {

  getModel: async () => {
    let { data } = await axios.get('/model')

    return data
  },

  convertModel: model => {

      const nodes = model.services.map(service => { return {
          key: service.name,
          text: service.name,
          color: 'lightblue'
      }})

      const links = []
      model.services.forEach(service => {
          service.instances.forEach(instance => {
              if(instance.requiredInterfaces) {
                  instance.requiredInterfaces.forEach(i => {
                      links.push({
                          from: service.name,
                          to: i.targetName
                      })
                  })
              }
          })
      })

      return {
          nodeDataArray: nodes,
          linkDataArray: links,
      }
  },

  getBaseUrl: () => AXIOS_CONF.baseURL,

  setBaseUrl: baseURL => {
    AXIOS_CONF.baseURL = baseURL
    axios = Axios.create(AXIOS_CONF)
  },
}
