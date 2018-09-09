package nexucis.elasticsearch.entity;

import nexucis.elasticsearch.data.annotation.Document;
import nexucis.elasticsearch.data.annotation.Id;

import java.io.Serializable;

@Document(alias = "index-test", type = "type-test")
public class EntityTest implements Serializable {

    private static final long serialVersionUID = 5728619202845564904L;

    @Id
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
