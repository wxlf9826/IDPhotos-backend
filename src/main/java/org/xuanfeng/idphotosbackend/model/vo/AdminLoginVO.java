package org.xuanfeng.idphotosbackend.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLoginVO {
    private String token;
}
