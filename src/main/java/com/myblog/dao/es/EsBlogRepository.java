package com.myblog.dao.es;

import com.myblog.entity.Es.EsBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsBlogRepository extends ElasticsearchRepository<EsBlog,String> {


    EsBlog findByBlogId(Long id);


    Page<EsBlog> findDistinctByTitleOrSummaryOrContentOrTags
            (String title, String Summary, String content, String tags, Pageable pageable);

}
