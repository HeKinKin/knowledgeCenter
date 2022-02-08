package org.sang.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.sang.bean.base.InputConverter;
import org.sang.mongodb.document.KnowledgeDoc;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * 知识model
 */
@Data
public class KnowledgeModel implements InputConverter<KnowledgeDoc> {

    @JsonProperty("_id")
    private String objectId;

    /**
     * 标题
     */
    @JsonProperty("title")
    private String title;

    /**
     * 主旨
     */
    @JsonProperty("summary")
    private String summary;

    /**
     * md文件源码
     */
    @JsonProperty("md_content")
    private String mdContent;

    /**
     * html源码
     */
    @JsonProperty("html_content")
    private String htmlContent;

    /**
     * 状态
     */
    @JsonProperty("state")
    private int state;

    /**
     * 2月1日洗澡能-分类集合
     */
    @JsonProperty("category_ids")
    private List<ObjectId> categoryIds;

    /**
     * 2月1日新增-已维护标签 集合
     */
    @JsonProperty("tag_ids")
    private List<ObjectId> tagIds;

    /**
     * 2月1日新增-自由打标签 集合
     */
    @JsonProperty("freeTag_names")
    private List<String> freeTagNames;

    /**
     * 2.4新增
     */
    @JsonProperty("author")
    private String author;

}
