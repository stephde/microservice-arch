import * as Axios from 'axios'

let DEFAULT_API_URL = process.env.IS_DEV
    ? 'http://localhost:8002'
    : 'http://fb14srv7.hpi.uni-potsdam.de:1800/zipkin-consumer';

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
let consumerIsActive = false;

const handleApiError = response => {
    console.error(JSON.stringify(response))
    error({statusCode: response.status(), message: response.message()})
}

export default {
    startLoop: async () => {
        let { data } = await axios.post('/start')

        consumerIsActive = true;

        return data.trim()
    },
    stopLoop: async () => {
        let { data } = await axios.post('/stop')

        consumerIsActive = false;

        return data.trim()
    },

    getDependencies: async () => {
        let { data } = await axios.get('/dependencies')

        console.info(data)
    },

    getCollectorUrl: async () => {
        let { data } = await axios.get('/zipkinurl')

        return data
    },

    setCollectorUrl: async url => {
        axios.post('/zipkinurl', url)
    },

    getBaseUrl: () => AXIOS_CONF.baseURL,

    setBaseUrl: baseURL => {
        AXIOS_CONF.baseURL = baseURL
        axios = Axios.create(AXIOS_CONF)
    },

    getIsConsumerActive: () => consumerIsActive
}
