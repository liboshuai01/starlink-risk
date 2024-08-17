package com.liboshuai.starlink.slr.admin.api.dto;

import com.liboshuai.starlink.slr.admin.api.constants.ChannelConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 上送kafka事件错误数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EventErrorDTO implements Serializable {

    private static final long serialVersionUID = 112687514700269556L;

    /**
     * 数据索引
     */
    private Integer index;

    /**
     * 错误原因
     */
    private List<String> reasons;

    /**
     * 错误数据
     */
    private EventUploadDTO eventUploadDTO;

}
