<!--textarea高度自适应-->

var order = $("#order").val();
var totalCommentPage;


$(function () {
    //初始化流动文本框
    $('.content').flexText();

    //异步加载评论
     flushComment($("#blog_id").val(),"new");
});


    //渲染分页
    function renderPage(totalCount,currentPage,totalPage) {

        $("#totalDataCount").html(totalCount);



        $("#commentPage").empty();
        $("#commentPage").append("<ul class='pager'>\n" +
            "<li>总："+totalPage+"页</li>\n" +
            "<li><a href='javascript:;' onclick='changePageOfCom("+(parseInt(currentPage)-1)+")'><span aria-hidden='true'>&larr;</span></a></li>\n" +
            "<li>第"+currentPage+"页</li>\n" +
            "<li><a href='javascript:;' onclick='changePageOfCom("+(parseInt(currentPage)+1)+")'><span aria-hidden='true'>&rarr;</span></a></li>\n" +
            "</ul>");

        if (parseInt(currentPage) >= parseInt(totalPage)){
            $("#commentPage ul li:eq(3) a").removeAttr('onclick');
            $("#commentPage ul li:eq(3)").addClass("disabled");
        }

        if (parseInt(currentPage) == 1 || parseInt(totalPage) <= 1){
            $("#commentPage ul li:eq(1) a").removeAttr('onclick');
            $("#commentPage ul li:eq(1)").addClass("disabled");
        }

    }


    //页面改变时
    function changePageOfCom(newPage){
        if (!parseInt(newPage) <= 0){
            $("#currentPage").val(newPage);
            flushComment($("#blog_id").val(),"new");
        }else {
            $("#commentPage ul li:eq(1) a").removeAttr('onclick');
            $("#commentPage ul li:eq(1)").addClass("disabled");
        }

    }


    //查找最热评论
    function findHotComment(){
        $('.nav-pills li:eq(0)').removeClass("active");
        $('.nav-pills li:eq(1)').addClass("active")

        $("#currentPage").val(1);

        flushComment($("#blog_id").val(),"hot");
    }

    //查找最新评论
    function findNewComment(){
        $('.nav-pills li:eq(0)').addClass("active")
        $('.nav-pills li:eq(1)').removeClass("active");

        $("#currentPage").val(1);
        flushComment($("#blog_id").val(),"new");
    }

    //查找某一评论下所有回复
    function findReplyByCommentId(comment_id){
        $.ajax({
            url:"/replys/"+comment_id,
            type:"GET",
            async:true,
            success:function(data){       //
                console.log(data);

                //渲染回复
                var dataList= data.dataList;
                $("#"+comment_id).html(null);
                

                //====================================================================================
                for (var i = 0; i < dataList.length; i++) {
                    var like;
                    var type;
                    var id;
                    var commentId = dataList[i].comment_id;
                    if (dataList[i].praise == false){
                        like = "class = 'date-dz-z-click-red'";
                        type = 1;
                    }else if (dataList[i].praise == true) {
                        like = "class = 'date-dz-z-click-red red'";
                        type = 2;
                        id = dataList[i].currentPraiseId;
                    }

                    //判断此回复的拥有者是否是当前登陆用户
                    var remove = "<a href=javascript:;' onclick='deleteReplyById("+dataList[i].id+","+dataList[i].comment_id+")' >删除</a>"
                    if ($("#currentNickname").val() != dataList[i].replyToOtherName ){
                        remove = "";
                    }

                    var oAt = '回复<a href="#" class="atName">@' + dataList[i].repliedByOtherName + '</a> : ' + dataList[i].content;

                    var oHtml = '<div class="all-pl-con"><div class="pl-text hfpl-text clearfix">' +
                        '<a href="#" class="comment-size-name">'+dataList[i].replyToOtherName+' </a>' +
                        '<span class="my-pl-con"> ' + oAt + '</span></div>' +
                        '<div class="date-dz"> <span class="date-dz-left pull-left comment-time">' + dataList[i].createtime + '</span> ' +
                        '<div class="date-dz-right pull-right comment-pl-block"> ' +
                        '<a href="javascript:;" class="date-dz-pl pl-hf hf-con-block pull-left" value="'+dataList[i].comment_id+'">回复</a>'+
                         remove+
                        '<span class="pull-left date-dz-line">|</span>' +
                        ' <a href="javascript:;" class="date-dz-z pull-left" onclick="chanegeReplyPraise('+type+','+id+','+dataList[i].id+','+commentId+')">' +
                        '<i '+like+'></i>(<i class="z-num">'+ dataList[i].parse_count+'</i>)</a> </div> </div></div>';


                    $("#"+comment_id).prepend(oHtml);
                }
                //======================================================================================================
            },
            error:function(){
                spop({
                    template: '加载评论失败，请稍后再试',
                    autoclose: 2000
                });
            },
            dataType:"json"      //第4行返回字符串的类型(解析格式方式)
        });
    }




    <!--textarea限制字数-->
    function keyUP(t){
        var len = $(t).val().length;
        if(len > 139){
            $(t).val($(t).val().substring(0,140));
        }
    }

    <!--发表评论-->
    $('.commentAll').on('click','.plBtn',function(){
        //=====================向服务器提交评论==============================
        $.ajax({
            url:"/comments",
            async:true,      //是否异步还是同步
            type:"POST",       //请求方式
            // contentType: "application/json;charset=utf-8",     //后台不是接收单个字符串，而是一个实体类时就用它了。
            data:{
                "blog_id":$("#blog_id").val(),
                "content":$("#commentId").val()
            },
            success:function(data){       //
                if (data.code == 200){
                    spop({
                        style     : 'success', //
                        template: '评论成功',
                        autoclose: 2000
                    });
                }else if (data.code == 400){
                    spop({
                        style     : 'error',
                        template: '评论失败',
                        autoclose: 2000
                    });
                }


                //
                $("#commentId").val('');

                flushComment($("#blog_id").val(),"new");
            },
            error:function(){
                spop({
                    template: '请确保你已经登陆',
                    autoclose: 2000
                });
            },
            dataType:"json"      //第4行返回字符串的类型(解析格式方式)
        });


    });
//=================================================================================================================

    /**
     *          渲染评论列表
     *
     */
    function flushComment(blogId,key) {

        var currentPage = $("#currentPage").val();
        
        $.ajax({
            url:"/comments/"+blogId+"?currentPage="+currentPage+"&&order="+key+"",
            async:true,      //是否异步还是同步
            type:"GET",       //请求方式
            // contentType: "application/json;charset=utf-8",     //后台不是接收单个字符串，而是一个实体类时就用它了。
            success:function(data){       //
                //alert(data.message);

                var datalist = data.dataList;
                $('#showComment').html(null);

                for (var i = datalist.length-1; i >=0; i--) {
                    var like ;
                    var type ;
                    var id = null;
                    var remove = "<a href='javascript:;' value='"+datalist[i].id+"' class='removeBlock'>删除</a>";

                    //判断此评论的拥有者是否是当前登陆用户
                    if ($("#currentUserName").val() != datalist[i].user.username ){
                        remove = "";
                    }


                    if (datalist[i].currentUserIspraise == true){

                        like = "class = 'date-dz-z-click-red red'";
                        type = 2;
                        id = datalist[i].currentPraiseId;
                    }else{
                        like = "class = 'date-dz-z-click-red'";
                        type = 1;
                    }

                    //1-动态创建评论模块
                    oHtml = '<div class="comment-show-con clearfix row">' +
                        '<div class="comment-show-con-img pull-left">' +
                        '<img src="'+datalist[i].user.avatar+'" class="img-circle" width="50px" alt=""></div> ' +
                        '<div class="comment-show-con-list pull-left clearfix">' +
                        '<div class="pl-text clearfix"> <a href="#" class="comment-size-name">'+datalist[i].user.nickname+'</a> ' +
                        '<span class="my-pl-con">&nbsp;'+ datalist[i].content +'</span> </div> ' +
                        '<div class="date-dz"> <span class="date-dz-left pull-left comment-time">'+datalist[i].createTime+'</span> ' +
                        '<a class="date-dz-left pull-left comment-time" style="margin-left: 60px" onclick="findReplyByCommentId('+datalist[i].id+')">查看回复</a>'+
                        '<div class="date-dz-right pull-right comment-pl-block">' +
                        remove +
                        '<a href="javascript:;" class="date-dz-pl pl-hf hf-con-block pull-left" value="'+datalist[i].id+'">回复</a> ' +
                        '<span class="pull-left date-dz-line">|</span> ' +
                        '<a href="javascript:;" class="date-dz-z pull-left" onclick="chanegeCommentPraise('+type+','+id+','+datalist[i].id+')">' +
                        '<i '+like+' id="cPraise" style="height: 23px;background-position-y: -194px;"></i>(<i class="z-num">'+datalist[i].praise_count+'</i>)</a> </div> ' +
                        '</div><div class="hf-list-con" id="'+datalist[i].id+'"></div></div></div>';

                    //渲染点赞状态
                    // $("#cPraise").addClass(like);

                    $('#showComment').prepend(oHtml);
                    $(this).siblings('.flex-text-wrap').find('.comment-input').prop('value','').siblings('pre').find('span').text('');
                }

                //2-渲染分页列表∂
                renderPage(data.totalDataCount,data.currentPage,data.totalPageCount);

            },
            error:function(){
                spop({
                    template: '加载异常，请稍后再试',
                    autoclose: 2000
                });
            },
            dataType:"json"      //第4行返回字符串的类型(解析格式方式)
        });


    }



//=================================================================================================================


    <!--点击回复动态创建回复块-->
    $('.comment-show').on('click','.pl-hf',function(){

         var comment_id = $(this).attr("value");

        //获取回复人的名字
        var fhName = $(this).parents('.date-dz-right').parents('.date-dz').siblings('.pl-text').find('.comment-size-name').html();
        //回复@
        var fhN = '回复@'+fhName+": ";
        //var oInput = $(this).parents('.date-dz-right').parents('.date-dz').siblings('.hf-con');
        var fhHtml = '<div class="hf-con pull-left"> ' +
            '<textarea class="content comment-input hf-input" placeholder="'+fhN+'" onkeyup="keyUP(this)"></textarea> ' +
            '<a href="javascript:;" class="hf-pl" value="'+comment_id+'">评论</a></div>';

        //显示回复
        if($(this).is('.hf-con-block')){
            $(this).parents('.date-dz-right').parents('.date-dz').append(fhHtml);
            $(this).removeClass('hf-con-block');
            $('.content').flexText();
            $(this).parents('.date-dz-right').siblings('.hf-con').find('.pre').css('padding','6px 15px');
            //console.log($(this).parents('.date-dz-right').siblings('.hf-con').find('.pre'))
            //input框自动聚焦
            $(this).parents('.date-dz-right').siblings('.hf-con').find('.hf-input').val('').focus().val();
        }else {
            $(this).addClass('hf-con-block');
            $(this).parents('.date-dz-right').siblings('.hf-con').remove();
        }
    });




    $('.comment-show').on('click','.hf-pl',function(){
        //获取输入内容
        var oHfVal = $(this).siblings('.flex-text-wrap').find('.hf-input').val();
        console.log(oHfVal)
        var oHfName = $(this).parents('.hf-con').parents('.date-dz').siblings('.pl-text').find('.comment-size-name').html();
        var oAllVal = '回复@'+oHfName;


        var comment_id = $(this).attr("value");

        if(oHfVal.replace(/^ +| +$/g,'') == '' || oHfVal == oAllVal){

        }else {
            /*
                            提交回复
             */
            $.ajax({
                url:"/replys",
                async:true,      //是否异步还是同步
                type:"POST",       //请求方式
                contentType:"application/json;charset=utf-8",
                data: JSON.stringify({"comment_id":comment_id,"content":oHfVal,"repliedByOtherName":oHfName}) ,  //发送到服务器的参数，建议使用json格式
                success:function(data){       //

                    if (data.code == 200){
                        spop({
                            style:'success',
                            template: '评论成功',
                            autoclose: 2000
                        });
                    } else if (data.code == 400){
                        spop({
                            style:'success',
                            template: '评论失败',
                            autoclose: 2000
                        });
                    }

                    //
                    findReplyByCommentId(comment_id);

                    //清空回复评论框内容
                    //$(this).parents('.flex-text-wrap').find('.hf-input')
                    $(".hf-input").val('');

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

    <!--删除评论块-->
    $('.commentAll').on('click','.removeBlock',function(){

        var comment_id = $(this).attr("value");

        $.ajax({
            url:"/comments/"+comment_id,
            async:true,      //是否异步还是同步
            type:"DELETE",       //请求方式
            success:function(data){       //

                if (data.code == 200){
                    spop({
                        style     : 'success', //
                        template: '删除成功',
                        autoclose: 2000
                    });
                }else if (data.code == 400){
                    spop({
                        style     : 'error',
                        template: '删除失败',
                        autoclose: 2000
                    });
                }


                //2-判断是最新还是最热
                if ($("#newId").hasClass("active")) {
                    flushComment($("#blog_id").val(),"new");
                }else{
                    flushComment($("#blog_id").val(),"hot");
                }
            },
            error:function(){
                spop({
                    template: '请确保你已经登陆',
                    autoclose: 2000
                });
            },
            dataType:"json"
        });
    });



        //删除回复
        function deleteReplyById(reply_id,comment_id) {
            $.ajax({
                url:"/replys/"+reply_id,
                async:true,      //是否异步还是同步
                type:"DELETE",       //请求方式
                success:function(data){       //
                    if (data.code == 200){
                        spop({
                            style     : 'success', //
                            template: '删除成功',
                            autoclose: 2000
                        });
                    }else if (data.code == 400){
                        spop({
                            style     : 'error',
                            template: '删除失败',
                            autoclose: 2000
                        });
                    }

                    //刷新评论列表
                    findReplyByCommentId(comment_id);
                },
                error:function(){
                    spop({
                        template: '请确保你已经登陆',
                        autoclose: 2000
                    });
                },
                dataType:"json"
            });
        }


    //点赞评论
    function chanegeCommentPraise(type,id,commentId){
            //alert($("#currentUser").val());

         if ($("#currentUser").val() == "anonymousUser"){
             spop({
                 template: '请确保你已经登陆',
                 autoclose: 2000
             });
             return;
         }

            
        //发表点赞
        if (type == 1)     {
            $.ajax({
                url:"/praise/2/"+commentId,
                async:true,      //是否异步还是同步
                type:"POST",       //请求方式
                success:function(data){       //

                    if (data.code == 204){
                        spop({
                            style     : 'success', //
                            template: data.message,
                            autoclose: 2000
                        });
                    }else if (data.code == 404){
                        spop({
                            style     : 'error',
                            template:  data.message,
                            autoclose: 2000
                        });
                    }

                    //2-判断是最新还是最热
                    if ($("#newId").hasClass("active")) {
                        flushComment($("#blog_id").val(),"new");
                    }else{
                        flushComment($("#blog_id").val(),"hot");
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
        //取消点赞
        else if (type == 2){
            $.ajax({
                url:"/praise/2/"+id+"?entityId="+commentId,
                async:true,      //是否异步还是同步
                type:"DELETE",       //请求方式
                success:function(data){       //
                    //2-判断是最新还是最热
                    if ($("#newId").hasClass("active")) {
                        flushComment($("#blog_id").val(),"new");
                    }else{
                        flushComment($("#blog_id").val(),"hot");
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

    }

   
    //点赞回复
    function chanegeReplyPraise(type,id,replyId,commentId) {
        //发表点赞
        if (type == 1){
            $.ajax({
                url:"/praise/3/"+replyId,
                async:true,      //是否异步还是同步
                type:"POST",       //请求方式
                success:function(data){       //


                    if (data.code == 204){
                        spop({
                            style     : 'success', //
                            template: data.message,
                            autoclose: 2000
                        });
                    }else if (data.code == 404){
                        spop({
                            style     : 'error',
                            template:data.message,
                            autoclose: 2000
                        });
                    }

                    //刷新评论列表
                    findReplyByCommentId(commentId);
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
        //取消点赞
        else if(type == 2){
            $.ajax({
                url:"/praise/3/"+id+"?entityId="+replyId,
                async:true,      //是否异步还是同步
                type:"DELETE",       //请求方式
                success:function(data){       //

                    findReplyByCommentId(commentId);

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





    }

