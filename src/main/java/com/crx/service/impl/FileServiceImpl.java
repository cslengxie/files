package com.crx.service.impl;

import com.crx.dao.FilesDao;
import com.crx.entity.Files;
import com.crx.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FileServiceImpl implements FilesService {

    @Autowired
    private FilesDao filesDao;

    @Override
    public Files findById(Integer id) {

        return filesDao.findById(id);
    }

    @Override
    public void update(Files files) {
        filesDao.update(files);
    }

    @Override
    public void delete(Integer id) {
        filesDao.delete(id);
    }

    @Override
    public List<Files> findByUserId(Integer id) {
        return filesDao.findByUserId(id);
    }

    @Override
    public void save(Files files) {
        /*
        是否是图片？
        解决方案：当类型中含有image时，说明当前类型一定为图片类型
         */
        String isImg = files.getType().startsWith("image")? "是" : "否";
        files.setIsImg(isImg);
        files.setDownCounts(0);
        files.setUploadTime(new Date());

        filesDao.save(files);
    }


}
