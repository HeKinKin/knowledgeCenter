package org.sang.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransferUtil {

    /**
     * List-model转entity
     * @param sourceList
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> convertList(List sourceList,Class<T> clazz){
        if (CollectionUtils.isNotEmpty(sourceList)) {
            return (List<T>)sourceList.stream().map(source ->{
                try {
                    T target = clazz.newInstance();
                    BeanUtils.copyProperties(source,target);
                    return target;
                }catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 单个model转entity
     * @param source
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T convertDocument(Object source , Class<T> clazz){
        if (null != source) {
            try {
                T target = clazz.newInstance();
                BeanUtils.copyProperties(source,target);
                return target;
            }catch (Exception e){
                e.printStackTrace();;
                return null;
            }
        }
        return null;
    }

    /**
     * string转model
     * @param param 入参string
     * @param clazz 泛型
     * @param <T> model
     * @return
     */
    public static <T> T convertString2Model(String param,Class<T> clazz){
        // 接收入参，转model
        try {
            return new ObjectMapper().readValue(param.getBytes(),clazz);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
