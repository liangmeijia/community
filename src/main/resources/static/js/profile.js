$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	// if($(btn).hasClass("btn-info")) {
	// 	// 关注TA
	// 	//$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	// } else {
	// 	// 取消关注
	// 	$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
	// }
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function (e,xhr,options){
		xhr.setRequestHeader(header,token);
	});
	$.post(CONTEXTPATH+"/follow",
		{"entityType":3,"entityId":$(btn).prev().val()},
		function (data) {
			data = $.parseJSON(data);
			if(data.code == 0){
				window.location.reload();
			}else{
				alert(data.msg)
			}
		})
}