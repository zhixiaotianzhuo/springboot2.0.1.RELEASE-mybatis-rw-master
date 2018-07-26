package com.hqs.springboot.constants;

/**
 * Created by Manu on 2018/7/25.
 */
public class ModuleNameCollection {
    public static final String AuthUserController_list = "auth:user:list";
    public static final String AuthUserController_get = "auth:user:get";
    public static final String AuthUserController_save = "auth:user:save";
    public static final String AuthUserController_update = "auth:user:update";
    public static final String AuthUserController_getDataGroup = "auth:user:getDataGroup";
    public static final String AuthUserController_updateStatus = "auth:user:updateStatus";

    // 机构管理
    public static final String DepController_insert = "sys:dep:insert";
    public static final String DepController_update = "sys:dep:update";
    public static final String DepController_search = "sys:dep:view";
    public static final String DepController_getTree = "sys:dep:view";
    public static final String DepController_delete = "sys:dep:delete";

    // 功能角色管理
    public static final String RoleController_moduleSearch = "sys:role:view";
    public static final String RoleController_getTree = "sys:role:view";
    public static final String RoleController_insert = "sys:role:insert";
    public static final String RoleController_update = "sys:role:update";
    public static final String RoleController_search = "sys:role:view";
    public static final String RoleController_delete = "sys:role:delete";
}

