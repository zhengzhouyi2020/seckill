package com.zzy.seckill.vo;

import com.zzy.seckill.validator.IsMobile;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginVo {
    @NonNull
    @IsMobile(requried = false)
    private String mobile;
    @NonNull
    @Length(min=32)
    private String password;


}
