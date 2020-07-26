package com.wk.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;

/**
 * <p>
 * 
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 加密盐
     */
    private String salt;

    /**
     * 是否启用 0：不可用；1：可用
     */
    private Integer enable;

    /**
     * 令牌 非数据库字段
     */
    @TableField(exist = false)
    private String token;

    /**
     * 角色标识符 非数据库字段
     */
    @TableField(exist = false)
    private HashSet<String> roleCode;

    /**
     * 权限标识符 非数据库字段
     */
    @TableField(exist = false)
    private HashSet<String> resourceCode;

}
