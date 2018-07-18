package com.hqs.springboot.entity;

import lombok.Data;
import java.util.Date;
/**
 * Created by Manu on 2018/7/17.
 */
@Data
public class SysUser {
    private Long id;

    private String name;

    private String email;

    private String remark;

    private Integer userStatus;

    private Integer deleteFlag;

    private Date createTime;

    private Long createById;

    private Date updateTime;

    private Long updateById;
}
