package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.data.annotation.AnnotationManager;
import nexucis.elasticsearch.data.annotation.Document;
import nexucis.elasticsearch.data.type.Page;
import nexucis.elasticsearch.utils.JsonUtils;
import nexucis.elasticsearch.utils.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocumentNamespace {

    private RestHighLevelClient client;
    private AnnotationManager annotationManager;

    public DocumentNamespace(RestHighLevelClient client, AnnotationManager annotationManager) {
        this.client = client;
        this.annotationManager = annotationManager;
    }

    public <T> T create(T entity) throws IOException {
        Document document = this.annotationManager.getDocument(entity.getClass());

        String id = this.annotationManager.getId(entity);
        IndexRequest request;

        if (StringUtils.isEmpty(id)) {
            request = new IndexRequest(this.annotationManager.getAlias(document), document.type());
        } else {
            request = new IndexRequest(this.annotationManager.getAlias(document), document.type(), id);
            // remove the Id in order to not create a value in elasticSearch
            this.annotationManager.setId(null, entity.getClass());
        }

        request.source(JsonUtils.getJsonFromObject(entity), XContentType.JSON);

        IndexResponse response = client.index(request);

        this.annotationManager.setId(response.getId(), entity);

        return entity;
    }

    public <T> Optional<T> get(String id, Class<T> clazz) throws IOException {
        Document document = this.annotationManager.getDocument(clazz);
        GetRequest getRequest = new GetRequest(this.annotationManager.getAlias(document), document.type(), id);
        return this.get(getRequest, clazz);
    }

    public <T> Optional<T> get(GetRequest getRequest, Class<T> clazz) throws IOException {
        GetResponse response = client.get(getRequest);

        if (!response.isExists()) {
            return Optional.empty();
        }

        T entity = JsonUtils.getObjectFromString(response.getSourceAsString(), clazz);
        this.annotationManager.setId(response.getId(), entity);

        return Optional.of(entity);
    }

    public <T> Optional<T> findOne(QueryBuilder queryBuilder, Class<T> clazz) throws IOException {
        Page<T> page = this.find(queryBuilder, 0, 1, clazz);

        if (page.getHits().size() == 0) {
            return Optional.empty();
        }

        return Optional.of(page.getHits().get(0));
    }

    public <T> Page<T> findAll(int from, int size, Class<T> clazz) throws IOException {
        return this.find(QueryBuilders.matchAllQuery(), from, size, clazz);
    }

    public <T> Page<T> find(QueryBuilder queryBuilder, int from, int size, Class<T> clazz) throws IOException {
        Document document = this.annotationManager.getDocument(clazz);
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder)
                .from(from)
                .size(size);
        searchRequest.source(searchSourceBuilder)
                .indices(this.annotationManager.getAlias(document))
                .types(document.type());

        return this.find(searchRequest, clazz);
    }

    public <T> Page<T> find(SearchRequest searchRequest, Class<T> clazz) throws IOException {
        SearchResponse searchResponse = client.search(searchRequest);

        Page<T> page = new Page<>();

        if (!RestStatus.OK.equals(searchResponse.status())) {
            return page;
        }

        SearchHits hits = searchResponse.getHits();
        long totalElement = hits.getTotalHits();
        List<T> pageHits = new ArrayList<>();

        for (SearchHit hit : hits.getHits()) {
            T entity = JsonUtils.getObjectFromString(hit.getSourceAsString(), clazz);
            this.annotationManager.setId(hit.getId(), entity);
            pageHits.add(entity);
        }

        page.setTotalElement(totalElement)
                .setTotalPage(totalElement / searchRequest.source().size())
                .setHits(pageHits);

        return page;
    }

}
