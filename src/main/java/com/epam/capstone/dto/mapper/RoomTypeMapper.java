package com.epam.capstone.dto.mapper;

import com.epam.capstone.model.RoomType;
import com.epam.capstone.dto.RoomTypeDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingConstants;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface RoomTypeMapper {
    RoomType toEntity(RoomTypeDto dto);
    RoomTypeDto toDto(RoomType entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RoomType partialUpdate(RoomTypeDto dto, @MappingTarget RoomType entity);
}
