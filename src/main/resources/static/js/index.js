$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function (e,xhr,options){
		xhr.setRequestHeader(header,token);
	});
	$.post(
		CONTEXTPATH+"/discussPost/add",
		{"title":title,"content":content},
		function (data) {
			console.log(typeof (data))
			data = $.parseJSON(data)
			console.log(typeof (data))
			$("#hintBody").text(data.msg)
			console.log(data.msg)
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