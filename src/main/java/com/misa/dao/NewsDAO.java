package com.misa.dao;

import com.misa.entity.News;

import java.util.List;
import java.util.Optional;

public interface NewsDAO {
    Optional<News> getById(int id);
    Optional<News> getByNewsId(int id);
    List<News> getAll();
    News getLatest();
    int save(News news);
}
