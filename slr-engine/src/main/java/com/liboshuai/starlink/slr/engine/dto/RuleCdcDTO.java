package com.liboshuai.starlink.slr.engine.dto;

import com.liboshuai.starlink.slr.admin.api.dto.risk.RuleJsonDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RuleCdcDTO implements Serializable {

    private static final long serialVersionUID = -5669551177148147064L;
    /**
     * 库名、表名
     */
    private CdcSourceDTO source;
    /**
     * 之前的数据
     */
    private RuleJsonDTO before;
    /**
     * 之后的数据
     */
    private RuleJsonDTO after;
    /**
     * 操作类型：r-查询；u-更新；d-删除；c-创建
     */
    private String op;

}
