package com.misa.dao;

import com.misa.entity.News;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import lombok.NonNull;

public interface NewsDAO {
    Optional<News> getById(@NonNull BigDecimal id);

    Optional<News> getByNewsId(@NonNull BigDecimal id);

    List<News> getAll();

    BigDecimal save(@NonNull News news);

    List<BigDecimal> save(List<News> news);

    List<News> getByIds(List<BigDecimal> newsIds);
}
