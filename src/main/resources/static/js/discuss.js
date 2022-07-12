$(function (){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#delBtn").click(setDel);
});
function setTop(){
    var url = CONTEXTPATH+"/discussPost/top";
    var id = $("#postId").val();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e,xhr,options){
        xhr.setRequestHeader(header,token);
    });
    $.post(
        url,
        {
            "discussPostId":id
        },
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                $("#topBtn").text(data.type==1?'取消置顶':'置顶');

            }else {
                alert(data.msg);
            }
        }
    )
}
function setWonderful(){
    var url = CONTEXTPATH+"/discussPost/wonderful";
    var id = $("#postId").val();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e,xhr,options){
        xhr.setRequestHeader(header,token);
    });
    $.post(
        url,
        {
            "discussPostId":id
        },
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                $("#wonderfulBtn").text(data.status==1?'取消加精':'加精');
            }else {
                alert(data.msg);
            }
        }
    )
}
function setDel(){
    var url = CONTEXTPATH+"/discussPost/del";
    var id = $("#postId").val();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e,xhr,options){
        xhr.setRequestHeader(header,token);
    });
    $.post(
        url,
        {
            "discussPostId":id
        },
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0){
                location.href = CONTEXTPATH+"/index";
            }else {
                alert(data.msg);
            }
        }
    )
}
function like(entity, entityType, entityId, entityUserId, postId){
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e,xhr,options){
        xhr.setRequestHeader(header,token);
    });
    $.post(
        CONTEXTPATH+"/like",
        {"entityType":entityType, "entityId":entityId, "targetId":entityUserId, "postId":postId},
        function (data){
            data = $.parseJSON(data);
            if(data.code == 0){
                $(entity).children("b").text(data.likeStatus == 1?"已赞":"赞");
                $(entity).children("i").text(data.likeCount);
            }else{
                alert(data.msg);
            }

        }
    )
}