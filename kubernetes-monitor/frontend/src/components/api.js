import * as Axios from 'axios'

const axios = Axios.create({
    baseURL: 'http://localhost:8081',
    headers: {
        'Access-Control-Allow-Origin': 'http://localhost:8080',
        // 'X-CSRF-TOKEN' : 'cross-site-forgery-token'
        'Content-Type': 'application/json'
    }
})

const handleApiError = response => {
    console.error(JSON.stringify(response))
    error({statusCode: response.status(), message: response.message()})
}

export default {
    getNamespace: async () => {
        let { data } = await axios.get('/api/namespace')

        return data
    },

    getWatchers: async () => {
        let { data } = await axios.get('/api/watchers')

        return data
    },

    setWatcher: (type, active) => {
        axios.post(`/api/watchers/${type}`, active ? "true" : "false")
    }
}
