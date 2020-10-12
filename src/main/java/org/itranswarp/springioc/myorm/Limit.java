package org.itranswarp.springioc.myorm;

import java.util.List;

public final class Limit<T> extends CriteriaQuery<T> {
    Limit(Criteria<T> criteria, int offset, int maxResult) {
        super(criteria);
        if(offset < 0){
            throw new IllegalArgumentException("offset must be >=0 ");
        }
        if(maxResult <= 0){
            throw  new IllegalArgumentException("maxResults must be > 0");
        }
        this.criteria.offset = offset;
        this.criteria.maxResults = maxResult;
    }

    public List<T> list(){
        return criteria.list();
    }
}
