/**
 *     用户主页博客列表
 *
 *
 */

$(function () {



});

var user_id = $("#blogUser_id").val();
var username = $("#blogUsername").val();

function loadMyBlog() {
    //1-刷新博客分类
    getCategoryByUser();

    //2-加载博客列表
    getMyBlogByCondition();


}







//1-加载用户的所有个人分类
function getCategoryByUser() {
    $("#blog_category").empty();
    $("#blog_category").append("<option value=''>分类</option>");

    $.ajax({
        url:  "/categorys/"+user_id,
        type: 'GET',
        success: function(data){
            var content = "";

            for (var i = 0; i < data.length; i++) {
               $("#blog_category").append("<option value='"+data[i].id+"'>"+data[i].categoryName+"</option>")
            }


        },
        error : function() {
            alert("error")
        }
    });
}

//2-加载博客列表
function getMyBlogByCondition() {
    $("#blog_bloglist").empty();

   /*  Long user_id;
     String keyword;
     String order  = "new";    //默认最新查询
     Integer categoryId;
     int currentPage = 1;    //默认第一页
     int pageSize = 6;      //默认每页显示六个*/

    var order = $("#order").val();
    var currentPage = $("#currentPage").val();
    var keyword = $("#keyword").val();
    var categoryId =  $("#categoryId").val();

    $.ajax({
        url:"/home/"+username+"/blogs",
        async:true,
        type:"GET",
        data:{"order":order,"currentPage":currentPage,"keyword":keyword,"user_id":user_id,"categoryId":categoryId},
        success:function(data){
            //
            var blogList = data.dataList;
            for (var i = 0; i < blogList.length; i++) {
                $("#blog_bloglist").append("<div class='col-sm-6'>" +
                    "<div class='item'>" +
                    "<a href='#'>" +
                    "<div class='blog__img'>" +
                    "<i class='pe-7s-news-paper'></i>" +
                    "<img src='"+blogList[i].blog_img+"' style='height: 168px'  alt=''>" +
                    "</div>" +
                    "</a>" +
                    "<div class='content matchH' style='height: 92px;'>" +
                    "<p class='info'>"+blogList[i].createTime+"</p>" +
                    "<h3><a href='/"+username+"/blogs/"+blogList[i].id+"' target='_blank'>"+blogList[i].title+"</a>" +
                    "</h3></div></div></div>");

                //重新设置当前页，查找顺序，关键字
                 //$("#order").val();
                // $("#currentPage").val(data.currentPage);
                // $("#keyword").val();

            }


            //3-刷新分页条
            if(!$("#blog_Pagination > li ").length > 0) {
                flushPagination(data.totalPageCount,data.currentPage,data.totalDataCount);
            }else {
                $("#blog_Pagination").empty();
                flushPagination(data.totalPageCount,data.currentPage,data.totalDataCount);
            }

        },
        error:function(){
            alert("请求失败");
        },
        dataType:"json"
    });

}

//渲染分页面板
function flushPagination(totalPage,currentPage,totalCount){

    $("#blog_Pagination").append("总："+totalPage+"页&nbsp;&nbsp;"+totalCount+"篇博客<li><a href='javascript:;' " +
        "onclick='changePage("+(parseInt(currentPage)-1)+")'>" +
        "<span aria-hidden='true'>&larr;</span></a></li>" +
        "第:&nbsp;&nbsp;"+currentPage+"&nbsp;&nbsp; 页" +
        "<li><a href='javascript:;' onclick='changePage("+(parseInt(currentPage)+1)+")'><span aria-hidden='true'>&rarr;</span></a></li>");

    if (currentPage == "1") {
        $('#blog_Pagination li:eq(0) a').removeAttr('onclick');
        $("#blog_Pagination li:eq(0)").addClass("disabled")
    }


    if (totalPage == currentPage){
        $('#blog_Pagination li:eq(1) a').removeAttr('onclick');
        $("#blog_Pagination li:eq(1)").addClass("disabled")
    }



}



//当前页改变时
function changePage(newPage) {

    //2-重新加载博客列表
    if (!parseInt(newPage) <= 0){
        //1-重新修改当前页隐藏域
        $("#currentPage").val(newPage);

        getMyBlogByCondition();
    }


}
//分类改变时
function changeCategory(){
    $("#categoryId").val($("#blog_category").val());

    getMyBlogByCondition();
}



//搜索博客
function searchBlog(){
    //1-重新修改当前页隐藏域
    $("#currentPage").val(1);

    getMyBlogByCondition();
}

//切换最新
$(".newBtn a").click(function () {

    if (!$(".newBtn").hasClass("active")) {
        $(".hotBtn").removeClass("active");
        $(".newBtn").addClass("active");

        $("#order").val("new");
        $("#currentPage").val(1);

        getMyBlogByCondition();
    }

});

//切换最新
$(".hotBtn a").click(function () {

    if (!$(".hotBtn").hasClass("active")) {
        $(".newBtn").removeClass("active");
        $(".hotBtn").addClass("active");

        $("#order").val("hot");
        $("#currentPage").val(1);

        getMyBlogByCondition();

    }
});



