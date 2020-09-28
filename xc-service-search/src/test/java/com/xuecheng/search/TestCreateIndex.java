package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.text.Highlighter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestCreateIndex {

    @Autowired
    RestHighLevelClient client;
    @Autowired
    RestClient restClient;

    //删除索引库
    @Test
    public void testDeleteIndex() throws IOException {
        //删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        //删除索引
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);
        //删除索引响应结果
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    //创建索引库
    @Test
    public void testCreateIndex() throws IOException {
        //创建索引请求对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");//xc_course
        //设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", 1)
                .put("number_of_replicas", 0));
        //设置映射
        createIndexRequest.mapping("doc", " {\n" +
                " \t\"properties\": {\n" +
                "           \"name\": {\n" +
                "              \"type\": \"text\",\n" +
                "              \"analyzer\":\"ik_max_word\",\n" +
                "              \"search_analyzer\":\"ik_smart\"\n" +
                "           },\n" +
                "           \"description\": {\n" +
                "              \"type\": \"text\",\n" +
                "              \"analyzer\":\"ik_max_word\",\n" +
                "              \"search_analyzer\":\"ik_smart\"\n" +
                "           },\n" +
                "           \"studymodel\": {\n" +
                "              \"type\": \"keyword\"\n" +
                "           },\n" +
                "           \"price\": {\n" +
                "              \"type\": \"float\"\n" +
                "           }\n" +
                "        }\n" +
                "}", XContentType.JSON);
        //创建索引操作客户端
        IndicesClient indices = client.indices();
        //创建响应对象
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        //得到响应结果
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    //添加文档(添加索引)
    @Test
    public void testAddDoc() throws Exception {
        //1  准备json数据
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("name", "spring cloud实战");
        hashMap.put("description", "本课程主要从四个章节进行讲解: 1.   2.   3.   4.   ");
        hashMap.put("studaymodel", "201001");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        hashMap.put("timestamp", dateFormat.format(new Date()));
        hashMap.put("price", 5.6f);
        //2  索引请求对象  创建索引创建对象
        IndexRequest indexRequest = new IndexRequest("zzq", "doc");//索引库  类型
        //3  指定索引文档内容
        indexRequest.source(hashMap);
        //4  索引响应对象  通过client进行http请求
        IndexResponse indexResponse = client.index(indexRequest);
        //5  获取响应结果
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println(result);
    }

    //查询文档
    @Test
    public void testGetDoc() throws Exception {
        //1  查询请求对象
        GetRequest getRequest = new GetRequest("zzq", "doc", "CzHm424BLse1aI4Wkerm");
        //2  通过client获取响应内容
        GetResponse getResponse = client.get(getRequest);
        //3  得到文档的内容转为map集合
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        System.out.println(sourceAsMap);
    }

    //java测试搜索全部记录
    @Test
    public void testSearchAll() throws IOException {
        //1  搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //2  搜索制定类型
        searchRequest.types("doc");

        //3  搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //4  向搜索请求对象中设置搜索源
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());//搜索源类型:matchAllQuery为搜索全部
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});//设置查询条件  参数1:包括的字段 参数2:不包括的字段
        searchRequest.source(searchSourceBuilder);

        //5  执行搜索
        SearchResponse search = client.search(searchRequest);

        //6  获取搜索结果
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();//获得的总记录数
        SearchHit[] hitsHits = hits.getHits();//得到匹配度高的文档  是个数组
        for (SearchHit hit : hitsHits) {
            String id = hit.getId();//文档的主键
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//文档的主要内容
            String name = (String) sourceAsMap.get("name");//内容中的name(键)对应的值
            System.out.println(name);
        }
    }

    //java测试分页查询
    @Test
    public void testSearchPage() throws IOException {
        //1  搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //2  搜索制定类型
        searchRequest.types("doc");

        //3  搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //***********
        int page = 1;//起始页
        int size = 1;//每页显示条数
        int from = (page - 1) * size;//分页起始索引
        searchSourceBuilder.from(from);//设置分页起始索引
        searchSourceBuilder.size(size);//设置分页每页显示条数
        //***********
        //4  向搜索请求对象中设置搜索源
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());//搜索源类型:matchAllQuery为搜索全部
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});//设置查询条件  参数1:包括的字段 参数2:不包括的字段
        searchRequest.source(searchSourceBuilder);

        //5  执行搜索
        SearchResponse search = client.search(searchRequest);

        //6  获取搜索结果
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();//获得符合条件的总记录数
        SearchHit[] hitsHits = hits.getHits();//得到匹配度高的文档  是个数组  分页的记录数
        for (SearchHit hit : hitsHits) {
            String id = hit.getId();//文档的主键
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//文档的主要内容
            String name = (String) sourceAsMap.get("name");//内容中的name(键)对应的值
            System.out.println(name);
        }
    }

    //java测试搜索Term Query  精确查询，在搜索时会整体匹配关键字，不再将关键字分词。
    @Test  //文档内容为name   值为spring
    public void testTermQuery() throws IOException {
        //1  搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //2  搜索制定类型
        searchRequest.types("doc");

        //3  搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //***********
        int page = 1;//起始页
        int size = 1;//每页显示条数
        int from = (page - 1) * size;//分页起始索引
        searchSourceBuilder.from(from);//设置分页起始索引
        searchSourceBuilder.size(size);//设置分页每页显示条数
        //***********
        //4  向搜索请求对象中设置搜索源
        searchSourceBuilder.query(QueryBuilders.termQuery("name", "spring"));//搜索源类型:matchAllQuery为搜索全部
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});//设置查询条件  参数1:包括的字段 参数2:不包括的字段
        searchRequest.source(searchSourceBuilder);

        //5  执行搜索
        SearchResponse search = client.search(searchRequest);

        //6  获取搜索结果
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();//获得符合条件的总记录数
        SearchHit[] hitsHits = hits.getHits();//得到匹配度高的文档  是个数组  分页的记录数
        for (SearchHit hit : hitsHits) {
            String id = hit.getId();//文档的主键
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//文档的主要内容
            String name = (String) sourceAsMap.get("name");//内容中的name(键)对应的值
            System.out.println(name);
        }
    }


    //java测试搜索Term Query  精确查询，在搜索时会整体匹配关键字，不再将关键字分词。
    @Test  //根据id精确匹配
    public void testTermQueryById() throws IOException {
        //1  搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //2  搜索制定类型
        searchRequest.types("doc");

        //3  搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //***********
        int page = 1;//起始页
        int size = 1;//每页显示条数
        int from = (page - 1) * size;//分页起始索引
        searchSourceBuilder.from(from);//设置分页起始索引
        searchSourceBuilder.size(size);//设置分页每页显示条数
        //***********
        //4  向搜索请求对象中设置搜索源
        String[] ids = new String[]{"1", "2"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id", ids));//搜索源类型:matchAllQuery为搜索全部
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});//设置查询条件  参数1:包括的字段 参数2:不包括的字段
        searchRequest.source(searchSourceBuilder);

        //5  执行搜索
        SearchResponse search = client.search(searchRequest);

        //6  获取搜索结果
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();//获得符合条件的总记录数
        SearchHit[] hitsHits = hits.getHits();//得到匹配度高的文档  是个数组  分页的记录数
        for (SearchHit hit : hitsHits) {
            String id = hit.getId();//文档的主键
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//文档的主要内容
            String name = (String) sourceAsMap.get("name");//内容中的name(键)对应的值
            System.out.println(name);
        }
    }

    //java测试搜索Match Query  精确查询，在搜索时会整体匹配关键字，不再将关键字分词。
    @Test  //文档内容为name   值为spring
    public void testMatchQuery() throws IOException {
        //1  搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //2  搜索制定类型
        searchRequest.types("doc");

        //3  搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //***********
        int page = 1;//起始页
        int size = 1;//每页显示条数
        int from = (page - 1) * size;//分页起始索引
        searchSourceBuilder.from(from);//设置分页起始索引
        searchSourceBuilder.size(size);//设置分页每页显示条数
        //***********
        //4  向搜索请求对象中设置搜索源
        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "spring开发框架")
                .minimumShouldMatch("80%"));//搜索源类型:matchQuery为全文检索
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});//设置查询条件  参数1:包括的字段 参数2:不包括的字段
        searchRequest.source(searchSourceBuilder);

        //5  执行搜索
        SearchResponse search = client.search(searchRequest);

        //6  获取搜索结果
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();//获得符合条件的总记录数
        SearchHit[] hitsHits = hits.getHits();//得到匹配度高的文档  是个数组  分页的记录数
        for (SearchHit hit : hitsHits) {
            String id = hit.getId();//文档的主键
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//文档的主要内容
            String name = (String) sourceAsMap.get("name");//内容中的name(键)对应的值
            System.out.println(name);
        }
    }

    //java测试搜索Multi Query
    @Test  //匹配多个字段时可以提升字段的boost（权重）来提高得分
    public void testMulti() throws IOException {
        //1  搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //2  搜索制定类型
        searchRequest.types("doc");

        //3  搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //4  向搜索请求对象中设置搜索源
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10));//搜索源类型:matchQuery为全文检索
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});//设置查询条件  参数1:包括的字段 参数2:不包括的字段
        searchRequest.source(searchSourceBuilder);

        //5  执行搜索
        SearchResponse search = client.search(searchRequest);

        //6  获取搜索结果
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();//获得符合条件的总记录数
        SearchHit[] hitsHits = hits.getHits();//得到匹配度高的文档  是个数组  分页的记录数
        for (SearchHit hit : hitsHits) {
            String id = hit.getId();//文档的主键
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//文档的主要内容
            String name = (String) sourceAsMap.get("name");//内容中的name(键)对应的值
            System.out.println(name);
        }
    }

    //java测试搜索Boolean Query
    @Test  //匹配多个字段时可以提升字段的boost（权重）来提高得分
    public void testBooleanQuery() throws IOException {
        //1  搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //2  搜索制定类型
        searchRequest.types("doc");

        //3  搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义MultiMatchQueryBuilder查询条件
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);
        //定义TermQueryBuilder查询条件
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        //4  向搜索请求对象中设置搜索源
        searchSourceBuilder.query(boolQueryBuilder);//搜索源类型:boolQueryBuilder
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});//设置查询条件  参数1:包括的字段 参数2:不包括的字段
        searchRequest.source(searchSourceBuilder);

        //5  执行搜索
        SearchResponse search = client.search(searchRequest);

        //6  获取搜索结果
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();//获得符合条件的总记录数
        SearchHit[] hitsHits = hits.getHits();//得到匹配度高的文档  是个数组  分页的记录数
        for (SearchHit hit : hitsHits) {
            String id = hit.getId();//文档的主键
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//文档的主要内容
            String name = (String) sourceAsMap.get("name");//内容中的name(键)对应的值
            System.out.println(name);
        }
    }


    //java测试搜索过虑器
    @Test  //过虑是针对搜索的结果进行过虑，过虑器主要判断的是文档是否匹配，不去计算和判断文档的匹配度得分，所以过虑器性能比查询要高，且方便缓存
    public void testFilterQuery() throws IOException {
        //1  搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //2  搜索制定类型
        searchRequest.types("doc");

        //3  搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义MultiMatchQueryBuilder查询条件
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte("60").lte("100"));
        //4  向搜索请求对象中设置搜索源
        searchSourceBuilder.query(boolQueryBuilder);//搜索源类型:boolQueryBuilder
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});//设置查询条件  参数1:包括的字段 参数2:不包括的字段
        searchRequest.source(searchSourceBuilder);

        //5  执行搜索
        SearchResponse search = client.search(searchRequest);

        //6  获取搜索结果
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();//获得符合条件的总记录数
        SearchHit[] hitsHits = hits.getHits();//得到匹配度高的文档  是个数组  分页的记录数
        for (SearchHit hit : hitsHits) {
            String id = hit.getId();//文档的主键
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//文档的主要内容
            String name = (String) sourceAsMap.get("name");//内容中的name(键)对应的值
            System.out.println(name);
        }
    }

    //java测试排序
    @Test  //过虑是针对搜索的结果进行过虑，过虑器主要判断的是文档是否匹配，不去计算和判断文档的匹配度得分，所以过虑器性能比查询要高，且方便缓存
    public void testSortQuery() throws IOException {
        //1  搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //2  搜索制定类型
        searchRequest.types("doc");
        //3  搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte("0").lte("100"));
        //4  向搜索请求对象中设置搜索源
        searchSourceBuilder.query(boolQueryBuilder);//搜索源类型:boolQueryBuilder

        searchSourceBuilder.sort("studymodel", SortOrder.DESC);//降序descending order
        searchSourceBuilder.sort("price", SortOrder.ASC);//升序  ascending order

        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});//设置查询条件  参数1:包括的字段 参数2:不包括的字段
        searchRequest.source(searchSourceBuilder);

        //5  执行搜索
        SearchResponse search = client.search(searchRequest);

        //6  获取搜索结果
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();//获得符合条件的总记录数
        SearchHit[] hitsHits = hits.getHits();//得到匹配度高的文档  是个数组  分页的记录数
        for (SearchHit hit : hitsHits) {
            String id = hit.getId();//文档的主键
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//文档的主要内容
            String name = (String) sourceAsMap.get("name");//内容中的name(键)对应的值
            System.out.println(name);
        }
    }

    //java测试高亮
    @Test  //过虑是针对搜索的结果进行过虑，过虑器主要判断的是文档是否匹配，不去计算和判断文档的匹配度得分，所以过虑器性能比查询要高，且方便缓存
    public void testHighLight() throws IOException {
        //1  搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //2  搜索制定类型
        searchRequest.types("doc");

        //3  搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义MultiMatchQueryBuilder查询条件
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发框架", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        //定义过滤器
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte("0").lte("100"));
        //4  向搜索请求对象中设置搜索源
        searchSourceBuilder.query(boolQueryBuilder);//搜索源类型:boolQueryBuilder
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});//设置查询条件  参数1:包括的字段 参数2:不包括的字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        highlightBuilder.preTags("<tag>");//前缀
        highlightBuilder.postTags("<tag>");//后缀
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));//目标字段
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        //5  执行搜索
        SearchResponse search = client.search(searchRequest);

        //6  获取搜索结果
        SearchHits hits = search.getHits();
        long totalHits = hits.getTotalHits();//获得符合条件的总记录数
        SearchHit[] hitsHits = hits.getHits();//得到匹配度高的文档  是个数组  分页的记录数
        for (SearchHit hit : hitsHits) {
            String id = hit.getId();//文档的主键
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//文档的主要内容
            String name = (String) sourceAsMap.get("name");//内容中的name(键)对应的值

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null){
                HighlightField nameFiled = highlightFields.get("name");
                if(nameFiled!=null){
                    Text[] fragments = nameFiled.fragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text text : fragments) {
                        stringBuffer.append(text);
                    }
                    name = stringBuffer.toString();
                }
            }
            System.out.println(name);
        }
    }


}
