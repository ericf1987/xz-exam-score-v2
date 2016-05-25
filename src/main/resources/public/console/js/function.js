$(document).ready(function() {
    var funcName = $.urlParam('name');

    initTitle(funcName);
    initFunctionInfo(funcName);
    callBackFunction(funcName, "");

    function callBackFunction(funcName, p) {
        $.ajax({
            url: '/api/' + funcName + "?p=" + p,
            dataType: "json",
            timeout: 30000,

            success: function(res){
                var msg = res.message;
                var html = "";

                var result = JSON.stringify(res, undefined, 4)
                if (result) {
                    result = escapeHtml(result)

                    html += "<strong>调用结果</strong><br/>" + result + "<br/>";
                    if (msg && msg.length > 100) {
                        html += "<br/>" + escapeHtml(msg);
                    }

                    $("#call-result").html(html)
                }
            },
        });
    }

    function initFunctionInfo (funcName) {
        $.ajax({
            url: '/apiconsole/functionInfo/' + funcName,
            dataType: "json",
            timeout: 30000,

            success: function(res){
                if (res.success){
                    var funcInfo = res.data.functionInfo;
                    if (funcInfo) {
                        $("#func-name").html("<code><strong>" + funcInfo.functionName + "</strong></code>");
                        $("#func-desc").html(funcInfo.functionDesc);
                        $("#func-params").html(parseParams(funcInfo.params));
                        $("#func-result").html(parseResults(funcInfo.result))
                    }
                }
            }
        });
    }

    function parseParams (params) {
        var html = "";

        if (params && params.length) {
            for(var i=0; i<params.length; i++) {
                var obj = params[i];
                var className = obj.required ? "required" : "optional";
                var optionalStr = obj.required ? "" : "，可选";
                var defaultValueStr = obj.defaultValue ? ("，缺省值 " + obj.defaultValue) : "";

                html += "<div class='param " + className + "'>" +
                        "<span class='code'>" +
                        "<span class='codename'>" + obj.name + "</span> : " +
                        "<span class='type'>" + obj.type + "</span>" +
                        "</span> - " + obj.description + optionalStr + defaultValueStr + "&nbsp;" +
                        "<input type='text' name='" + obj.name +"'/></div>";
            }

            html += "<div class='param submit'><input id='submit-button' type='button' value='提交'/></div>";
        }

        return html;
    }

    function parseResults (result) {
        var html = "";

        if (result) {
            html += "<div class='label'>返回值属性</div>" +
                    "<div class='property return_value_title'>通用属性：</div>" +
                    "<div class='property'><span class='code'><span class='codename'>success</span> : " +
                    "<span class='type'>Boolean</span></span> - " + result.success + "</div>" +
                    "<div class='property'><span class='code'><span class='codename'>message</span> : " +
                    "<span class='type'>String</span></span> - 如果 success 为 false，则表示错误信息</div>" +
                    "<div class='property'><span class='code'><span class='codename'>resultCode</span> : " +
                    "<span class='type'>Integer</span></span> - 0 表示成功，其他值表示失败</div>" +
                    "<div class='property return_value_title'>自定义属性：</div>";

            var properties = result.properties;
            if (properties.length) {
                for(var i=0; i<properties.length; i++) {
                    html += getPropertyHtml(properties[i]);
                }
            }

            var listProperties = result.listProperties;
            if (listProperties.length) {
                for(var i=0; i<listProperties.length; i++) {
                    var listPropertie = listProperties[i];

                    var propertiesHtml = "";
                    var properties = listPropertie.properties;
                    if (properties && properties.length) {
                        for(var i=0; i<properties.length; i++) {
                            propertiesHtml += getPropertyHtml(properties[i]);
                        }
                    }

                    html += "<div class='listproperty'><div class='property' style='margin-left:0'>" +
                            "<span class='code'><span class='codename'>" + listPropertie.name + "</span> :" +
                            "<span class='type'>List&lt;Map&gt;</span></span> - " + listPropertie.description + "" +
                            "</div>" + propertiesHtml + "</div>"
                }
            }
        }

        return html;
    }

    function getPropertyHtml (property) {
        var html = "";

        if (property) {
            html += "<div class='property'><span class='code'><span class='codename'>" + property.name + "</span>" +
                    " : <span class='type'>" + property.type + "</span></span> - " + property.description + "</div>";
        }

        return html;
    }

    function escapeHtml(str) {
        if (str) {
            return str.replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/\n/g, "<br/>")
                .replace(/\t/g, "    ")
                .replace(/ /g, "&nbsp;");
        }

        return "";
    }

    function initTitle (funcName) {
        $("#function-title").html("<a href='/console/index.html'>接口列表</a> &gt; " + funcName);
        $("title").html("接口 " + funcName);
        $("#func-params").attr("func", funcName);
    }

    $("#submit-button").live("click", function(){
        var func = $("#func-params").attr("func");
        var params = "";

        $("#func-params input[type='text']").each(function(){
            var name=$(this).attr('name');
            var val=$(this).val();

            params += name + "=" + val + ";";
        });

        if (params.length > 1) {
            params = params.substring(0, params.length - 1);
            params = encodeURIComponent(params);
        }

        callBackFunction(func, params);
    })
});