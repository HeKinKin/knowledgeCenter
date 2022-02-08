package org.sang.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @ClassName TagModel
 * @Description tag - model
 * @Author HeX
 * @Date 2022/1/31 0:09
 * @Version 1.0
 **/
@Data
public class TagModel {

    @JsonProperty("id")
    private String objectId;
    /**
     * 标签编号
     */
    @JsonProperty("tag_no")
    private String tagNo;

    /**
     * 标签名称
     */
    @JsonProperty("tag_name")
    private String tagName;

}
