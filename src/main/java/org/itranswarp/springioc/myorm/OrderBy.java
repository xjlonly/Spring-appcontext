package org.itranswarp.springioc.myorm;

import java.util.ArrayList;
import java.util.List;

public final class OrderBy<T> extends CriteriaQuery<T>{

    OrderBy(Criteria<T> criteria,String orderBy) {
        super(criteria);
        orderBy(orderBy);
    }


    public OrderBy<T> orderBy(String orderBy){
        if(criteria.orderBy == null){
            criteria.orderBy = new ArrayList<>();
        }
        orderBy = checkProperty(orderBy);
        criteria.orderBy.add(orderBy);
        return  this;
    }

    String checkProperty(String orderBy){
        String prop = null;
        String upper = orderBy.toUpperCase();
        if(upper.endsWith(" DESC")){
            prop = orderBy.substring(0, orderBy.length() - 5).trim();
            return propertyToField(prop) + " DESC";
        }
        else if(upper.endsWith(" ASC")){
            prop = orderBy.substring(0, orderBy.length() - 4).trim();
            return propertyToField(prop) + " ASC";
        }else{
            prop = orderBy.trim();
            return propertyToField(prop);
        }
    }

    private String propertyToField(String prop) {
        AccessibleProperty ap = this.criteria.mapper.allPropertiesMap.get(prop.toLowerCase());
        if(ap == null){
            throw new IllegalArgumentException("Invalid property when use order by: " + prop);
        }else{
            return  ap.columnName;
        }
    }

    public OrderBy<T> desc(){
        int last = this.criteria.orderBy.size() - 1;
        String s = this.criteria.orderBy.get(last);
        if(!s.toUpperCase().endsWith(" DESC ")){
            s = s + " DESC";
        }
        criteria.orderBy.set(last, s);
        return this;
    }

    public Limit<T> limit(int maxResults){
        return limit(0, maxResults);
    }

    public Limit<T> limit(int offset, int maxResults){
        return new Limit<>(this.criteria,offset,maxResults);
    }

    public List<T> list(){
        return this.criteria.list();
    }

    public T first(){
        return this.criteria.first();
    }
    public T unique(){
        return this.criteria.unique();
    }
}
