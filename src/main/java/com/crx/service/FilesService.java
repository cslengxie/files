package com.crx.service;

import com.crx.entity.Files;

import java.util.List;

public interface FilesService {

    List<Files> findByUserId(Integer id);

    void save(Files files);

    Files findById(Integer id);

    void update(Files files);

    void delete(Integer id);
}
