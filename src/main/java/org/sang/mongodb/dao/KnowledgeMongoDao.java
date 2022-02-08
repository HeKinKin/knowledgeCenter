package org.sang.mongodb.dao;

import org.sang.mongodb.document.KnowledgeDoc;
import org.springframework.stereotype.Repository;

@Repository
public class KnowledgeMongoDao extends MongoDbDao<KnowledgeDoc> {

    @Override
    protected Class<KnowledgeDoc> getEntityClass() {
        return KnowledgeDoc.class;
    }
}
