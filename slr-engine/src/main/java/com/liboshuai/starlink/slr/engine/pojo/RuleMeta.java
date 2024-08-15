package com.liboshuai.starlink.slr.engine.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.roaringbitmap.longlong.Roaring64Bitmap;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleMeta {

    private String op;

    private String rule_id;
    private String rule_desc;
    private String model_id;
    private String rule_param_json;
    private int status;
    private String rule_author;
    private Timestamp create_time;
    private Timestamp modify_time;

    private String calculator_code;

    private byte[] pre_select_users;

    private Roaring64Bitmap pre_select_users_bitmap;


}
