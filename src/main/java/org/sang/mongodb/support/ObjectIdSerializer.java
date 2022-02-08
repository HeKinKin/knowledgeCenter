package org.sang.mongodb.support;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ObjectIdSerializer
 * @Description TODO
 * @Author HeX
 * @Date 2022/1/30 22:59
 * @Version 1.0
 **/
public class ObjectIdSerializer implements ObjectSerializer, ObjectDeserializer {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        if ("java.util.List<org.bson.types.ObjectId>".equals(type.getTypeName())){
            return (T) MongoUtil.toObjectIds(parser.parseArray(String.class));
        }else {
            return (T) new ObjectId(parser.parseObject(String.class));
        }
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if (object instanceof List) {
            List<ObjectId> ids = (List<ObjectId>) object;
            serializer.write(ids.stream().map(objectId -> objectId.toString()).collect(Collectors.toList()));
        } else {
            ObjectId id = (ObjectId) object;
            serializer.write(id.toString());
        }
    }
}
