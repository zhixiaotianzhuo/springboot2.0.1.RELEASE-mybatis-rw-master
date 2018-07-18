package com.hqs.springboot.mapper;

import com.hqs.springboot.entity.SysModule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by Manu on 2018/7/18.
 */
@Mapper
public interface SysModuleMapper {
    Long insert(SysModule record);

    // List<SysModule> searchByUserId(Long userId);

    List<SysModule> searchById(Long userId);
}
