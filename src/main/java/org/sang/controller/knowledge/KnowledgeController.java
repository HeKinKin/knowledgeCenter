package org.sang.controller.knowledge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sang.bean.Article;
import org.sang.bean.RespBean;
import org.sang.bean.model.KnowledgeModel;
import org.sang.mongodb.document.KnowledgeDoc;
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

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    @Autowired
    KnowledgeService knowledgeService;

//    @RequestMapping(value = "/", method = RequestMethod.POST)
//    public RespBean addNewArticle(KnowledgeModel knowledgeModel) {
//        // model转entity
//        KnowledgeDoc knowledgeDoc = TransferUtil.convertDocument(knowledgeModel,KnowledgeDoc.class);
//        String result = knowledgeService.addKnowledge(knowledgeDoc);
//        if (!StringUtils.isEmpty(result)) {
//            return new RespBean("success", result + "");
//        } else {
//            return new RespBean("error", knowledgeDoc.getState() == 0 ? "文章保存失败!" : "文章发表失败!");
//        }
//    }

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
    public Map<String, Object> getArticleByState(@RequestParam(value = "state", defaultValue = "-1") Integer state, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count,String keywords) {
        List<HashMap> re = knowledgeService.getKnowledge(Long.valueOf(page.toString()),Long.valueOf(count.toString()),state,keywords);
        if (CollectionUtils.isEmpty((List) re.get(0).get("data"))) {
            Map<String, Object> map = new HashMap<>();
            map.put("articles", Collections.emptyList());
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

    /**
     * 根据主键查询知识详情
     * @param aid
     * @return
     */
    @RequestMapping(value = "/{aid}", method = RequestMethod.GET)
    public HashMap getArticleById(@PathVariable String aid) {
        return knowledgeService.getKnowledgeDetails(aid);
    }

    @RequestMapping(value = "/mobile", method = RequestMethod.GET)
    @CrossOrigin
    public List<HashMap> getArticleForMobile1(@RequestParam Integer page, @RequestParam Integer count) {
        List<HashMap> re = knowledgeService.getKnowledgeForMobile(Long.valueOf(page.toString()),Long.valueOf(count.toString()));
        re.stream().forEach(e ->{
            e.put("article_type","2");
            List<HashMap> list = new ArrayList<>();

            HashMap map1 = new HashMap();
            map1.put("url","https://vkceyugu.cdn.bspapp.com/VKCEYUGU-dc-site/b2e201d0-517d-11eb-8a36-ebb87efcf8c0.jpg");
            map1.put("width",563);
            map1.put("height",316);

            HashMap map2 = new HashMap();
            map2.put("url","https://vkceyugu.cdn.bspapp.com/VKCEYUGU-dc-site/b4cd3000-517d-11eb-a16f-5b3e54966275.jpg");
            map2.put("width",641);
            map2.put("height",360);

            HashMap map3 = new HashMap();
            map3.put("url","https://vkceyugu.cdn.bspapp.com/VKCEYUGU-dc-site/b7c7f970-517d-11eb-97b7-0dc4655d6e68.jpg");
            map3.put("width",640);
            map3.put("height",360);
            list.add(map1);
            list.add(map2);
            list.add(map3);


            e.put("image_list",list);
            e.put("id",e.get("_id").toString());
            e.remove("_id");
        });
        return re;
    }
}
