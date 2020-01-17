package com.iheartmedia.dlct.espoc.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import static com.iheartmedia.dlct.espoc.util.Constant.INDEX;
import static com.iheartmedia.dlct.espoc.util.Constant.TYPE;

@Slf4j
@Component
public class ElasticHelper {

    private RestHighLevelClient client;

    @Autowired
    public ElasticHelper(RestHighLevelClient client) {
        this.client = client;
    }

    public boolean createIndexWithMapping(JSONObject index) throws Exception{
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX);
        XContentBuilder builder = XContentFactory.jsonBuilder().prettyPrint();
        try (XContentParser parser = XContentFactory.xContent(XContentType.JSON).createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, index.toString())) {
            builder.copyCurrentStructure(parser);
        }
        createIndexRequest.source(builder);
        createIndexRequest.timeout(TimeValue.timeValueMinutes(2));
        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }

    public boolean deleteIndex() throws Exception {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(INDEX);
        AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        return deleteIndexResponse.isAcknowledged();
    }

}
