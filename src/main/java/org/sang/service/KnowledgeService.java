package org.sang.service;

import org.sang.bean.model.KnowledgeModel;
import org.sang.mongodb.document.KnowledgeDoc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识相关操作
 */
public interface KnowledgeService {
    /**
     * 新增文章，处理标签
     * @param knowledgeModel
     * @return
     */
    KnowledgeDoc addKnowledge(KnowledgeModel knowledgeModel);

    /**
     * 查询文章信息
     * @return
     */
    List<HashMap> getKnowledge(Long page,Long size,int state,String content);

    /**
     * 根据主键查询文章信息
     * @param _id
     * @return
     */
    HashMap getKnowledgeDetails(String _id);

    /**
     * 查询文章 for 移动端
     * @param page
     * @param size
     * @return
     */
    List<HashMap> getKnowledgeForMobile(Long page,Long size);

}
