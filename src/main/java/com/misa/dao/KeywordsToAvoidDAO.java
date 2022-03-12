package com.misa.dao;

import com.misa.entity.KeywordToAvoid;

import java.math.BigDecimal;
import java.util.List;

import lombok.NonNull;

public interface KeywordsToAvoidDAO {
    List<KeywordToAvoid> getAll();

    BigDecimal save(@NonNull String word);
}
