package org.sang.controller.knowledge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.sang.bean.Article;
import org.sang.bean.RespBean;
import org.sang.bean.model.KnowledgeModel;
import org.sang.mongodb.document.CategoryDoc;
import org.sang.mongodb.document.KnowledgeDoc;
import org.sang.mongodb.document.TagDoc;
import org.sang.response.JsonResult;
import org.sang.response.ResultTool;
import org.sang.service.KnowledgeService;
import org.sang.utils.CollectionUtils;
import org.sang.utils.TransferUtil;
import org.sang.utils.Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    @Autowired
    KnowledgeService knowledgeService;


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public RespBean addNewArticle(@RequestBody String param) throws IOException {
        // String转model
        KnowledgeModel knowledgeModel = TransferUtil.convertString2Model(param,KnowledgeModel.class);
        // 处理一下标签
        KnowledgeDoc result = knowledgeService.addKnowledge(knowledgeModel);
        if (!StringUtils.isEmpty(result.getObjectId())) {
            return new RespBean("success", result + "");
        } else {
            return new RespBean("error", result.getState() == 0 ? "文章保存失败!" : "文章发表失败!");
        }
    }

    @RequestMapping(value = "/admin/all", method = RequestMethod.GET)
    public Map<String, Object> getArticleByStateByAdmin(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count, String keywords) {
        List<HashMap> re = knowledgeService.getKnowledge(Long.valueOf(page.toString()),Long.valueOf(count.toString()),-2,keywords);
        if (CollectionUtils.isEmpty((List) re.get(0).get("data"))) {
            Map<String, Object> map = new HashMap<>();
            map.put("articles",Collections.emptyList());
            map.put("totalCount",0);
            return map;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("articles",re.get(0).get("data"));
        List totals = (List) re.get(0).get("total");
        HashMap countMessage = (HashMap) totals.get(0);
        map.put("totalCount",countMessage.get("count"));
        return map;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public JsonResult getArticleByState(@RequestParam(value = "state", defaultValue = "-1") Integer state, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count,String keywords) {
        List<HashMap> re = knowledgeService.getKnowledge(Long.valueOf(page.toString()),Long.valueOf(count.toString()),state,keywords);
        if (CollectionUtils.isEmpty((List) re.get(0).get("data"))) {
            Map<String, Object> map = new HashMap<>();
            map.put("articles", Collections.emptyList());
            map.put("totalCount",0);
            return ResultTool.success(map);
        }
        Map<String, Object> map = new HashMap<>();
        // 将objectId转为string
        List<HashMap> data = (List<HashMap>) re.get(0).get("data");
        data.stream().forEach(e -> {
            List<CategoryDoc>  cates = (List<CategoryDoc>) e.get("cate_ids");
            List<HashMap> cateMap = cates.stream().map(c -> {
                HashMap cate = new HashMap();
                cate.put("categoryName",c.getCategoryName());
                cate.put("categoryNo",c.getCategoryNo());
                cate.put("id",c.getObjectId().toString());
                cate.put("parentNo",c.getParentNo());
                return cate;
            }).collect(Collectors.toList());
            e.put("cate_ids",cateMap);

            List<TagDoc>  tags = (List<TagDoc>) e.get("tag_ids");
            List<HashMap> tagMaps = tags.stream().map(t -> {
                HashMap tagMap = new HashMap();
                tagMap.put("createTime",t.getCreateTime());
                tagMap.put("id",t.getObjectId().toString());
                tagMap.put("tagName",t.getTagName());
                tagMap.put("tagNo",t.getTagNo());
                return tagMap;
            }).collect(Collectors.toList());
            e.put("tag_ids",tagMaps);

        });
        map.put("articles",data);
        List totals = (List) re.get(0).get("total");
        HashMap countMessage = (HashMap) totals.get(0);
        map.put("totalCount",countMessage.get("count"));
        return ResultTool.success(map);
    }

    /**
     * 根据主键查询知识详情
     * @param aid
     * @return
     */
    @RequestMapping(value = "/detail/{aid}", method = RequestMethod.GET)
    public JsonResult getArticleById(@PathVariable String aid) {
        HashMap hashMap = knowledgeService.getKnowledgeDetails(aid);
        // objectId转string
        List<ObjectId> categoryIds = (List<ObjectId>) hashMap.get("categoryIds");
        List<String> categoryStringIds = categoryIds.stream().map(e ->{
            return e.toString();
        }).collect(Collectors.toList());
        hashMap.put("categoryIds",categoryStringIds);

        List<ObjectId> tagIds = (List<ObjectId>) hashMap.get("tagIds");
        List<String> tagStringIds = tagIds.stream().map(e ->{
            return e.toString();
        }).collect(Collectors.toList());
        hashMap.put("tagIds",tagStringIds);

        return ResultTool.success(hashMap);
    }

    /**
     * 删除知识或放入回收站(批量)
     * @param aids 需操作知识主键
     * @param state 2-回收
     * @return
     */
    @RequestMapping(value = "/dustbin", method = RequestMethod.PUT)
    public JsonResult updateKnowledgeState(String[] aids, Integer state) {
        // string转数组
        List<String> knowledgeIds = Arrays.asList(aids);
        return ResultTool.success(knowledgeService.updateKnowledgeState(knowledgeIds,state));
    }

    /**
     * 还原知识
     * @param articleId
     * @return
     */
    @RequestMapping(value = "/restore", method = RequestMethod.PUT)
    public JsonResult restoreArticle(String articleId) {
        if (knowledgeService.restoreKnowledge(articleId)) {
            return ResultTool.success();
        }
        return ResultTool.fail();
    }

    /**
     * 移动端查询知识
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/mobile", method = RequestMethod.GET)
    @CrossOrigin
    public List<HashMap> getArticleForMobile1(@RequestParam Integer page, @RequestParam Integer count) {
        List<HashMap> re = knowledgeService.getKnowledgeForMobile(Long.valueOf(page.toString()),Long.valueOf(count.toString()));
        re.stream().forEach(e ->{
            e.put("article_type",2);
            e.put("datetime","两天前");
            e.put("image_url","https://vkceyugu.cdn.bspapp.com/VKCEYUGU-dc-site/b4cd3000-517d-11eb-a16f-5b3e54966275.jpg");


            List<HashMap> list = new ArrayList<>();

            HashMap map1 = new HashMap();
            map1.put("url","https://vkceyugu.cdn.bspapp.com/VKCEYUGU-dc-site/b4cd3000-517d-11eb-a16f-5b3e54966275.jpg");
//            map1.put("width",563);
//            map1.put("height",316);

            HashMap map2 = new HashMap();
            map2.put("url","https://vkceyugu.cdn.bspapp.com/VKCEYUGU-dc-site/b4cd3000-517d-11eb-a16f-5b3e54966275.jpg");
            map2.put("width",641);
            map2.put("height",360);

            HashMap map3 = new HashMap();
            map3.put("url","https://vkceyugu.cdn.bspapp.com/VKCEYUGU-dc-site/b7c7f970-517d-11eb-97b7-0dc4655d6e68.jpg");
            map3.put("width",640);
            map3.put("height",360);
            list.add(map1);
//            list.add(map2);
//            list.add(map3);


//            e.put("image_list",list);
            e.put("id",e.get("_id").toString());
            e.remove("_id");
        });
        return re;
    }
}
