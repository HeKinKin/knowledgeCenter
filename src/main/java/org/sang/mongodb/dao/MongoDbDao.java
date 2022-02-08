package org.sang.mongodb.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * mongoDB基础方法封装
 * @param <T>
 */

public abstract class MongoDbDao<T> {

    /**
     * 反射获取泛型类型
     */
    protected abstract Class<T> getEntityClass();

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存一个对象
     * @param t
     */
    public T save(T t) {
        System.out.println("-------------->MongoDB save start");
        this.mongoTemplate.save(t);
        return t;
    }

    /**
     * 根据id从集合中查询对象
     * @param id
     * @return
     */
    public T queryById(String id){
        Query query = new Query(Criteria.where("_id").is(id));
        System.out.println("-------------->MongoDB find start");
        return this.mongoTemplate.findOne(query,getEntityClass());
    }

    /**
     * 获取所有数据
     * @return
     */
    public List<T> getAll(){
        return this.mongoTemplate.find(new Query(),getEntityClass());
    }

    /**
     * 根据条件查询集合
     * @param object
     * @return
     */
    public List<T> queryList(T object){
        Query query = getQueryByObject(object);
        System.out.println("-------------->MongoDB find start");
        return mongoTemplate.find(query,this.getEntityClass());
    }

    /**
     * 根据自定义条件查询数据
     * @param criteria
     * @return
     */
    public List<T> queryListByCriteria(Criteria criteria){
        return this.mongoTemplate.find(new Query(criteria),getEntityClass());
    }

    /**
     * 根据条件分页查询
     * @param object
     * @param start
     * @param size
     * @return
     */
    public List<T> getPage(T object,int start,int size){
        Query query = getQueryByObject(object);
        query.skip(start);
        query.limit(size);
        System.out.println("-------------->MongoDB queryPage start");
        return this.mongoTemplate.find(query,this.getEntityClass());
    }

    /**
     * 根据条件查询库中符合条件的记录数量
     * @param object
     * @return
     */
    public Long getCount(T object){
        Query query = getQueryByObject(object);
        System.out.println("-------------->MongoDB Count start");
        return this.mongoTemplate.count(query, this.getEntityClass());
    }

    /**
     * 删除对象
     * @param t
     * @return
     */
    public int delete(T t){
        System.out.println("-------------->MongoDB delete start");
        return (int) this.mongoTemplate.remove(t).getDeletedCount();
    }

    /**
     * 根据id删除数据
     * @param id
     */
    public void deleteById(String id){
        Criteria criteria = Criteria.where("_id").is(id);
        if (null != criteria) {
            Query query = new Query(criteria);
            T obj = this.mongoTemplate.findOne(query,this.getEntityClass());
            System.out.println("-------------->MongoDB deleteById start");
            if (obj != null) {
                this.delete(obj);
            }
        }
    }

    /**
     * 修改匹配到的第一条记录
     * @param srcOBj
     * @param targetObj
     */
    public void updateFirst(T srcOBj,T targetObj) {
        Query query = getQueryByObject(srcOBj);
        Update update = getUpdateByObject(targetObj);
        System.out.println("-------------->MongoDB updateFirst start");
        this.mongoTemplate.updateFirst(query,update,this.getEntityClass());
    }

    /*MongoDB中更新操作分为三种
     * 1：updateFirst     修改第一条
     * 2：updateMulti     修改所有匹配的记录
     * 3：upsert  修改时如果不存在则进行添加操作
     * */


    /**
     * 修改匹配到的所有记录
     * @param srcObj
     * @param targetObj
     */
    public void updateMulti(T srcObj, T targetObj){
        Query query = getQueryByObject(srcObj);
        Update update = getUpdateByObject(targetObj);
        System.out.println("-------------->MongoDB updateFirst start");
        this.mongoTemplate.updateMulti(query,update,this.getEntityClass());
    }

    /***
     * 修改匹配到的记录，若不存在该记录则进行添加
     * @param srcObj
     * @param targetObj
     */
    public void updateInsert(T srcObj, T targetObj){
        Query query = getQueryByObject(srcObj);
        Update update = getUpdateByObject(targetObj);
        System.out.println("-------------->MongoDB updateInsert start");
        this.mongoTemplate.upsert(query,update,this.getEntityClass());
    }


    /**
     * 将查询条件对象转换为query
     * @param object
     * @return
     */
    private Query getQueryByObject(T object){
        Query query = new Query();
        String [] fields = getFiledName(object);
        Criteria criteria = new Criteria();
        for (int i = 0;i< fields.length;i++) {
            String filedName = fields[i];
            Object filedValue = getFieldValueByName(filedName,object);
            if (filedValue != null) {
                criteria.and(filedName).is(filedValue);
            }
        }
        query.addCriteria(criteria);
        return query;
    }


    /**
     * 将查询条件对象转换为update
     *
     * @param object
     * @return
     * @author Jason
     */
    private Update getUpdateByObject(T object) {
        Update update = new Update();
        String[] fileds = getFiledName(object);
        for (int i = 0; i < fileds.length; i++) {
            String filedName = fileds[i];
            Object filedValue =getFieldValueByName(filedName, object);
            if (filedValue != null) {
                update.set(filedName, filedValue);
            }
        }
        return update;
    }

    /**
     * 获取对象属性，返回字符串数组
     * @param o
     * @return
     */
    private static String[] getFiledName(Object o){
        // 获得某个类的所有声明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
        // getFields()：获得某个类的所有的公共（public）的字段，包括父类中的字段。
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        // 先i加1后，再使用
        for (int i = 0; i < fields.length; ++i) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }


    /**
     * 根据属性获取对象属性值
     * @param fieldName
     * @param o
     * @return
     */
    private static Object getFieldValueByName(String fieldName,Object o){
        try {
            String e = fieldName.substring(0,1).toUpperCase();
            String getter = "get" + e + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[0]);
            return method.invoke(o, new Object[0]);
        }catch (Exception e){
            return null;
        }

    }
}
