package nexucis.elasticsearch.client.namespace;

import nexucis.elasticsearch.client.Client;
import nexucis.elasticsearch.data.exception.IllegalEntityException;
import nexucis.elasticsearch.entity.EntityComplete;
import nexucis.elasticsearch.entity.EntityWithoutDocumentAnnotation;
import nexucis.elasticsearch.entity.EntityWithoutIdAnnotation;
import nexucis.elasticsearch.utils.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class DocumentNamespaceTest {

    private Client client;

    @Before
    public void before() {
        client = new Client(new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http"))));
    }

    @After
    public void after() throws IOException {
        client.indices().deleteAll();
        client.close();
    }

    @Test(expected = IllegalEntityException.class)
    public void testRequestWithEntityMissingDocumentAnnotation() throws IOException {
        EntityWithoutDocumentAnnotation entity = new EntityWithoutDocumentAnnotation();
        client.document().create(entity);
    }

    @Test(expected = IllegalEntityException.class)
    public void testRequestWithEntityMissingIdAnnotation() throws IOException {
        EntityWithoutIdAnnotation entity = new EntityWithoutIdAnnotation();
        client.document().create(entity);
    }

    @Test
    public void testCreateEntity() throws IOException {
        EntityComplete entityTest = new EntityComplete();
        entityTest.setAge("45");
        EntityComplete entity = client.document().create(entityTest);

        Assert.assertTrue(StringUtils.isNotEmpty(entity.getId()));
    }

}
