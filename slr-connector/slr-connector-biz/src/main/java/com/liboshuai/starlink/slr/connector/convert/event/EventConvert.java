package com.liboshuai.starlink.slr.connector.convert.event;

import com.liboshuai.starlink.slr.admin.api.dto.event.EventDetailDTO;
import com.liboshuai.starlink.slr.admin.api.dto.event.EventKafkaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EventConvert {

    EventConvert INSTANCE = Mappers.getMapper(EventConvert.class);

    List<EventKafkaDTO> batchDetailToKafkaDTO(List<EventDetailDTO> eventDetailDTOList);

}