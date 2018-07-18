package com.hqs.springboot.mapper;

import com.hqs.springboot.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by Manu on 2018/7/18.
 */
@Mapper
public interface SysUserMapper {
//    int insert(SysUser sysUser);
//
//    int update(SysUser sysUser);
//
//    List<SysUser> selectAll();

    SysUser selectByEmail(String email);

//    int updateStatus(long id);
}
