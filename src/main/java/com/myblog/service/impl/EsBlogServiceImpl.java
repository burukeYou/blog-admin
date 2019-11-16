package com.myblog.service.impl;

import com.myblog.dao.es.EsBlogRepository;
import com.myblog.entity.Es.EsBlog;
import com.myblog.entity.User;
import com.myblog.service.EsBlogService;
import com.myblog.service.UserService;
import com.myblog.vo.TagVo;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
        ElasticsearchTemplate中的名词概念

 term是代表完全匹配，也就是精确查询，搜索前不会再对搜索词进行分词，所以我们的搜索词必须是文档分词集合中的一个

 TermsBuilder:构造聚合函数

 AggregationBuilders:创建聚合函数工具类

 BoolQueryBuilder:拼装连接(查询)条件

 QueryBuilders:简单的静态工厂”导入静态”使用。主要作用是查询条件(关系),如区间\精确\多值等条件

 NativeSearchQueryBuilder:将连接条件和聚合函数等组合

 SearchQuery:生成查询

 elasticsearchTemplate.query:进行查询

 Aggregations:代表一组添加聚合函数统计后的数据

 Bucket:满足某个条件(聚合)的文档集合
--------------------------------------------------------------------------------------------------------
 搜索类型：
 1、query and fetch
     向索引的所有分片（shard）都发出查询请求，各分片返回的时候把元素文档（document）和计算后的排名信息一起返回。
     这种搜索方式是最快的。因为相比下面的几种搜索方式，这种查询方法只需要去shard查询一次。但是各个shard返回的结果的数量之和可能是用户要求的size的n倍。

 2、query then fetch（默认的搜索方式）
    如果你搜索时，没有指定搜索方式，就是使用的这种搜索方式。这种搜索方式，大概分两个步骤，
    第一步，先向所有的shard发出请求，各分片只返回排序和排名相关的信息（注意，不包括文档document)，
    然后按照各分片返回的分数进行重新排序和排名，取前size个文档。然后进行第二步，去相关的shard取document。这种方式返回的document与用户要求的size是相等的。

 3、DFS query and fetch
    这种方式比第一种方式多了一个初始化散发(initial scatter)步骤，有这一步，据说可以更精确控制搜索打分和排名。

 4、DFS query then fetch
    比第2种方式多了一个初始化散发(initial scatter)步骤。





 */

@Service
public class EsBlogServiceImpl implements EsBlogService {

    @Autowired
    private EsBlogRepository esBlogRepository;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private UserService userService;


    @Override
    public void addBlog(EsBlog blog) {

        esBlogRepository.save(blog);

    }

    @Override
    public EsBlog getEsBlogByBlogId(Long id) {
        return esBlogRepository.findByBlogId(id);
    }

    @Override
    public void deleteEsBlog(EsBlog esBlog) {
        esBlogRepository.delete(esBlog);
    }


    /**
     *        条件最新查询
     */
    @Override
    public Page<EsBlog> findNewEsBlogsByCondition(String keyword, Pageable pageable) {
        Page<EsBlog> pages = null;
        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        if (pageable.getSort().isUnsorted()) {
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        pages = esBlogRepository.findDistinctByTitleOrSummaryOrContentOrTags
                                        (keyword,keyword,keyword,keyword,pageable);

        return pages;
    }

    /**
     *        条件最热查询
     */
    @Override
    public Page<EsBlog> findHotEsBligByCondition(String keyword, Pageable pageable) {

        Sort sort = new Sort(Sort.Direction.DESC,"readSize","commentSize","praise_count","createTime");
        if (pageable.getSort().isUnsorted())
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(),sort);

        Page<EsBlog> pages = esBlogRepository.
                findDistinctByTitleOrSummaryOrContentOrTags
                        (keyword,keyword,keyword,keyword,pageable);

        return pages;
    }


    /**
     *          查找最新的6篇文章
     */
    @Cacheable(cacheNames = "indexCache",key = "#root.methodName")
    public List<EsBlog> findTop6NewEsBlog() {
        System.out.println("查询最新blog");

        Page<EsBlog> page = this.findNewEsBlogsByCondition(null, new PageRequest(0, 6));
        return page.getContent();
    }

    /**
     *          查找最热门的6篇文章
     */
    @Cacheable(cacheNames = "indexCache",key = "#root.methodName")
    public List<EsBlog> findTop6HotEsBlog() {

        System.out.println("查询最热门文章");

        Page<EsBlog> page =this.findHotEsBligByCondition(null,new PageRequest(0,6));
        return page.getContent();
    }


    /**
     *      查找最热门的用户
     *              --根据用户发表文章的个数和阅读量
     */
    @Cacheable(cacheNames = "indexCache",key = "#root.methodName")
    public List<User> findTopUsers() {
        /*System.out.println("查询user");*/

        //使用elasticsearchTemplate操作es
        //1-查询方式器     --什么短语查询，匹配查询之类
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();  //设置查询所有，相当于不设置查询条件

       //2-聚合器TermsBuilder     --构建聚合器的工具类AggregationBuilders
        TermsAggregationBuilder termsBuilder = AggregationBuilders
                .terms("groupby_hotUser")  //给这个聚合查询任意取的名字
                .field("user_id")
                //排序策略，根据聚合后的hot字段别名进行排序
                .order(Terms.Order.aggregation("hot", false))
                .size(6)
                 //子聚合 求和
                .subAggregation(
                        AggregationBuilders
                                .sum("hot")
                                .field("readSize") //.field("commentSize").field("praise_count")
                );

        //3-构建SearchQuery查询条件器
       SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("myblog").withTypes("blog")   //设置要查询的索引和类型
                .withSearchType(SearchType.QUERY_THEN_FETCH) //置搜索类型
                .withQuery(queryBuilder)
                .addAggregation(termsBuilder)
                .build();



       //4-使用es模版对象进行根据查询器进行查询,得到聚合结果集
        //方法1,通过reporitory执行查询,获得有Page包装了的结果集
        //方法2,通过elasticSearch模板elasticsearchTemplate.queryForList方法查询

        //方法3,通过elasticSearch模板elasticsearchTemplate.query()方法查询,获得聚合(常用)
        Aggregations results = esTemplate.query(searchQuery, (searchResponse -> searchResponse.getAggregations()));


        //5-从结果集中进行结果提取
        List userIds = new ArrayList<>();
        LongTerms modelTerms =  (LongTerms) results.asMap().get("groupby_hotUser");
        List<LongTerms.Bucket> buckets = modelTerms.getBuckets(); //获得所有桶

        for (LongTerms.Bucket e : buckets){
           // System.out.println(e.getKey()+":"+e.getDocCount());
            userIds.add(e.getKey());
        }


        //6-根据查找出所有热门用户的id去数据库查找
        List<User> userList = null;
        if (userIds != null)
            userList = userService.findUserListById(userIds);

        return userList;
    }

    @Override
    @Cacheable(cacheNames = "indexCache",key = "#root.methodName")
    public List<TagVo> findTopTags() {

     /*   System.out.println("查询标签");*/

        List<TagVo> tagVoList = new ArrayList<>();
        Map<String,Integer> tagMap = new TreeMap();

        TermsAggregationBuilder termsBuilder = AggregationBuilders
                .terms("groupby_tags")  //给这个聚合查询任意取的名字
                .field("tags.keyword")   // text字段排序报错解决 : Fielddata is disabled on text fields by default
                .order(Terms.Order.count(false))
                .size(30);

        SearchQuery searchQuery =  new NativeSearchQueryBuilder()
                .withIndices("myblog").withTypes("blog")   //设置要查询的索引和类型
                .withSearchType(SearchType.QUERY_THEN_FETCH) //置搜索类型
                .withQuery(QueryBuilders.matchAllQuery())
                .addAggregation(termsBuilder)
                .build();

        Aggregations aggregations = esTemplate.query(searchQuery, (searchResponse -> searchResponse.getAggregations()));

        StringTerms terms = (StringTerms) aggregations.asMap().get("groupby_tags");

        for (Terms.Bucket e : terms.getBuckets()){
            String[] arr = e.getKeyAsString().split(",");
            Integer count = (int)e.getDocCount();

            for (String tag : arr){
                if (tag.equals(""))  // 空字符串标签也会聚合出来
                    continue;

                if (!tagMap.containsKey(tag))
                    tagMap.put(tag,1*count);
                else
                    tagMap.put(tag,tagMap.get(tag)+1*count);
            }
        }

        tagMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(e ->  tagVoList.add(new TagVo(e.getKey(),e.getValue())));


        return tagVoList;
    }
}
