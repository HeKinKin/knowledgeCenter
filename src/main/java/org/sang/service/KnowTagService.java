package org.sang.service;

import org.sang.mongodb.document.TagDoc;

import java.util.List;
import java.util.Map;

/**
 * @ClassName KnowTagService
 * @Description 标签逻辑处理
 * @Author HeX
 * @Date 2022/1/30 23:51
 * @Version 1.0
 **/
public interface KnowTagService {
    /**
     * 新增标签
     * @param tagDoc
     * @return
     */
    String addTag(TagDoc tagDoc);

    /**
     * 获取标签信息
     * @return
     */
    List<Map<String,Object>> getTag();
}
