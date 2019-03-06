import * as Axios from 'axios'

let DEFAULT_API_URL = process.env.IS_PROD
    ? 'http://fb14srv7.hpi.uni-potsdam.de:1800/kube-consumer'
    : 'http://localhost:8002';

const ORIGIN = process.env.IS_PROD
    ? 'http://fb14srv7.hpi.uni-potsdam.de:1801'
    : 'http://localhost:8081';

const AXIOS_CONF = {
    baseURL: DEFAULT_API_URL,
    headers: {
        'Access-Control-Allow-Origin': ORIGIN,
        'Content-Type': 'application/json'
    }
}

let axios = Axios.create(AXIOS_CONF)

const handleApiError = response => {
    console.error(JSON.stringify(response))
    error({statusCode: response.status(), message: response.message()})
}

export default {
    getNamespace: async () => {
        let { data } = await axios.get('/api/namespace')

        return data.trim()
    },

    setNamespace: (namespace) => {
        console.info(`Setting namespace to ${namespace}`)
        axios.post('/api/namespace', namespace)
    },

    getWatchers: async () => {
        let { data } = await axios.get('/api/watchers')

        return data
    },

    setWatcher: (type, active) => {
        console.info(`Setting watcher ${type} to ${active}`)
        axios.post(`/api/watchers/${type}`, active ? "true" : "false")
    },

    getBaseUrl: () => AXIOS_CONF.baseURL,

    setBaseUrl: baseURL => {
        AXIOS_CONF.baseURL = baseURL
        axios = Axios.create(AXIOS_CONF)
    }
}
