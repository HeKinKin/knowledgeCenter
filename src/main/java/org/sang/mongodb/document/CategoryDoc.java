package org.sang.mongodb.document;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @ClassName CategoryDoc
 * @Description TODO
 * @Author HeX
 * @Date 2022/1/30 19:36
 * @Version 1.0
 **/
@Data
@Document(collection = "kbs_category")
public class CategoryDoc {

    @Id
    @Field("_id")
    private ObjectId objectId;

    /**
     * 分类编号
     */
    @Field("category_no")
    private String categoryNo;

    /**
     * 分类名称
     */
    @Field("category_name")
    private String categoryName;

    /**
     * 父节点
     */
    @Field("parent_no")
    private String parentNo;

}
