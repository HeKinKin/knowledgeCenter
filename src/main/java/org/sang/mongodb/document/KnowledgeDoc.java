package org.sang.mongodb.document;


import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;


/**
 * @ClassName KnowledgeBaseDocument
 * @Description 知识主表-文档,索引只预设了主键
 * @Version: v1
 */
@Data
@Document(collation = "kbs_knowledge")
public class KnowledgeDoc {

    @Id
    @Field("_id")
    private ObjectId objectId;

    /**
     * 标题
     */
    @Field("title")
    private String title;

    /**
     * 主旨
     */
    @Field("summary")
    private String summary;

    /**
     * md文件源码
     */
    @Field("md_content")
    private String mdContent;

    /**
     * html源码
     */
    @Field("html_content")
    private String htmlContent;

    /**
     * 分类id
     */
    private ObjectId categoryId;

    /**
     * 发布时间
     */
    @Field("publish_time")
    private Date publishTime;

    /**
     * 更新时间
     */
    @Field("update_time")
    private Date updateTime;

    /**
     * 浏览量
     */
    @Field("page_views")
    private int pageView;

    /**
     * 状态
     */
    @Field("state")
    private int state;

    @Field("category_ids")
    private List<ObjectId> categoryIds;

    @Field("tag_ids")
    private List<ObjectId> tagIds;

    @Field("author")
    private String author;
}
