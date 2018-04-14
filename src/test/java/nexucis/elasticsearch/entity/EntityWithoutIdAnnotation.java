package nexucis.elasticsearch.entity;

import nexucis.elasticsearch.data.annotation.Document;

import java.io.Serializable;

@Document(index = "test_annotation", type = "test")
public class EntityWithoutIdAnnotation implements Serializable {

    private static final long serialVersionUID = 136183907810823313L;

    private String id;

    private String age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
