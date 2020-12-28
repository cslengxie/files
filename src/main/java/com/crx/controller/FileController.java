package com.crx.controller;

import com.crx.entity.Files;
import com.crx.service.FilesService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FilesService filesService;

    @Value("${upload.dir}")
    private String uploadPath;
    private String dateFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());
    //这里是注释

    @GetMapping("/showAllJSON")
    @ResponseBody
    public List<Files> findAllJSON(HttpSession session){
        List<Files> files = filesService.findByUserId((Integer) session.getAttribute("userId"));
        return files;
    }

    @DeleteMapping("/delete")
    public String delete(Integer id) throws FileNotFoundException {
        //根据id查询信息
        Files files = filesService.findById(id);
        //删除文件
        String realpath = uploadPath + "/files/" + dateFormat;
        File file = new File(realpath, files.getNewFileName());
        if(file.exists()){
            file.delete();
            //删除数据库中的记录
            filesService.delete(id);
        }

        return "redirect:/file/showAll";
    }

    @GetMapping("/download")
    public void download(String openStyle,Integer id, HttpServletResponse response) throws IOException {
        openStyle = openStyle == null ? "attachment":"inline";
        //获取文件信息
        Files files = filesService.findById(id);
        //点击下载链接 更新下载次数
        if("attachment".equals(openStyle)){
            files.setDownCounts(files.getDownCounts() + 1);
            filesService.update(files);
        }
        //根据文件信息中文件名字和文件存储路径获取文件输入流
        String realPath = uploadPath + "/files/" + dateFormat;
        //获取文件输入流
        FileInputStream in = new FileInputStream(new File(realPath, files.getNewFileName()));
        //附件下载
        response.setHeader("content-disposition",openStyle + ";fileName="
                + URLEncoder.encode(files.getOldFileName(),"UTF-8"));
        //获取响应输出流
        ServletOutputStream out = response.getOutputStream();
        //文件拷贝
        IOUtils.copy(in,out);
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
    }



    @PostMapping("/upload")
    public String upload(MultipartFile aaa,HttpSession session) throws IOException {
        //获取文件原始名称
        String oldFileName = aaa.getOriginalFilename();
        //获取文件的后缀
        String extension = "." + FilenameUtils.getExtension(aaa.getOriginalFilename());
        //生成新的文件名称
        String newFileName = new Date().getTime() + UUID.randomUUID().toString().replace("-","")
                + extension;
        //文件的大小
        long size = aaa.getSize();
        //文件类型
        String type = aaa.getContentType();
        //处理根据日期生成目录
        //String realPath = ResourceUtils.getURL("classpath:").getPath() + "static/files";
        String dateDirPath = uploadPath + "/files/" + dateFormat;
        File dateDir = new File(dateDirPath);
        if(!dateDir.exists()){
            dateDir.mkdirs();
        }
        //处理文件上传
        aaa.transferTo(new File(dateDir,newFileName));

        //将文件信息保存数据库
        Files files = new Files();
        files.setOldFileName(oldFileName).setNewFileName(newFileName).setExt(extension).setSize(String.valueOf(size))
                .setType(type).setPath("/files/" + dateFormat).setUserId((Integer) session.getAttribute("userId"));
        filesService.save(files);
        return "redirect:/file/showAll";
    }

    @GetMapping("/showAll")
    public String findAll(HttpSession session, Model model){
        List<Files> files = filesService.findByUserId((Integer) session.getAttribute("userId"));
        model.addAttribute("files",files);
        return "showAll";
    }
}
