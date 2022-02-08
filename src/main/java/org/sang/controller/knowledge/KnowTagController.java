package org.sang.controller.knowledge;

import org.sang.bean.RespBean;
import org.sang.bean.model.CategoryModel;
import org.sang.bean.model.TagModel;
import org.sang.mongodb.document.CategoryDoc;
import org.sang.mongodb.document.TagDoc;
import org.sang.service.KnowTagService;
import org.sang.utils.TransferUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @ClassName KnowTagController
 * @Description 标签 controller
 * @Author HeX
 * @Date 2022/1/31 0:08
 * @Version 1.0
 **/
@RestController
@RequestMapping("/knowledge/tag")
public class KnowTagController {

    @Autowired
    KnowTagService knowTagService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public RespBean addTag(TagModel tagModel) {
        if ("".equals(tagModel.getTagName()) || tagModel.getTagName() == null) {
            return new RespBean("error", "请输入标签名称!!");
        }
        TagDoc tagDoc = TransferUtil.convertDocument(tagModel, TagDoc.class);
        String _id = knowTagService.addTag(tagDoc);
        if (!StringUtils.isEmpty(_id)) {
            return new RespBean("success", "添加成功!");
        }
        return new RespBean("error", "添加失败!");
    }

    /**
     * 查询所有标签
     * @param
     * @return
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Map<String,Object>> getTag() {
        List<Map<String,Object>> result  = knowTagService.getTag();
        return result;
    }


}
