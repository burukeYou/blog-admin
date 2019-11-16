/*
服务器给浏览器提供服务建议开启CSRF，給后台提供服务可以不开启

在Security的默认拦截器里，默认会开启CSRF处理，判断请求是否携带了token，如果没有就拒绝访问。
并且，在请求为(GET|HEAD|TRACE|OPTIONS)时，则不会开启
对每个ajax请求都设置token会十分繁琐，因此可以参考在ajaxSetup中统一设置：

    $(function(){
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $.ajaxSetup({
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            }
        );
    });

    或者同步请求时：
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
*/


//document.write("<script  src='/MessagePopup/spop.js'></script>");



window.onbeforeunload= function(event) { return confirm("确定离开此页面吗？"); }

var uuid;

//1-页面加载时，初始化页面
$(function() {
    //初始化 md 编辑器
    $(function() {
         uuid = guid();

        $('.dowebok :input').labelauty();

        var editor = editormd("editor", {
            width: "100%",
            height: "700px",
            path : "/js/lib/",  //依赖位置

            delay:0,
            watch:false,
            lineNumbers:true,          //显示编辑器行号

            //图片上传设置
            imageUpload    : true,   //开启图片上传功能
            imageFormats   : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
            imageUploadURL : "/file/upload?blogToken="+uuid,

            //开启自动生成目录
            toc: true,
            tocm: true,
            tocContainer : "#custom-toc-container",   // Custom Table Of Contents Container Selector
            tocDropdown   : false


            /**设置主题颜色*/
          /*  editorTheme: "pastel-on-dark",
            theme: "gray",
            previewTheme: "dark"*/
        });



    });
});

//生成GUID算法
function guid() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}


//2-点击发布文章时：异步加载所有个人分类
$('#myModal').on('shown.bs.modal', function () {
    loadCategorylist();
});

function  loadCategorylist(){
    //1-校验
    //alert($('#tagsinputval').val())
    $(".dowebok").empty();

    var user_id = $("#user_id").val();  //

    //2-加载某个用户的个人分类
    $.ajax({
        url:  "/categorys/"+user_id+"",
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function(data){
            $(".dowebok").empty();
            for (var i = 0; i < data.length; i++) {
                $(".dowebok").append("<li style=''>" +
                    "<input type='radio' name='category' data-labelauty='"+data[i].categoryName+"' value='"+data[i].id+"' ></li>");
            }

            $('.dowebok :input').labelauty();
        },
        error : function() {
            spop({
                template: '加载个人分类失败',
                autoclose: 2000
            });
        }
    });
}




//3-提交发布博客
function publicBlog() {

    if (parseInt($('#summary').val().length) >= 95){
        spop({
            style: 'error',
            template: '摘要内容不能超过87个字',
            autoclose: 3000
        });

        return;
    }

    //1-校验
    //标签最多5个
    var arr = $('#tagsinputval').val().split(",");
    if (arr.length > 5){
        spop({
            style: 'error',
            template: '标签最多5个',
            autoclose: 3000
        });
        return;
    }

    //
    if ($('#title').val() == "" || $('#title').val() == ""){
        spop({
            style: 'error',
            template: '标题不能为空',
            autoclose: 3000
        });
        return;
    }


    if ($('#content').val() == "" || $('#content').val() == ""){
        spop({
            style: 'error',
            template: '博客内容不能为空',
            autoclose: 3000
        });
        return;
    }


    //2-
    $.ajax({
        url:"/"+$('#username').val()+"/blogs/edit",
        async:true,      //是否异步还是同步
        type:"POST",       //请求方式
        data:{
            "id":$('#blog_id').val(),
            "title":$('#title').val(),
            "summary":$('#summary').val(),
            "content":$("#content").val(),
            "tags":$('#tagsinputval').val(),
            "category_id":$(".dowebok li input[type='radio']:checked").val(),
            "blogToken":uuid
        },
        success:function(data){       //
            //关闭发布博客弹出层
            $('#myModal').modal('hide');

            if (data.code == "202"){
              spop('<h4 class="spop-title">Success</h4>'+data.message+'', 'success');
            }else if (data.code == "402"){
                spop('<strong>'+data.message+'</strong>', 'error');
            }

        },
      /*  error:function(){
            alert("保存失败");
        },*/
        dataType:"json"      //第4行返回字符串的类型(解析格式方式)
    });
}


//3-添加分类
$("#addCategoryBtn").click(function () {

    var res = prompt("请输入新的分类");
    if (res.trim() != "" && res != null) {
        //
        $.ajax({
            url:"/categorys",
            async:true,      //是否异步还是同步
            type:"POST",       //请求方式
            data:{"username":$('#username').val(),"categoryName":res},
            success:function(data){       //

                if (data.code == 403){
                    spop({
                        style: 'error',
                        template: data.message,
                        autoclose: 2000
                    });

                    return;
                }else if (data.code == 203){
                    spop({
                        style: 'success',
                        template: data.message,
                        autoclose: 2000
                    });
                }
                else if (data.code == 401){
                    spop({
                        style: 'error',
                        template: data.message,
                        autoclose: 2000
                    });
                }


               //重新加载分类列表
                loadCategorylist();
            },
              error:function(){
                  spop({
                      template: '操作异常，稍后再试',
                      autoclose: 2000
                  });
              },
            dataType:"json"
        });
    }
});



