package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.client.Client;
import nexucis.elasticsearch.data.exception.IllegalEntityException;
import nexucis.elasticsearch.data.exception.ShardException;
import nexucis.elasticsearch.entity.EntityTest;
import nexucis.elasticsearch.entity.EntityWithoutDocumentAnnotation;
import nexucis.elasticsearch.entity.EntityWithoutIdAnnotation;
import nexucis.elasticsearch.utils.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class DocumentNamespaceEntity {

    private Client client;

    @Before
    public void before() {
        client = new Client(new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http"))));
    }

    @After
    public void after() throws IOException {
        client.getRestHighLevelClient().indices().delete(new DeleteIndexRequest("_all"));
    }

    @Test(expected = IllegalEntityException.class)
    public void testRequestWithEntityMissingDocumentAnnotation() throws IOException, ShardException {
        EntityWithoutDocumentAnnotation entity = new EntityWithoutDocumentAnnotation();
        client.document().create(entity);
    }

    @Test(expected = IllegalEntityException.class)
    public void testRequestWithEntityMissingIdAnnotation() throws IOException, ShardException {
        EntityWithoutIdAnnotation entity = new EntityWithoutIdAnnotation();
        client.document().create(entity);
    }

    @Test
    public void testCreateEntity() throws IOException, ShardException {
        EntityTest entityTest = new EntityTest();
        entityTest.setAge("45");
        EntityTest entity = client.document().create(entityTest);

        Assert.assertTrue(StringUtils.isNotEmpty(entity.getId()));
    }

}
