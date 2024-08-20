package com.liboshuai.starlink.slr.engine.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.roaringbitmap.longlong.Roaring64Bitmap;

import java.sql.Timestamp;

/**
 * 规则参数对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleMeta {
    /**
     * cdc操作类型
     */
    private String op;

    /**
     * 规则ID
     */
    private String rule_id;
    /**
     * 规则描述
     */
    private String rule_desc;
    /**
     * 模型ID
     */
    private String model_id;
    /**
     * 规则参数Json
     */
    private String rule_param_json;
    /**
     * 规则状态
     */
    private int status;
    /**
     * 规则作者
     */
    private String rule_author;
    /**
     * 创建时间
     */
    private Timestamp create_time;
    /**
     * 修改时间
     */
    private Timestamp modify_time;
    /**
     * 运算机代码
     */
    private String calculator_code;
    /**
     * 预圈选人群byte数组
     */
    private byte[] pre_select_users;
    /**
     * 预圈选人群bitmap
     */
    private Roaring64Bitmap pre_select_users_bitmap;

}
