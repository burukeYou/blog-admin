package com.myblog.util;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class AliyunOssClientUtil {

    //OSS客户端
    private volatile static OSSClient instance;

    //OSS 的地址
    private final static String OSS_END_POINT = "http://xxxx.com";
    //OSS 的key值
    private final static String OSS_ACCESS_KEY_ID = "xx";
    //OSS 的secret值
    private final static String OSS_ACCESS_KEY_SECRET = "xxx";
    //OSS 的bucket名字
    private final static String OSS_BUCKET_NAME = "xxx";
    //设置URL过期时间为10年
    private final static Date OSS_URL_EXPIRATION = DateUtils.addDays(new Date(), 365 * 10);

    //文件路径的枚举
    public enum FileDirType {
        blogImg("blogImg/"),
        avatar("userAvatar/");

        private String dir;

        FileDirType(String dir) {
            this.dir = dir;
        }

        @JsonValue
        public String getDir() {
            return dir;
        }
    }

    //================================================================================================================


    private AliyunOssClientUtil() {
    }

 
    private static OSSClient getOSSClient() {
        if (instance == null) {
            synchronized (AliyunOssClientUtil.class) {
                if (instance == null) {
                    instance = (OSSClient) new OSSClientBuilder().build(OSS_END_POINT, OSS_ACCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);
                }
            }
        }
        return instance;
    }



    /**
     * 1 - 当Bucket不存在时创建Bucket
     *
     Bucket命名规则：
     *                         1.只能包含小写字母、数字和短横线，
     *                         2.必须以小写字母和数字开头和结尾
     *                         3.长度在3-63之间
     */
    private static void createBucket() {
        try {
            if (!AliyunOssClientUtil.getOSSClient().doesBucketExist(OSS_BUCKET_NAME)) {//判断是否存在该Bucket，不存在时再重新创建
                AliyunOssClientUtil.getOSSClient().createBucket(OSS_BUCKET_NAME);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 2-  上传文件---去除URL中的？后的时间戳
     *              参数：
     *                  file 文件
     *                  fileDir 上传到OSS上文件的路径
     */
    public static String upload(MultipartFile file, FileDirType fileDir) {
        AliyunOssClientUtil.createBucket();
        String fileName = AliyunOssClientUtil.uploadFile(file, fileDir);

        String fileOssURL = AliyunOssClientUtil.getImgUrl(fileName, fileDir);
        int firstChar = fileOssURL.indexOf("?");
        if (firstChar > 0) {
            fileOssURL = fileOssURL.substring(0, firstChar);
        }
        return fileOssURL;    //文件的访问地址
    }





    /**
     * 3- 上传到OSS服务器  如果同名文件会覆盖服务器上的
              @param file 文件
              @param fileDir  上传到OSS上文件的路径
              @return 文件的访问地址
     */
    private static String uploadFile(MultipartFile file, FileDirType fileDir) {
        String fileName = String.format(
                "%s.%s",
                UUID.randomUUID().toString(),
                FilenameUtils.getExtension(file.getOriginalFilename()));

        try (InputStream inputStream = file.getInputStream()) {
            //创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(getContentType(FilenameUtils.getExtension("." + file.getOriginalFilename())));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);

            System.out.println(fileDir.getDir() + fileName);

            //上传文件
            PutObjectResult putResult =
                    AliyunOssClientUtil.getOSSClient().putObject(OSS_BUCKET_NAME, fileDir.getDir() + fileName, inputStream, objectMetadata);

            return fileName;

        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获得文件路径
          @param fileUrl  文件的URL
          @param fileDir  文件在OSS上的路径
          @return 文件的路径
     */
    private static String getImgUrl(String fileUrl, FileDirType fileDir) {
        if (StringUtils.isEmpty(fileUrl)) {
            throw new RuntimeException("文件地址为空");
        }
        String[] split = fileUrl.split("/");

        //获取oss图片URL失败
        URL url = AliyunOssClientUtil.getOSSClient().generatePresignedUrl(OSS_BUCKET_NAME, fileDir.getDir() + split[split.length - 1], OSS_URL_EXPIRATION);
        if (url == null) {
            throw new RuntimeException("获取oss图片URL失败");
        }
        return url.toString();
    }

  
    private static String getContentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase("gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase("jpeg") ||
                FilenameExtension.equalsIgnoreCase("jpg") ||
                FilenameExtension.equalsIgnoreCase("png")) {
            return "image/jpeg";
        }
        if (FilenameExtension.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase("txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase("pptx") ||
                FilenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase("docx") ||
                FilenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        return "image/jpeg";
    }


    //删除单个
    public static void deleteFileByUrl(String url){

        String filename = url.substring(url.lastIndexOf(AliyunOssClientUtil.FileDirType.avatar.getDir()),url.length());

        if (AliyunOssClientUtil.getOSSClient().doesObjectExist(OSS_BUCKET_NAME,filename)){
            AliyunOssClientUtil.getOSSClient().deleteObject(OSS_BUCKET_NAME,filename);

        }
    }

    //批量删除
    public static void deleteFileList(List<String> urls){
        List<String> rs = AliyunOssClientUtil.getOSSClient()
                .deleteObjects(new DeleteObjectsRequest(OSS_BUCKET_NAME).withKeys(urls))
                .getDeletedObjects();

        if (rs.size() > 0)
            System.out.println("删除失败");
    }

}



