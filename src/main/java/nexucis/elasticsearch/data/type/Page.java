package nexucis.elasticsearch.data.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {

    private static final long serialVersionUID = -2517507672159174317L;

    private long totalElement;

    private long totalPage;

    private List<T> hits;

    public Page() {
        this.totalElement = 0;
        this.totalPage = 0;
        this.hits = new ArrayList<>();
    }

    public Page(long totalElement, long totalPage, List<T> hits) {
        this.totalElement = totalElement;
        this.totalPage = totalPage;
        this.hits = hits;
    }

    public long getTotalElement() {
        return totalElement;
    }

    public Page<T> setTotalElement(long totalElement) {
        this.totalElement = totalElement;
        return this;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public Page<T> setTotalPage(long totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public List<T> getHits() {
        return hits;
    }

    public Page<T> setHits(List<T> hits) {
        this.hits = hits;
        return this;
    }
}
