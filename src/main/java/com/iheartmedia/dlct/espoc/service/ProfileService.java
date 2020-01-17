package com.iheartmedia.dlct.espoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iheartmedia.dlct.espoc.document.ProfileDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.iheartmedia.dlct.espoc.util.Constant.INDEX;
import static com.iheartmedia.dlct.espoc.util.Constant.TYPE;

@Service
@Slf4j
public class ProfileService {

    private RestHighLevelClient client;

    private ObjectMapper objectMapper;

    @Autowired
    public ProfileService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public String createProfileDocument(ProfileDocument document) throws Exception {
        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());

        Map<String, Object> documentMapper = objectMapper.convertValue(document, Map.class);
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, document.getId())
                .source(documentMapper);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

        return indexResponse
                .getResult()
                .name();
    }

    public String updateProfileDocument(ProfileDocument document) throws Exception {
        ProfileDocument resultDocument = findById(document.getId());

        UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, resultDocument.getId());
        Map<String, Object> documentMapper = objectMapper.convertValue(document, Map.class);
        updateRequest.doc(documentMapper);

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

        return updateResponse.getResult().name();
    }

    public ProfileDocument findById(String id) throws Exception {
        GetRequest getRequest = new GetRequest(INDEX, TYPE, id);

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> resultMap = getResponse.getSource();

        return objectMapper.convertValue(resultMap, ProfileDocument.class);
    }

    public List<ProfileDocument> findAll() throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public List<ProfileDocument> searchByTechnology(String technology) throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("technologies.name", technology));

        searchSourceBuilder.query(QueryBuilders.nestedQuery("technologies", queryBuilder, ScoreMode.Avg));

        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(response);
    }

    public String deleteProfileDocument(String id) throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);

        return response.getResult().name();
    }

    private List<ProfileDocument> getSearchResult(SearchResponse response) {
        SearchHit[] searchHits = response.getHits().getHits();
        List<ProfileDocument> profileDocuments = new ArrayList<>();
        if(searchHits.length > 0) {
            Arrays.stream(searchHits).forEach(hit -> profileDocuments.add(objectMapper.convertValue(hit.getSourceAsMap(), ProfileDocument.class)));
        }
        return profileDocuments;
    }

}
