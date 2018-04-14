package nexucis.elasticsearch.entity;

import nexucis.elasticsearch.data.annotation.Id;

import java.io.Serializable;

public class EntityWithoutDocumentAnnotation implements Serializable {

    private static final long serialVersionUID = 490925652200439032L;

    @Id
    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
