package com.wk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("role_resource")
public class RoleResource implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "role_id", type = IdType.INPUT)
    private Integer roleId;

    private Integer resourceId;


}
