package org.sang.mongodb.dao;

import org.sang.mongodb.document.CategoryDoc;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CategoryDao
 * @Description TODO
 * @Author HeX
 * @Date 2022/1/30 19:39
 * @Version 1.0
 **/
@Repository
public class CategoryDao extends MongoDbDao<CategoryDoc> {
    @Override
    protected Class<CategoryDoc> getEntityClass() {
        return CategoryDoc.class;
    }
}
