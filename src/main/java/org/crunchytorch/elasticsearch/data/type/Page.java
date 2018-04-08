package org.crunchytorch.elasticsearch.data.type;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {
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
