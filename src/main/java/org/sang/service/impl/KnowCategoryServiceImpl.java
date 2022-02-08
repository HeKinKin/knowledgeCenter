package org.sang.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.bson.types.ObjectId;
import org.sang.mongodb.dao.CategoryDao;
import org.sang.mongodb.document.CategoryDoc;
import org.sang.mongodb.support.ObjectIdSerializer;
import org.sang.service.KnowCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName KnowCategoryServiceImpl
 * @Description 分类逻辑处理
 * @Author HeX
 * @Date 2022/1/30 19:40
 * @Version 1.0
 **/
@Service
public class KnowCategoryServiceImpl implements KnowCategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public String addCategory(CategoryDoc categoryDoc) {
        // 获取最新的编号
        Query query =new Query();
        query.with(Sort.by(
                Sort.Order.desc("category_no")
        ));
        CategoryDoc doc = mongoTemplate.findOne(query,CategoryDoc.class);
        if (null == doc ) {
            // 第一笔分类，默认为0
            categoryDoc.setCategoryNo("1");
        }else {
            // 获取最大编号+1，得到新增的分类编号
            int num = Integer.valueOf(doc.getCategoryNo())+1;
            categoryDoc.setCategoryNo(String.valueOf(num));
        }
        categoryDao.save(categoryDoc);
        return categoryDoc.getObjectId().toString();
    }

    @Override
    public JSONArray getCategoryTree() {
        List<CategoryDoc> list = mongoTemplate.find(new Query(),CategoryDoc.class);
        return treeRecursionDataList(list,"");
    }

    @Override
    public List<Map<String, Object>> getCategoryDocs() {
        List<CategoryDoc> categoryDocs =  mongoTemplate.find(new Query(),CategoryDoc.class);
        List<Map<String, Object>> resultList = categoryDocs.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getObjectId().toString());
            map.put("categoryName", m.getCategoryName());
            map.put("categoryNo", m.getCategoryNo());
            return map;
        }).collect(Collectors.toList());
        return resultList;
    }

    /**
     * 递归获取分类组织树
     * @param categoryDocs
     * @param parentId
     * @return
     */
    public JSONArray treeRecursionDataList(List<CategoryDoc> categoryDocs, String parentId) {
        JSONArray childMenu = new JSONArray();
        for (CategoryDoc categoryDoc : categoryDocs) {
            String categoryNo = categoryDoc.getCategoryNo();
            String parentNo = categoryDoc.getParentNo();
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("id",categoryNo);
            jsonResult.put("label",categoryDoc.getCategoryName());
            if (parentId.equals(parentNo)) {
                JSONArray c_node = treeRecursionDataList(categoryDocs, categoryNo);
                jsonResult.put("children", c_node);
                childMenu.add(jsonResult);
            }
        }
        return childMenu;
    }
}
