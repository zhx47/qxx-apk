function XMLHttp() {
    function request(param) {
    };

    function response(param) {
    };
}

let http = new XMLHttp();

function initXMLHttpRequest() {
    let open = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function (...args) {
        let send = this.send;
        let _this = this;
        let post_data = [];
        this.send = function (...data) {
            post_data = data;

            if (data[0].indexOf('vaptcha_token=16') != -1 || data[0].indexOf('capToken=16') != -1) {
                window.$app.log(post_data[0]);
//             window.$app.log("哈哈哈哈啊哈哈");
                console.log(post_data[0]);
                data[0] = '123';
            } else {
                return send.apply(_this, data);
            }
        }
        http.request(args);
        this.addEventListener('readystatechange',
            function () {
                if (this.readyState === 4) {
                    let config = {
                        url: args[1],
                        status: this.status,
                        method: args[0],
                        data: post_data
                    }
                    http.response({
                        config,
                        response: this.response
                    });
                }
            },
            false);
        return open.apply(this, args);
    }
}

(function () {
    http.request = function (param) {
        console.log(param, "---request");
    };
    http.response = function (res) {
        console.log(res, "---response");
    }
    // 初始化 XMLHttpRequest
    initXMLHttpRequest();
})();