package com.myblog.entity.Es;


import com.myblog.entity.Blog;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


import java.io.Serializable;
import java.util.Date;

/**
 *      ES文档对象
 *
 * 1- Document注解参数：
 *       类型        参数名          默认值             说明
         String   indexName          无       索引库的名称，建议以项目的名称命名,必须是小写字母
         String   type               ""       类型，建议以实体的名称命名
         short    shards             5        默认分区数
         short    replica            1        每个分区默认的备份数
         String   refreshInterval   "1s"      刷新间隔
         String   indexStoreType    "fs"      索引文件存储类型

    2-属性注解@Field
|           @Field默认是可以不加的，默认所有属性都会添加到ES中。
        参数：
            类型        参数名           默认值                     说明
            FileType	type	  FieldType.Auto	        自动检测属性的类型
            FileType	index	  FieldIndex.analyzed	    默认情况下分词
            boolean	    store	        false	            默认情况下不存储原文
            String	searchAnalyzer	       ""	            指定字段搜索时使用的分词器
            String	indexAnalyzer	      ""	            指定字段建立索引时指定的分词器
            String[]	ignoreFields	{}	                如果某个字段需要被忽略



 */


@Document(indexName = "myblog", type = "blog")
@Data
@ToString
public class EsBlog implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    private String id;    //ES中一条记录的主键

    //@Field(index = false)
    private Long blogId;        //bigint(20) NOT NULL AUTO_INCREMENT,

   // @Field(index = false)
    private Long user_id;        //bigint(20) NULL,

    private String username;   //对应nickname

   // @Field(index = false)
    private String avatar;        //用户头像

   // @Field(index = false)
    private Integer category_id;        //int(50) NULL,

    private String categoryName;

    private String title;        //varchar(50) NOT NULL,

    private String summary;        //varchar(200) NOT NULL,

    private String content;        //longtext NOT NULL,

    private String htmlContent;        //longtext NOT NULL,


    //@Field(index = false)
    @Field(type = FieldType.Date)
    private Date createTime;        //timestamp NOT NULL,


   // @Field(index = false)
    private volatile Integer readSize;        //int(50) NULL,


   // @Field(index = false)
    private volatile Integer commentSize;        //int(50) NULL,

   // @Field(index = false)
    private Integer praise_count;        //int(50) NULL,

    private String tags;        //varchar(255) NULL,

    //@Field(index = false)
    private Integer status;        //int(10) NULL,

    //@Field(index = false)
    private String blog_img;


    protected EsBlog() {  // JPA 的规范要求无参构造函数；设为 protected 防止直接使用
    }


    public EsBlog(Blog blog){
        this.blogId = blog.getId();
        this.user_id = blog.getUser_id();

        if (blog.getUser() != null){
         this.username = blog.getUser().getNickname();
         this.avatar = blog.getUser().getAvatar();
        }


        this.category_id = blog.getCategory_id();
        if (blog.getCategory() != null)
          this.categoryName = blog.getCategory().getCategoryName();

        this.title = blog.getTitle();
        this.summary = blog.getSummary();
        this.content = blog.getContent();
        this.htmlContent = blog.getHtmlContent();
        this.createTime = blog.getCreateTime();
        this.readSize = blog.getReadSize();
        this.commentSize = blog.getCommentSize();
        this.praise_count = blog.getPraise_count();
        this.tags = blog.getTags();
        this.status = blog.getStatus();
        this.blog_img = blog.getBlog_img();
    }



 //============================================================================================================
    public void setCommentSize(Integer commentSize) {
        if (commentSize < 0)
            commentSize = 0;
     this.commentSize = commentSize;
    }



   public void update(Blog blog) {
        this.blogId = blog.getId();
        this.user_id = blog.getUser_id();

        if (blog.getUser() != null){
         this.username = blog.getUser().getUsername();
         this.avatar = blog.getUser().getAvatar();
        }


        this.category_id = blog.getCategory_id();
        if (blog.getCategory() != null)
         this.categoryName = blog.getCategory().getCategoryName();

        this.title = blog.getTitle();
        this.summary = blog.getSummary();
        this.content = blog.getContent();
        this.htmlContent = blog.getHtmlContent();
        this.createTime = blog.getCreateTime();
        this.readSize = blog.getReadSize();
        this.commentSize = blog.getCommentSize();
        this.praise_count = blog.getPraise_count();
        this.tags = blog.getTags();
        this.status = blog.getStatus();
        this.blog_img = blog.getBlog_img();
   }
}
