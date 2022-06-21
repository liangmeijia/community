$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

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