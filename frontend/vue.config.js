// https://github.com/vuejs-templates/webpack/issues/546

module.exports = {
    // baseUrl: '/kube-ui',
    // assetsPublicPath: '/kube-ui',
    devServer: {
        // disable host check, so that requests redirected by nginx proxy are accepted
        disableHostCheck: true,
    }
};