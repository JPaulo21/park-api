package com.jp.parkapi.web.dto.mapper;

import com.jp.parkapi.web.dto.PageableDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

public class PageableMapper {
    public static PageableDto toDto(Page page){
        return new ModelMapper().map(page, PageableDto.class);
    }
}
