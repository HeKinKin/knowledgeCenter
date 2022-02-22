package org.sang.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.sang.bean.model.KnowledgeModel;
import org.sang.mongodb.dao.KnowledgeMongoDao;
import org.sang.mongodb.dao.TagDao;
import org.sang.mongodb.document.KnowledgeDoc;
import org.sang.mongodb.document.TagDoc;
import org.sang.service.KnowledgeService;
import org.sang.utils.CollectionUtils;
import org.sang.utils.TransferUtil;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    KnowledgeMongoDao knowledgeMongoDao;

    @Autowired
    TagDao tagDao;

    @Autowired
    MongoTemplate mongoTemplate;


    @Override
    public KnowledgeDoc addKnowledge(KnowledgeModel knowledgeModel) {
        // 处理标签
        handTag(knowledgeModel);
        // model转entity
        KnowledgeDoc knowledgeDoc = knowledgeModel.convertTo();
        //处理文章摘要
        handSummary(knowledgeDoc);
        // 处理新增操作
        if (StringUtils.isEmpty(knowledgeDoc.getObjectId())) {
            //添加操作
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (knowledgeDoc.getState() == 1) {
                //设置发表日期
                knowledgeDoc.setPublishTime(timestamp);
            }
            knowledgeDoc.setAuthor(Util.getCurrentUser().getUsername());
            knowledgeDoc.setUpdateTime(timestamp);
            // 处理标签
            KnowledgeDoc doc = mongoTemplate.save(knowledgeDoc);
            return doc;
        }
        // 处理修改操作
        // 更新数据
        Query query = new Query(Criteria.where("_id").is(knowledgeDoc.getObjectId()));
//        Update update = knowledgeMongoDao.getUpdateByObject(knowledgeDoc);
//        Update update = new Update().set("state",2);
        Update update = new Update().set("category_ids",knowledgeDoc.getCategoryIds())
                .set("title",knowledgeDoc.getTitle())
                .set("tag_ids",knowledgeDoc.getTagIds())
                .set("update_time",new Timestamp(System.currentTimeMillis()))
                .set("md_content",knowledgeDoc.getMdContent())
                .set("html_content",knowledgeDoc.getHtmlContent());
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,"knowledgeDoc");
        return knowledgeDoc;
    }

    @Override
    public List<HashMap> getKnowledge(Long page,Long size,int state,String content) {
        LookupOperation lookup = LookupOperation.newLookup()
                //从表（关联的表）
                .from("kbs_category")
                //主表中与从表相关联的字段
                .localField("category_ids")
                //从表与主表相关联的字段
                .foreignField("_id")
                //查询出的从表集合 命名
                .as("cate_ids");
        Pattern pattern = Pattern.compile("^.*" + content + ".*$", Pattern.CASE_INSENSITIVE);
        Criteria criteria;
        if (state == -2) {
            criteria = Criteria.where("state").is(1);
        }else if (state == -1) {
            criteria = new Criteria();
        }else{
            criteria = Criteria.where("state").is(state).and("author").is(Util.getCurrentUser().getUsername());
        }
        criteria.orOperator(Criteria.where("title").is(pattern));

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.facet(count().as("count")).as("total")
                            .and(match(criteria),skip((page-1)*size),limit(size),lookup,lookup("kbs_tag","tag_ids","_id","tag_ids")
                                    ,project("title","author","update_time","cate_ids","tag_ids","state").andExpression("toString(_id)").as("id")
                                    .and("tag_ids.tag_name").as("tag_name")).as("data")
                );
        AggregationResults<HashMap> user = mongoTemplate.aggregate(agg, "knowledgeDoc", HashMap.class);
        return user.getMappedResults();
    }

    @Override
    public HashMap getKnowledgeDetails(String _id) {
        Aggregation agg = Aggregation.newAggregation(
                match(Criteria.where("_id").is(new ObjectId(_id))),
                Aggregation.lookup("kbs_category","category_ids","_id","cate_ids"),
                Aggregation.lookup("kbs_tag","tag_ids","_id","tag_ids"),
                Aggregation.project("title","author","tag_ids","cate_ids").and("update_time").as("updateTime").and("md_content").as("mdContent")
                        .and("html_content").as("htmlContent").and("page_views").as("pageView")
                        .and("cate_ids.category_name").as("categoryNames")
                        .and("cate_ids._id").as("categoryIds")
                        .and("tag_ids._id").as("tagIds")
                        .andExpression("toString(_id)").as("id")
        );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(agg,"knowledgeDoc",HashMap.class);
        // 浏览量＋1
        if (CollectionUtils.isNotEmpty(results.getMappedResults())) {
            HashMap hashMap = results.getMappedResults().get(0);
            int view = Integer.valueOf(hashMap.get("pageView").toString());
            Query query = Query.query(Criteria.where("_id").is(new ObjectId(_id)));
            Update update = new Update();
            update.set("page_views", view+1);
            mongoTemplate.updateFirst(query, update,"knowledgeDoc");
        }
        return CollectionUtils.isNotEmpty(results.getMappedResults()) ? results.getMappedResults().get(0) : null;
    }

    @Override
    public List<HashMap> getKnowledgeForMobile(Long page, Long size) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.skip((page-1)*size),limit(size),
                Aggregation.project("title").and("update_time").as("datetime").and("author").as("source").and("page_views").as("comment_count")
                );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(agg,"knowledgeDoc",HashMap.class);
        return results.getMappedResults();
    }

    @Override
    public boolean updateKnowledgeState(List<String> kids, int state) {
        if (state == 2) {
            // 批量删除
            knowledgeMongoDao.batchDeleteById(kids,"knowledgeDoc");
            return true;
        }
        //还原
        Query query = new Query(Criteria.where("_id").in(kids));
        Update update = new Update().set("state",2);
        UpdateResult updateResult = mongoTemplate.updateMulti(query,update,"knowledgeDoc");
        return true;
    }

    @Override
    public boolean restoreKnowledge(String kid) {
        Query query = new Query(Criteria.where("_id").is(kid));
        Update update = new Update().set("state",1);
        mongoTemplate.updateMulti(query,update,"knowledgeDoc");
        return true;
    }

    /**
     * 处理标签
     * @param knowledgeModel
     */
    public void handTag(KnowledgeModel knowledgeModel){
        // 处理标签
        if (CollectionUtils.isNotEmpty(knowledgeModel.getFreeTagNames())) {
            Criteria criteria = Criteria.where("tag_name").in(knowledgeModel.getFreeTagNames());
            List<TagDoc> tagDocs = tagDao.queryListByCriteria(criteria);
            List<ObjectId> tagIds = knowledgeModel.getTagIds();
            tagIds.addAll(CollectionUtils.getDif(knowledgeModel.getFreeTagNames(),tagDocs));
            knowledgeModel.setTagIds(tagIds);
        }
    }

    /**
     * 处理摘要
     * @param knowledgeDoc
     */
    public void handSummary(KnowledgeDoc knowledgeDoc){
        if (knowledgeDoc.getSummary() == null || "".equals(knowledgeDoc.getSummary())) {
            //直接截取
            String stripHtml = stripHtml(knowledgeDoc.getHtmlContent());
            knowledgeDoc.setSummary(stripHtml.substring(0, stripHtml.length() > 50 ? 50 : stripHtml.length()));
        }
    }

    public String stripHtml(String content) {
        content = content.replaceAll("<p .*?>", "");
        content = content.replaceAll("<br\\s*/?>", "");
        content = content.replaceAll("\\<.*?>", "");
        return content;
    }
}
