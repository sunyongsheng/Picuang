var adminOnly;

$(function () {
    $("[data-toggle='popover']").popover();
});

function showConfig() {
    setTimeout(function () {
        // 一列
        axios.get('/api/admin/getConf?conf=imageUploadedCount')
            .then(function (response) {
                $("#imageUploadedCount-input").val(response.data)
            });
        axios.get('/api/admin/getConf?conf=password')
            .then(function (response) {
                $("#password-input").val(response.data)
            });
        axios.get('/api/admin/getConf?conf=cloneLimit')
            .then(function (response) {
                $("#cloneLimit-input").val(response.data)
            });
        axios.get('/api/admin/getConf?conf=uploadLimit')
            .then(function (response) {
                $("#uploadLimit-input").val(response.data)
            });
        axios.get('/api/admin/getConf?conf=savePath')
            .then(function (response) {
                $("#savePath-input").val(response.data)
            });
        axios.get('/api/admin/getConf?conf=imgNameStrategy')
            .then(function (response) {
                $("#imgNameStrategy-input").val(response.data)
            });
        axios.get('/api/admin/getConf?conf=defaultSaveDir')
            .then(function (response) {
                $("#defaultSaveDir-input").val(response.data)
            })
        // 二列
        axios.get('/api/admin/getConf?conf=adminOnly')
            .then(function (response) {
                if (response.data === "on") {
                    adminOnly = "启用";
                } else if (response.data === "off") {
                    adminOnly = "停用";
                } else {
                    adminOnly = "未知";
                }
                $("#adminOnlyStatus").html(adminOnly);
            });
        $("#logout").show(200);
        $("#config").show(500);
    }, 250);
}

function getHelp(key) {
    if (key === "imageUploadedCount") {
        tip("存储图片数量是在主页展示的\"为用户累计永久存储图\"的数值。<br>" +
            "修改本值不会影响图片的存储，用户每上传一张图片该数值就会+1。");
    } else if (key === "uploadLimit") {
        tip("设置上传的频率限制。<br>" +
            "冒号左侧代表\"时间\"，右侧代表\"次数\"。<br>" +
            "例如\"3:1\"代表\"每三秒允许上传一张图片\"。<br>" +
            "设置过小的数值会致使上传速度变慢。<br>" +
            "上传多张图片时达到限制会自动阻塞，不会影响正常上传。");
    } else if (key === "cloneLimit") {
        tip("设置克隆的频率限制。<br>" +
            "冒号左侧代表\"时间\"，右侧代表\"次数\"。<br>" +
            "例如\"3:1\"代表\"每三秒允许克隆一张图片\"。<br>" +
            "设置过小的数值会致使克隆速度变慢。<br>");
    } else if (key === "savePath") {
        tip("设置图片保存路径。<br>" +
            "留空代表使用服务所在路径下的static/uploadImages/<br>");
    } else if (key === "imgNameStrategy") {
        tip("设置图片的URL地址<br>" +
            "0  代表使用UUID为图片生成名称。<br>" +
            "1  代表使用图片原本的名称<br>");
    } else if (key === "defaultSaveDir") {
        tip("设置图片默认存储目录<br>" +
            "根目录使用 / 表示<br>" +
            "支持二级目录如 post/2020<br>");
    }
}

function tip(text) {
    $("#helpText").html(text);
    $('#helpModal').modal();
}

function adminOnlyToggle() {
    if (adminOnly === "启用") {
        axios.get('/api/admin/setConf?conf=adminOnly&value=off')
            .then(function (response) {
                    adminOnly = "停用";
                    $("#adminOnlyStatus").html(adminOnly);
                    sendNotify("已停用：仅管理员上传模式。");
                }
            );
    } else if (adminOnly === "停用") {
        axios.get('/api/admin/setConf?conf=adminOnly&value=on')
            .then(function (response) {
                    adminOnly = "启用";
                    $("#adminOnlyStatus").html(adminOnly);
                    sendNotify("已启用：仅管理员上传模式。");
                }
            );
    } else {
        sendNotify("无法读取管理员上传模式。请检查配置文件是否有adminOnly项，且值为on或off，修改后点击下方\"重载\"按钮后重试。");
    }
}

function editConfig(sheet) {
    var value = $("#" + sheet + "-input").val();
    axios.get('/api/admin/setConf?conf=' + sheet + '&value=' + value)
        .then(function (response) {
                sendInnerNotify(sheet + " 的值已成功修改为：" + value);
            }
        );
}

function exportConfig() {
    window.open("/api/admin/export");
}

function importConfig() {
    var file = document.getElementById("import").files[0];
    uploadConfigToServer(file);
}

function uploadConfigToServer(file) {
    var param = new FormData();
    param.append('file', file);
    var config = {
        headers: {'Content-Type': 'multipart/form-data'}
    };
    axios.post('/api/admin/import', param, config)
        .then(function (response) {
                if (response.data.code === 200) {
                    sendNotify("配置导入成功！配置现已重载并生效。请刷新页面！");
                } else {
                    sendNotify("配置导入失败！请检查你的配置文件后缀名是否是.ini，且确认其可用性。")
                }
            }
        );
}

function reloadServer() {
    axios.get('/api/admin/reload')
        .then(function (response) {
                sendNotify("配置已重载！请刷新页面。");
            }
        );
}

verifyReNew = 0;
function reNewConfig() {
    if (verifyReNew === 0) {
        tip("您确定要生成新的配置文件吗？在生成新的配置文件之前，你应该将旧的配置文件导出并备份！<br>关闭本窗口后，再点一次\"重新生成配置文件\"按钮确定生成。");
        verifyReNew = 1;
    } else {
        axios.get('/api/admin/renew')
            .then(function (response) {
                    sendNotify("已重新生成配置文件！请刷新页面。");
                }
            );
        verifyReNew = 0;
    }
}