package com.crx.dao;

import com.crx.entity.Files;

import java.util.List;

public interface FilesDao {

    //根据登录用户的id获取用户的文件列表信息
    List<Files> findByUserId(Integer id);

    void save(Files files);

    Files findById(Integer id);

    void update(Files files);

    void delete(Integer id);
}
