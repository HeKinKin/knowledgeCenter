package org.sang.controller.knowledge;

import com.alibaba.fastjson.JSONArray;
import org.sang.bean.RespBean;
import org.sang.bean.model.CategoryModel;
import org.sang.mongodb.document.CategoryDoc;
import org.sang.service.KnowCategoryService;
import org.sang.utils.CollectionUtils;
import org.sang.utils.TransferUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CategoryController
 * @Description TODO
 * @Author HeX
 * @Date 2022/1/30 19:49
 * @Version 1.0
 **/
@RestController
@RequestMapping("/knowledge/category")
public class KnowCategoryController {

    @Autowired
    KnowCategoryService knowCategoryService;

    /**
     * 新增根结点
     * @param categoryModel
     * @return
     */
    @RequestMapping(value = "/root", method = RequestMethod.POST)
    public RespBean addCategoryRoot(CategoryModel categoryModel) {
        if ("".equals(categoryModel.getCategoryName()) || categoryModel.getCategoryName() == null) {
            return new RespBean("error", "请输入分类名称!!");
        }
        CategoryDoc categoryDoc = TransferUtil.convertDocument(categoryModel, CategoryDoc.class);
        String _id = knowCategoryService.addCategory(categoryDoc);
        if (!StringUtils.isEmpty(_id)) {
            return new RespBean("success", "添加成功!");
        }
        return new RespBean("error", "添加失败!");
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public RespBean addCategory(CategoryModel categoryModel) {
        if ("".equals(categoryModel.getCategoryName()) || categoryModel.getCategoryName() == null) {
            return new RespBean("error", "请输入分类名称!!");
        }
        CategoryDoc categoryDoc = categoryModel.convertTo();
//        CategoryDoc categoryDoc = TransferUtil.convertDocument(categoryModel, CategoryDoc.class);
        String _id = knowCategoryService.addCategory(categoryDoc);
        if (!StringUtils.isEmpty(_id)) {
            return new RespBean("success", "添加成功!");
        }
        return new RespBean("error", "添加失败!");
    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public JSONArray getCategoryTree() {
        JSONArray result = knowCategoryService.getCategoryTree();
        if (CollectionUtils.isNotEmpty(result)) {
            return result;
        }
        return new JSONArray();
    }

    /**
     * 获取分类信息
     * @return
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Map<String, Object>> getCategoryAll() {
        List<Map<String, Object>> result = knowCategoryService.getCategoryDocs();
        if (CollectionUtils.isNotEmpty(result)) {
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * 删除分类
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public JSONArray deleteCategoryTree() {
        JSONArray result = knowCategoryService.getCategoryTree();
        if (CollectionUtils.isNotEmpty(result)) {
            return result;
        }
        return new JSONArray();
    }
}
