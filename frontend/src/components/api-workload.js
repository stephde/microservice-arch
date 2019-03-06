import * as Axios from 'axios'

let DEFAULT_API_URL = process.env.IS_DEV
    ? 'http://localhost:8003'
    : 'http://fb14srv7.hpi.uni-potsdam.de:1800/workload';

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
        let { data } = await axios.get('/start')

        consumerIsActive = true;
        console.info(data)
    },
    stopLoop: async () => {
        let { data } = await axios.get('/stop')

        consumerIsActive = false;
        console.info(data)
    },

    getServices: async () => {
        let { data } = await axios.get('/services')

        return data
    },

    getBaseUrl: () => AXIOS_CONF.baseURL,

    setBaseUrl: baseURL => {
        AXIOS_CONF.baseURL = baseURL
        axios = Axios.create(AXIOS_CONF)
    },

    getIsConsumerActive: () => consumerIsActive
}
