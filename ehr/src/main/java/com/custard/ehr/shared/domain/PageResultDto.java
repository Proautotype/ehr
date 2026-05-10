package com.custard.ehr.shared.domain;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResultDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static <T> PageResultDto<T> from(Page<T> page) {

        return new PageResultDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}