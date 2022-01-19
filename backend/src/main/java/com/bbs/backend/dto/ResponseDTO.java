package com.bbs.backend.dto;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private String error;
    private List<T> data;
}
