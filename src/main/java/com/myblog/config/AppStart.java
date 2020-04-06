package com.myblog.config;

import com.myblog.entity.Es.EsBlog;
import com.myblog.service.EsBlogService;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AppStart implements ApplicationListener<ApplicationStartedEvent> {

    private final EsBlogService esBlogService;

    public AppStart(EsBlogService esBlogService) {
        this.esBlogService = esBlogService;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        EsBlog build = EsBlog.builder().readSize(0).commentSize(0).praise_count(0)
                        .createTime(new Date()).avatar("").blog_img("").blogId(10L).user_id(6L).username("LiSi")
                        .content("测试").htmlContent("测试").title("测试").status(0).tags("测试").build();

        esBlogService.addBlog(build);
    }
}
