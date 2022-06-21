function like(entity, entityType, entityId, entityUserId, postId){
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