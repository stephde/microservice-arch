import * as Axios from 'axios'

const axios = Axios.create({
    baseURL: 'http://localhost:8081',
    headers: {
        'Access-Control-Allow-Origin': 'http://localhost:8080'
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
    }
}
