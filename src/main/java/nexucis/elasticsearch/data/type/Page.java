package nexucis.elasticsearch.data.type;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

    private static final long serialVersionUID = -2517507672159174317L;

    private long totalElement;

    private long totalPage;

    private List<T> hits;

    public Page(long totalElement, long totalPage, List<T> hits) {
        this.totalElement = totalElement;
        this.totalPage = totalPage;
        this.hits = hits;
    }

    public long getTotalElement() {
        return totalElement;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public List<T> getHits() {
        return hits;
    }
}
