package org.sang.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.sang.bean.base.InputConverter;
import org.sang.mongodb.document.CategoryDoc;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @ClassName CategoryModel
 * @Description TODO
 * @Author HeX
 * @Date 2022/1/30 19:50
 * @Version 1.0
 **/
@Data
public class CategoryModel implements InputConverter<CategoryDoc> {

    @JsonProperty("_id")
    private ObjectId objectId;

    /**
     * 分类编号
     */
    @JsonProperty("category_no")
    private String categoryNo;

    /**
     * 分类名称
     */
    @JsonProperty("category_name")
    private String categoryName;

    /**
     * 父节点
     */
    @JsonProperty("parent_no")
    private String parentNo;
}
