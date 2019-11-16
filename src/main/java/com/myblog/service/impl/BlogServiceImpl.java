package com.myblog.service.impl;

import com.myblog.dao.BlogMapper;
import com.myblog.dao.BlogRepository;
import com.myblog.entity.Blog;
import com.myblog.entity.BlogFile;
import com.myblog.entity.Es.EsBlog;
import com.myblog.service.BlogService;
import com.myblog.service.EsBlogService;
import com.myblog.util.AliyunOssClientUtil;
import com.myblog.vo.PageBean;
import com.myblog.vo.QueryConditionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private EsBlogService esBlogService;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private RedisTemplate redisTemplate;



    @Transactional
    @Override
    public void saveBlog(Blog blog) {
        blogMapper.insertBlog(blog);

        EsBlog esBlog = new EsBlog(blog);
        esBlog.setId(UUID.randomUUID().toString());

        //同时写到ES中
        esBlogService.addBlog(esBlog);

    }

    @Override
    public Blog getBlogById(Long id) {
        return blogMapper.getBlogById(id);
    }

    @Transactional
    @Override
    @Async
    public void readSizeIncrease(Long id) {
        blogMapper.readSizeIncrease(id);

        //同时更新es
        EsBlog esblog = esBlogService.getEsBlogByBlogId(id);
        esblog.setReadSize(esblog.getReadSize()+1);       //--并发问题
        esBlogService.addBlog(esblog);
    }

    @Transactional
    @Override
    public void deleteBlogById(Long id) {

        //1-先把博客上传的图片删除
        List<BlogFile> files = blogRepository.findAllByBlogId(id);
        if (files != null && files.size() > 0){
            List<String> urls = new ArrayList<>();
            for (BlogFile file : files) {
                urls.add(file.getUrl());
            }

            AliyunOssClientUtil.deleteFileList(urls);
            blogRepository.deleteAllByBlogId(id);
        }


        //2-
        blogMapper.deleteBlogById(id);

        //3-同时去es删除
        EsBlog esblog = esBlogService.getEsBlogByBlogId(id);
        esBlogService.deleteEsBlog(esblog);
    }

    @Transactional
    @Override
    public void updateBlog(Blog blog) {
        blogMapper.updateBlog(blog);

        //获得修改后的博客
        blog = blogMapper.getBlogById(blog.getId());

        EsBlog esBlog  = esBlogService.getEsBlogByBlogId(blog.getId());
        esBlog.update(blog);

        esBlogService.addBlog(esBlog);
    }

    @Override
    public PageBean<Blog> findBlogByCondition(QueryConditionVo queryVo) {
       //1-查找分页总个数
        int totalCount = blogMapper.findBlogCountByCondition(queryVo);
        int currentPage = queryVo.getCurrentPage();

        //2-查询博客列表
        List<Blog> blogList = null;
        queryVo.setCurrentPage((currentPage-1)*queryVo.getPageSize());  //重新设置起始行
        blogList =   blogMapper.findBlogByCondition(queryVo);


        //3-封装PageBean
        PageBean<Blog> pageBean = new PageBean(currentPage,totalCount,queryVo.getPageSize());
        pageBean.setDataList(blogList);

        return pageBean;
    }

    @Override
    public void commentSizeIncrease(Long blog_id) {
        blogMapper.commentSizeIncrease(blog_id);
    }

    @Override
    public void commentSizeReduce(Long blog_id) {
        blogMapper.commentSizeReduce(blog_id);
    }

    @Override
    public void praiseSizeIncrease(Long blogId) {

        blogMapper.praiseSizeIncrease(blogId);
    }

    @Override
    public void praiseSizeReduce(Long blogId) {
        blogMapper.praiseSizeReduce(blogId);
    }

    @Override
    public void insertAllBlogFile(List<BlogFile> blogFiles) {
         blogRepository.saveAll(blogFiles);
    }

    @Override
    public Integer findBlogCountByUser(Long id) {
        return blogMapper.findBlogCountByUser(id) != null? blogMapper.findBlogCountByUser(id).intValue():0;
    }

    @Override
    public Integer findUserAccessCount(Long id) {
        return blogMapper.findUserAccessCount(id) != null ? blogMapper.findUserAccessCount(id).intValue():0;
    }

    @Override
    public Integer findUserLikeCount(Long id) {
        return blogMapper.findUserLikeCount(id) != null ? blogMapper.findUserLikeCount(id).intValue() : 0;
    }

    @Override
    public Integer findUserCommentCount(Long id) {
        return blogMapper.findUserCommentCount(id) != null ?  blogMapper.findUserCommentCount(id).intValue():0;
    }


}
