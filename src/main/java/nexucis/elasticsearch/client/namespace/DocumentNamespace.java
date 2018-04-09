package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.data.annotation.Document;
import nexucis.elasticsearch.data.type.Page;
import nexucis.elasticsearch.utils.JsonUtils;
import nexucis.elasticsearch.utils.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
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

public class DocumentNamespace extends AbstractNamespace {

    public DocumentNamespace(RestHighLevelClient client) {
        super(client);
    }

    public <T> Optional<T> get(String id, Class<T> clazz) throws IOException {
        Document document = this.getDocument(clazz);
        String index = StringUtils.isNotEmpty(document.alias()) ? document.alias() : document.index();
        GetRequest getRequest = new GetRequest(index, document.type(), id);

        return this.get(getRequest, clazz);
    }

    public <T> Optional<T> get(GetRequest getRequest, Class<T> clazz) throws IOException {
        GetResponse response = client.get(getRequest);

        if (!response.isExists()) {
            return Optional.empty();
        }

        T entity = JsonUtils.getObjectFromString(response.getSourceAsString(), clazz);

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
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder)
                .from(from)
                .size(size);
        searchRequest.source(searchSourceBuilder);

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
            pageHits.add(entity);
        }

        page.setTotalElement(totalElement)
                .setTotalPage(totalElement / searchRequest.source().size())
                .setHits(pageHits);

        return page;
    }

}
