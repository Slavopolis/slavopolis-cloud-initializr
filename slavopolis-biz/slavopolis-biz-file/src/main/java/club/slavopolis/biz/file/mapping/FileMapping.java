package club.slavopolis.biz.file.mapping;

import club.slavopolis.base.mapping.BaseMapping;
import club.slavopolis.biz.file.dto.FileInfoDTO;
import club.slavopolis.file.domain.FileInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * 文件映射器实现
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/5
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FileMapping extends BaseMapping<FileInfo, FileInfoDTO> {

}
