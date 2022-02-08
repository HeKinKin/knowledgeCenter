package org.sang.bean.base;

import org.sang.utils.BeanUtils;
import org.sang.utils.ReflectionUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * @ClassName InputConverter
 * @Description TODO
 * @Author HeX
 * @Date 2022/2/3 16:34
 * @Version 1.0
 **/
public interface InputConverter<D> {
    /**
     * Convert to domain.(shallow)
     *
     * @return new domain with same value(not null)
     */
    @SuppressWarnings("unchecked")
    default D convertTo() {
        // Get parameterized type
        ParameterizedType currentType = parameterizedType();

        // Assert not equal
        Objects.requireNonNull(currentType,
                "Cannot fetch actual type because parameterized type is null");

        Class<D> domainClass = (Class<D>) currentType.getActualTypeArguments()[0];

        return BeanUtils.transformFrom(this, domainClass);
    }

    /**
     * Update a domain by dto.(shallow)
     *
     * @param domain updated domain
     */
    default void update(D domain) {
        BeanUtils.updateProperties(this, domain);
    }

    /**
     * Get parameterized type.
     *
     * @return parameterized type or null
     */
    @Nullable
    default ParameterizedType parameterizedType() {
        return ReflectionUtils.getParameterizedType(InputConverter.class, this.getClass());
    }
}
