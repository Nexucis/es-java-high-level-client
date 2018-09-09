package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.data.annotation.AnnotationManager;
import nexucis.elasticsearch.data.annotation.Document;
import nexucis.elasticsearch.data.exception.IndexAlreadyExists;
import nexucis.elasticsearch.data.exception.IndexNotPresent;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class IndexNamespace {

    private static final String INDEX_NAME_CONVENTION_1 = "_v1";
    private static final String INDEX_NAME_CONVENTION_2 = "_v2";

    private RestHighLevelClient client;
    private AnnotationManager annotationManager;

    public IndexNamespace(RestHighLevelClient client, AnnotationManager annotationManager) {
        this.client = client;
        this.annotationManager = annotationManager;
    }

    public <T> boolean createIndexByAlias(Class<T> clazz) throws IOException {
        Document document = this.annotationManager.getDocument(clazz);
        String alias = this.annotationManager.getAlias(document);
        String index = alias + IndexNamespace.INDEX_NAME_CONVENTION_1;

        if (this.existsIndex(index)) {
            throw new IndexAlreadyExists(index);
        }

        CreateIndexRequest request = new CreateIndexRequest(index).alias(new Alias(alias));

        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);

        return response.isAcknowledged() && response.isShardsAcknowledged();
    }

    public <T> void deleteIndexByAlias(Class<T> clazz) throws IOException {
        Document document = this.annotationManager.getDocument(clazz);
        String alias = this.annotationManager.getAlias(document);

        if (this.existsIndex(alias)) {
            throw new IndexAlreadyExists(alias);
        }

        String index = this.findIndexByAlias(alias);

        DeleteIndexRequest request = new DeleteIndexRequest(index);
        this.client.indices().delete(request,RequestOptions.DEFAULT);
    }

    protected boolean existsIndex(String index) throws IOException {
        return this.client.indices().exists(new GetIndexRequest().indices(index), RequestOptions.DEFAULT);
    }

    protected String findIndexByAlias(String alias) throws IOException {
        GetAliasesResponse response = this.client.indices().getAlias(new GetAliasesRequest(alias), RequestOptions.DEFAULT);
        Optional<Map.Entry<String, Set<AliasMetaData>>> opt = response.getAliases().entrySet().stream().findAny();
        if (!opt.isPresent()) {
            throw new IndexNotPresent(alias);
        }
        return opt.get().getKey();
    }
}
