package com.hqs.springboot.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Manu on 2018/7/18.
 */
@Data
public class SysModule  implements Serializable {
    private Long id;

    private Long sysSecondNavId;

    private String name;

    private String codeName;

}
