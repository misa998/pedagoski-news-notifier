package com.misa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class KeywordToAvoid {
    private final int id;
    private final String word;
}
