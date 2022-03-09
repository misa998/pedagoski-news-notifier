package com.misa.dao;

import com.misa.entity.KeywordToAvoid;

import java.util.List;

public interface KeywordsToAvoidDAO {
    List<KeywordToAvoid> getAll();

    int save(String word);
}
