package cn.mj.community.dao.elasticsearch;
import cn.mj.community.dao.DiscussPostMapper;
import cn.mj.community.pojo.DiscussPost;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class DiscussPostRepositoryTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;
//    @Autowired
    private ElasticsearchTemplate elasticTemplate;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void test1(){
        //SAVE ONE
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(217);
        discussPostRepository.save(discussPost);
    }

    @Test
    public void test2(){
        //SAVE LIST
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133,0,100,0));
    }

    @Test
    public void test3() {
        //DELETE
        discussPostRepository.deleteAll();
    }

    @Test
    public void test４(){
        //SEARCH

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC),
                        SortBuilders.fieldSort("score").order(SortOrder.DESC),
                        SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        // elasticTemplate.queryForPage(searchQuery, class, SearchResultMapper)
        // 底层获取得到了高亮显示的值, 但是没有返回.
        org.springframework.data.elasticsearch.core.SearchHits<DiscussPost> searchHits =
                elasticsearchRestTemplate.search(nativeSearchQuery, DiscussPost.class);

        List<DiscussPost> arrayList = new ArrayList<>();

        for (org.springframework.data.elasticsearch.core.SearchHit<DiscussPost> searchHit : searchHits) { // 获取搜索到的数据
            DiscussPost content = searchHit.getContent();
            DiscussPost discussPost = new DiscussPost();
            BeanUtils.copyProperties(content, discussPost);

            //处理高亮
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            for (Map.Entry<String, List<String>> highlightFieldsStr : highlightFields.entrySet()){
                String key = highlightFieldsStr.getKey();
                System.out.println(" key = "+key);
                if (StringUtils.equals(key, "title")){
                    List<String> fragments = highlightFieldsStr.getValue();
                    StringBuilder sb = new StringBuilder();
                    for (String fragment : fragments){
                        //System.out.println(" -- "+fragment);
                        sb.append(fragment);
                        break;
                    }
                    discussPost.setTitle(sb.toString());
                }else if(StringUtils.equals(key, "content")){
                    List<String> fragments = highlightFieldsStr.getValue();
                    StringBuilder sb = new StringBuilder();
                    for (String fragment : fragments){
                        //System.out.println(" -- "+fragment);
                        sb.append(fragment);
                        break;
                    }
                    discussPost.setContent(sb.toString());
                }
            }
            arrayList.add(discussPost);
        }

        for(DiscussPost discussPost:arrayList){
            System.out.println(discussPost);
        }
    }

//    @Test
//    public void testSearchByTemplate() {
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
//                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
//                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
//                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
//                .withPageable(PageRequest.of(0, 10))
//                .withHighlightFields(
//                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
//                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
//                ).build();
//
//        Page<DiscussPost> page = elasticTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
//            @Override
//            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
//                SearchHits hits = response.getHits();
//                if (hits.getTotalHits() <= 0) {
//                    return null;
//                }
//
//                List<DiscussPost> list = new ArrayList<>();
//                for (SearchHit hit : hits) {
//                    DiscussPost post = new DiscussPost();
//
//                    String id = hit.getSourceAsMap().get("id").toString();
//                    post.setId(Integer.valueOf(id));
//
//                    String userId = hit.getSourceAsMap().get("userId").toString();
//                    post.setUserId(Integer.valueOf(userId));
//
//                    String title = hit.getSourceAsMap().get("title").toString();
//                    post.setTitle(title);
//
//                    String content = hit.getSourceAsMap().get("content").toString();
//                    post.setContent(content);
//
//                    String status = hit.getSourceAsMap().get("status").toString();
//                    post.setStatus(Integer.valueOf(status));
//
//                    String createTime = hit.getSourceAsMap().get("createTime").toString();
//                    post.setCreateTime(new Date(Long.valueOf(createTime)));
//
//                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
//                    post.setCommentCount(Integer.valueOf(commentCount));
//
//                    // 处理高亮显示的结果
//                    HighlightField titleField = hit.getHighlightFields().get("title");
//                    if (titleField != null) {
//                        post.setTitle(titleField.getFragments()[0].toString());
//                    }
//
//                    HighlightField contentField = hit.getHighlightFields().get("content");
//                    if (contentField != null) {
//                        post.setContent(contentField.getFragments()[0].toString());
//                    }
//
//                    list.add(post);
//                }
//
//                return new AggregatedPageImpl(list, pageable,
//                        hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
//            }
//        });
//
//        System.out.println(page.getTotalElements());
//        System.out.println(page.getTotalPages());
//        System.out.println(page.getNumber());
//        System.out.println(page.getSize());
//        for (DiscussPost post : page) {
//            System.out.println(post);
//        }
//    }
}
