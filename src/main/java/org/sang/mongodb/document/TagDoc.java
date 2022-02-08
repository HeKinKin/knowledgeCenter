package org.sang.mongodb.document;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @ClassName TagDoc
 * @Description 标签文档
 * @Author HeX
 * @Date 2022/1/30 23:49
 * @Version 1.0
 **/
@Data
@Document(collection = "kbs_tag")
public class TagDoc {

    @Id
    @Field("_id")
    private ObjectId objectId;

    /**
     * 标签编号
     */
    @Field("tag_no")
    private String tagNo;

    /**
     * 标签名称
     */
    @Field("tag_name")
    private String tagName;

    /**
     * 创建时间
     */
    @Field("create_time")
    private Date createTime;
}
