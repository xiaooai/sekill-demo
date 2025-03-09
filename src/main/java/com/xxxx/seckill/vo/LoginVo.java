package com.xxxx.seckill.vo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.xxxx.seckill.validator.IsMobile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;
}
