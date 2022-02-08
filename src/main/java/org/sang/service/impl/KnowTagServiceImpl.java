package org.sang.service.impl;

import org.sang.mongodb.dao.TagDao;
import org.sang.mongodb.document.CategoryDoc;
import org.sang.mongodb.document.TagDoc;
import org.sang.service.KnowTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName KnowTagServiceImpl
 * @Description 标签服务
 * @Author HeX
 * @Date 2022/1/30 23:52
 * @Version 1.0
 **/
@Service
public class KnowTagServiceImpl implements KnowTagService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    TagDao tagDao;

    @Override
    public String addTag(TagDoc tagDoc) {
        // 校验标签是否重名
        if (!checkTagName(tagDoc.getTagName())) {
            throw new RuntimeException("标签名重复了");
        }
        // 获取最新的编号
        Query query =new Query();
        query.with(Sort.by(
                Sort.Order.desc("category_no")
        ));
        // 获取最大编号
        TagDoc doc = mongoTemplate.findOne(query,TagDoc.class);
        String no = doc==null ?  "0" : String.valueOf(Integer.valueOf(doc.getTagNo())+1);
        // 落存
        tagDoc.setCreateTime(new Date());
        tagDoc.setTagNo(no);
        TagDoc tag = tagDao.save(tagDoc);
        return tag.getObjectId().toString();
    }

    @Override
    public List<Map<String, Object>> getTag() {
        List<TagDoc> tagDocs = tagDao.getAll();
        List<Map<String, Object>> resultList = tagDocs.stream().map( m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id",m.getObjectId().toString());
            map.put("tagName",m.getTagName());
            map.put("tagNo",m.getTagNo());
            map.put("date",m.getCreateTime());
            return map;
        }).collect(Collectors.toList());
        return resultList;
    }

    /**
     * 校验标签名称是否重复
     * @param name
     * @return
     */
    private boolean checkTagName(String name){
        Query query = Query.query(Criteria.where("tag_name").is(name));
        TagDoc doc = mongoTemplate.findOne(query, TagDoc.class);
        return doc == null ? true : false;
    }
}
