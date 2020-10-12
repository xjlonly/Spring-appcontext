package org.itranswarp.springioc.myorm;

abstract class CriteriaQuery<T> {
    protected final Criteria<T> criteria;
    CriteriaQuery(Criteria<T> criteria){
        this.criteria = criteria;
    }
    String sql(){
       return this.criteria.sql();
    }

}
