package org.xuanfeng.idphotosbackend.utils;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CoverUtils {

    public static <F, T> PageInfo<T> convertPageInfo(PageInfo<F> from, Class<T> targetClass) {
        if (from == null) {
            return null;
        }
        // 先复制分页信息
        PageInfo<T> to = new PageInfo<>();
        BeanUtils.copyProperties(from, to);

        // 再转换列表
        List<T> targetList = from.getList().stream().map(item -> {
            try {
                T target = targetClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(item, target);
                return target;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        to.setList(targetList);
        return to;
    }
}
