$(function (){
    $("#uploadForm").submit(upload);

});
function upload(){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e,xhr,options){
        xhr.setRequestHeader(header,token);
    });
    $.ajax({
        url:"http://upload-z2.qiniup.com",
        method: "post",
        processData: false,//不要将表单作为字符串上传
        contentType: false,//不让jqury设置上传文件内容的类型
        data: new FormData($("#uploadForm")[0]),
        success: function (data){
            if(data && data.code == 0){
                $.post(
                    CONTEXTPATH+"/user/header/url",
                    {
                        "filename":$("input[name='key']").val()
                    },
                    function (data) {
                        data = $.parseJSON(data);
                        if(data.code == 0){
                            window.location.reload();
                        }else{
                            alert(data.msg);
                        }
                    }
                )
            }else{
                alert("upload file to qiniu server unsuccessfully")
            }
        }
    });
    return false;
}