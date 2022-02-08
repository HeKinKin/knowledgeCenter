package org.sang.mongodb.dao;

import org.sang.mongodb.document.TagDoc;
import org.springframework.stereotype.Repository;

/**
 * @ClassName TagDao
 * @Description TODO
 * @Author HeX
 * @Date 2022/1/31 0:07
 * @Version 1.0
 **/
@Repository
public class TagDao extends MongoDbDao<TagDoc> {
    @Override
    protected Class<TagDoc> getEntityClass() {
        return TagDoc.class;
    }
}
