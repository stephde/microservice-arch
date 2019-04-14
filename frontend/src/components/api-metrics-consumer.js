import * as Axios from 'axios'

let DEFAULT_API_URL = process.env.IS_DEV
    ? 'http://localhost:8002'
    : 'http://fb14srv7.hpi.uni-potsdam.de:1800/aspect';

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

    getMetricsUrl: async () => {
        let { data } = await axios.get('/serviceurl')

        return data
    },

    setMetricsUrl: async url => {
        axios.post('/serviceurl', url)
    },

    getInterval: async () => {
        let { data } = await axios.get('/interval')

        return data
    },

    setInterval: async interval => {
        axios.post('/interval', interval)
    },

    getBaseUrl: () => AXIOS_CONF.baseURL,

    setBaseUrl: baseURL => {
        AXIOS_CONF.baseURL = baseURL
        axios = Axios.create(AXIOS_CONF)
    },

    getIsConsumerActive: () => consumerIsActive
}
