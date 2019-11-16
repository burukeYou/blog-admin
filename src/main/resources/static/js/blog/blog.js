/*!
 * blogPage.html 页面脚本.
 *
 */

// DOM 加载完再执行
$(function() {

	getPraiseOfBlog();


});


//2-处理删除博客事件
$("#deleteBlog").click( function () {

	if (confirm("你也定要删除吗？")) {
		$.ajax({
			url:"/"+$("#username").val()+"/blogs/"+$("#blog_id").val(),
			async:true,      //是否异步还是同步
			type:"DELETE",       //请求方式
			success:function(data){       //
				if (data.code == 201){
					spop({
						style     : 'success',
						template: data.message,
						autoclose: 2000,
						onClose: function() {
							window.close();
						}
					});
				}else if(data.code == 400){
					spop({
						style     : 'error',
						template: data.message,
						autoclose: 3000
					});
				}


			},
			error:function(){
				spop({
					template: '请确保你已经登陆',
					autoclose: 2000
				});
			},
			dataType:"json"      //第4行返回字符串的类型(解析格式方式)
		});
	}
});


//异步加载博客点赞情况
function getPraiseOfBlog(){

       $.ajax({
           url:"/praise/1/"+$("#blog_id").val(),
           async:true,      //是否异步还是同步
           type:"GET",       //请求方式
           //data:{"blogId":},  //发送到服务器的参数，建议使用json格式
           success:function(data){       //
           		//{"code":null,"message":null,"data":{"id":4,"praiseCount":2,"praise":true}}
               //渲染点赞面板
			   var praiseVo = data.data;

			   if (praiseVo.praise == true){
				   $(".heart").addClass("heartAnimation").attr("rel","unlike");
				   $("#praiseId").val(praiseVo.id);

			   }else if (praiseVo.praise == false) {
				   $(".heart").removeClass("heartAnimation").attr("rel","like");
				   $(".heart").css("background-position","left");
				   $("#praiseId").val("");
			   }


			   $("#likeCount2").html(praiseVo.praiseCount);


           },
           error:function(){
              // alert("请登陆");
           },
           dataType:"json"      //第4行返回字符串的类型(解析格式方式)
       });

}






//点赞
$('body').on("click",'.heart',function(){
	var A=$(this).attr("id");
	var B=A.split("like");
	var messageID=B[1];
	var C=parseInt($("#likeCount"+messageID).html());
	$(this).css("background-position","")
	var D=$(this).attr("rel");

	//点赞
	if(D === 'like') {
		/*alert("点赞");*/
		$.ajax({
			url:"/praise/1/"+$("#blog_id").val(),
			async:true,      //是否异步还是同步
			type:"POST",       //请求方式
			success:function(data){       //
				if (data.code == 204){
					spop({
						style:'success',
						template: data.message,
						autoclose: 3000
					});
				}else if(data.code == 404){
					spop({
						style:'error',
						template: data.message,
						autoclose: 3000
					});
				}


				//刷新点赞面板
				getPraiseOfBlog();

			},
			error:function(){
				spop({
					template: '请确保你已经登陆',
					autoclose: 2000
				});
			},
			dataType:"json"      //第4行返回字符串的类型(解析格式方式)
		});


		/*$("#likeCount"+messageID).html(C+1);
		$(this).addClass("heartAnimation").attr("rel","unlike");*/
	}
	//取消点赞
	else{
		if ($("#praiseId").val() != null && $("#praiseId").val() != ""){
			$.ajax({
				url:"/praise/1/"+$("#praiseId").val()+"?entityId="+$("#blog_id").val(),
				async:true,      //是否异步还是同步
				type:"DELETE",       //请求方式
				success:function(data){       //
					//刷新点赞面板
					getPraiseOfBlog();

				},
				error:function(){
					alert("请先登陆");
				},
				dataType:"json"      //第4行返回字符串的类型(解析格式方式)
			});
		}


	/*	$("#likeCount"+messageID).html(C-1);*/
		/*$(this).removeClass("heartAnimation").attr("rel","like");
		$(this).css("background-position","left");*/
	}
});





