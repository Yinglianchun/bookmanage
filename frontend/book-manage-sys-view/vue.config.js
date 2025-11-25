module.exports = {
    lintOnSave: false,
    devServer: {
        host: "localhost",
        port: 21091,
        https: false,
        proxy: "http://localhost:21090",
        overlay: {
            warning: false,
            errors: false
        },
    },
    chainWebpack: config => {
        // 在开发模式下禁用preload，避免警告
        if (process.env.NODE_ENV === 'development') {
            config.plugins.delete('preload');
            config.plugins.delete('prefetch');
        } else {
            // 生产模式下优化资源预加载策略
            config.plugin('preload').tap(options => {
                options[0] = {
                    rel: 'preload',
                    fileBlacklist: [/\.map$/, /hot-update\.js$/],
                    include: 'initial'
                };
                return options;
            });
        }
    }
}