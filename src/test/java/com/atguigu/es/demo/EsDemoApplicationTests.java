package com.atguigu.es.demo;

import com.atguigu.es.demo.pojo.User;
import com.atguigu.es.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class EsDemoApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Test
    void contextLoads() {
        this.restTemplate.createIndex(User.class);
        this.restTemplate.putMapping(User.class);
    }
    @Test
    void testRepository(){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchQuery("name","冰冰"));
        queryBuilder.withSort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
        //分页参数。1-页码，从零开始，取值是pageNum-1
        queryBuilder.withPageable(PageRequest.of(1,2));
        queryBuilder.withFilter(QueryBuilders.rangeQuery("age").gte(18).lte(22));
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"name","age"},null));
        queryBuilder.withHighlightBuilder(new HighlightBuilder().field("name").preTags("<em>").postTags("</em"));
        queryBuilder.addAggregation(AggregationBuilders.terms("pswAgg").field("password"));
        AggregatedPage<User> userPage = (AggregatedPage)this.repository.search(queryBuilder.build());
        System.out.println(userPage.getContent());
        ParsedStringTerms pswAgg = (ParsedStringTerms)userPage.getAggregation("pswAgg");
        pswAgg.getBuckets().forEach(bucket -> {
            System.out.println(bucket.getKeyAsString());
            System.out.println(bucket.getDocCount());
        });


//        this.repository.search(QueryBuilders.matchQuery("name","冰冰")).forEach(System.out::println);
//        System.out.println(this.repository.findById(1L));
//        System.out.println(this.repository.findByAgeBetween(19, 21));
//        System.out.println(this.repository.findByAge(19, 21));


//        List<User> users = new ArrayList<>();
//        users.add(new User(1l, "柳冰冰", 18, "654321"));
//        users.add(new User(2l, "范冰冰", 19, "123456"));
//        users.add(new User(3l, "李冰冰", 20, "654321"));
//        users.add(new User(4l, "锋冰冰", 21, "123456"));
//        users.add(new User(5l, "小冰冰", 22, "654321"));
//        users.add(new User(6l, "韩冰冰", 23, "123456"));
//        this.repository.saveAll(users);
 //       this.repository.deleteById(4L);
    }

}
