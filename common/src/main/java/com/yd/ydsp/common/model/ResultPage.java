package com.yd.ydsp.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengyixun on 17/1/19.
 */
public class ResultPage<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6152628105833385292L;
    private List<T>           items            = new ArrayList<T>();
    private Long              total;
    private Integer           count;
    private Integer           pageSize;
    private Integer           pageNum;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * @return the items
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * @return the count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
