package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.data.annotation.Document;
import nexucis.elasticsearch.data.exception.ShardException;
import nexucis.elasticsearch.data.type.Page;
import nexucis.elasticsearch.utils.JsonUtils;
import nexucis.elasticsearch.utils.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RestHighLevelClient;
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
import java.util.StringJoiner;

public class DocumentNamespace extends AbstractNamespace {

    public DocumentNamespace(RestHighLevelClient client) {
        super(client);
    }

    public <T> T create(T entity) throws IOException, ShardException {
        Document document = this.getDocument(entity.getClass());

        String id = this.getId(entity.getClass());
        IndexRequest request;

        if (StringUtils.isEmpty(id)) {
            request = new IndexRequest(this.getIndex(document), document.type());
        } else {
            request = new IndexRequest(this.getIndex(document), document.type(), id);
        }

        // remove the Id in order to not create a value in elasticSearch
        this.setId(null, entity.getClass());

        request.source(JsonUtils.getJsonFromObject(entity))
                .create(true);

        IndexResponse response = client.index(request);

        ReplicationResponse.ShardInfo shardInfo = response.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            throw new ShardException("number of successful shards (" + shardInfo.getSuccessful() + ") is less than total shards (" + shardInfo.getTotal() + ")");
        }
        if (shardInfo.getFailed() > 0) {
            StringJoiner builder = new StringJoiner(";");
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                builder.add(failure.reason());
            }
            throw new ShardException(builder.toString());
        }

        this.setId(response.getId(), entity.getClass());

        return entity;
    }

    public <T> Optional<T> get(String id, Class<T> clazz) throws IOException {
        Document document = this.getDocument(clazz);
        GetRequest getRequest = new GetRequest(this.getIndex(document), document.type(), id);
        return this.get(getRequest, clazz);
    }

    public <T> Optional<T> get(GetRequest getRequest, Class<T> clazz) throws IOException {
        GetResponse response = client.get(getRequest);

        if (!response.isExists()) {
            return Optional.empty();
        }

        T entity = JsonUtils.getObjectFromString(response.getSourceAsString(), clazz);
        this.setId(response.getId(), entity.getClass());

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
        Document document = this.getDocument(clazz);
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder)
                .from(from)
                .size(size);
        searchRequest.source(searchSourceBuilder)
                .indices(this.getIndex(document))
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
            this.setId(hit.getId(), entity.getClass());
            pageHits.add(entity);
        }

        page.setTotalElement(totalElement)
                .setTotalPage(totalElement / searchRequest.source().size())
                .setHits(pageHits);

        return page;
    }

}
