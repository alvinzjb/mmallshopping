package com.njust.service.Impl;

import com.google.common.collect.Lists;
import com.njust.service.IFileService;
import com.njust.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by alvin
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {
     //日志
    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    //MultipartFile多文件上传
    public String upload(MultipartFile file,String path){
        //获取原文件名
        String fileName = file.getOriginalFilename();
        //扩展名
        //abc.jpg
        //获取文件的扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //生成新的文件名
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);
         //如果没有则生成文件目录
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);


        try {
            //上传文件
            file.transferTo(targetFile);
            //文件已经上传成功了

            //上传到ftp服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到ftp服务器上

           //删除服务器上的upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        //A:abc.jpg
        //B:abc.jpg
        return targetFile.getName();
    }

}
