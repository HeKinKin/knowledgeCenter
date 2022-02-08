package org.sang.service;

import com.alibaba.fastjson.JSONArray;
import org.sang.mongodb.document.CategoryDoc;

import java.util.List;
import java.util.Map;

/**
 * @ClassName KnowCategoryService
 * @Description 只是分类
 * @Author HeX
 * @Date 2022/1/30 19:40
 * @Version 1.0
 **/
public interface KnowCategoryService {
    /**
     * 新增分类
     * @param categoryDoc
     * @return
     */
    String addCategory(CategoryDoc categoryDoc);

    /**
     * 获取分类树状结构
     * @return
     */
    JSONArray getCategoryTree();

    /**
     * 获取分类信息
     * @return
     */
    List<Map<String, Object>> getCategoryDocs();


}
