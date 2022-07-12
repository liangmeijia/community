$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	//
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function (e,xhr,options){
		xhr.setRequestHeader(header,token);
	});
	$.post(
		CONTEXTPATH+"/message/letter/send",
		{"toName":toName,"content":content},
		function (data) {
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);

			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if(data.code == 0){
					window.location.reload();
				}
			}, 2000);
		}
	)

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}