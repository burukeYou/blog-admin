


$(function () {

    var order = $("#order").val();
    if (order == "hot"){
        $("#newL").attr("style","");
        $("#hotL").attr("style","color: #1353ff");
    }else if (order == "new"){
        $("#hotL").attr("style","");
        $("#newL").attr("style","color: #1353ff");
    }

    //搜索
    $("#searchBtn").click(function() {
        //1-重新设置当前页
        $("#currentPage").val(1);

        //2-提交表单
        $("#searchForm").submit();

    });

});


//页面改变时
function changePage(currentPage) {

    if (parseInt(currentPage) <= 0)
        return;

    //1-重新设置当前页
    $("#currentPage").val(currentPage);

    //2-提交表单
    $("#searchForm").submit();
}

//最新or最热
function changeOrder(order){
    //$("#show").removeAttr("style");
   // $("#show").attr("style","");

    //1-重新设置当前页
    $("#currentPage").val(1);

    //
    $("#order").val(order);

    //2-提交表单
    $("#searchForm").submit();
}

//根据标签查找博客
function findByTags(tag) {

    //1-重新设置当前页
    $("#currentPage").val(1);

    //2-设置关键字
    $("#searchbox").val(tag);

    //2-提交表单
    $("#searchForm").submit();
}




/*
function searchBlogByCondition() {
    //1-当前页
    var currentPage =  $("#currentPage").val();

    //2-最新or最热


    //3-关键字
    var keyword;
    if ($("#searchbox").val() != null || $("#searchbox").val() != '')
        keyword = $("#searchbox").val();
    else
        keyword = null;


    //
    $.ajax({
        url:"/blogs",
        async:true,      //是否异步还是同步
        type:"GET",       //请求方式
        data:{"currentPage":currentPage,"keyword":keyword,"order":"new"},  //发送到服务器的参数，建议使用json格式
        success:function(data){       //
        },
        error:function(){
            alert("请求失败");
        },
        dataType:"json"      //第4行返回字符串的类型(解析格式方式)
    });


}*/
