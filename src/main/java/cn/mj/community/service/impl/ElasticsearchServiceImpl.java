package cn.mj.community.service.impl;

import cn.mj.community.dao.DiscussPostMapper;
import cn.mj.community.pojo.DiscussPost;
import cn.mj.community.service.ElasticsearchService;
import cn.mj.community.service.LikeService;
import org.apache.commons.codec.binary.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    @Autowired
    private ElasticsearchRepository elasticsearchRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void addDiscusPost(DiscussPost discussPost) {
        elasticsearchRepository.save(discussPost);
    }

    @Override
    public void deleteDiscussPost(int id) {
        elasticsearchRepository.deleteById(id);
    }

    @Override
    public Map<String,Object> selectDiscussPosts(String keyword, int current, int limit) {
        Map<String,Object> map = new HashMap<>();
        List<DiscussPost> arrayList = new ArrayList<>();

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC),
                        SortBuilders.fieldSort("score").order(SortOrder.DESC),
                        SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        org.springframework.data.elasticsearch.core.SearchHits<DiscussPost> searchHits =
                elasticsearchRestTemplate.search(nativeSearchQuery, DiscussPost.class);

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
                        sb.append(fragment);
                        break;
                    }
                    discussPost.setTitle(sb.toString());
                }else if(StringUtils.equals(key, "content")){
                    List<String> fragments = highlightFieldsStr.getValue();
                    StringBuilder sb = new StringBuilder();
                    for (String fragment : fragments){
                        sb.append(fragment);
                        break;
                    }
                    discussPost.setContent(sb.toString());
                }
            }
            arrayList.add(discussPost);
        }
        map.put("discussPosts",arrayList);
        map.put("count",searchHits.getTotalHits());
        return map;
    }
}
