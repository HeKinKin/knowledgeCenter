package org.sang.utils;

import org.bson.types.ObjectId;
import org.sang.mongodb.document.TagDoc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionUtils {

    public static boolean isEmpty(Collection coll) {
        return (coll == null || coll.isEmpty());
    }

    public static boolean isNotEmpty(Collection coll) {
        return !CollectionUtils.isEmpty(coll);
    }

    /**
     * 过滤没有维护的标签，添加文章并新增标签
     * @param a
     * @param tagDocs
     * @return
     */
    public static List<ObjectId> getDif(List<String> a, List<TagDoc> tagDocs) {
        //查找出a表中不包含b的元素
        return tagDocs.stream().
                filter(m -> !a.contains(m.getObjectId().toString())).map(TagDoc::getObjectId).collect(Collectors.toList());
    }


}
