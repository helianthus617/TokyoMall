package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


/*此处测试和其他模块导入的jar包不同*/
@SpringBootTest
public class GulimallThirdPartyApplicationTests {

    @Autowired
    OSSClient ossClient;

    @Test
    public void testUploadWithSpringBoot() throws FileNotFoundException {

        InputStream inputStream1 = new FileInputStream("C:\\Users\\25420\\Desktop\\frozen.jpg");
        ossClient.putObject("helianthusmall", "frozen.jpg", inputStream1);

        ossClient.shutdown();
        System.out.println("上传完成...");
    }

    @Test
    public void testUpload() throws FileNotFoundException {
// Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
//云账号AccessKey有所有APr访问权限，建议遵循阿里云安全最佳实践，
//        创建并使用RAM子账号进行API访问
        String accessKeyId = "LTAI5tSNRBjvxzyAU79DNi1i";
        String accessKeySecret = "P1xrqfPN4a9zKkxpkwAht5VKenvvRx";
///创建ossclient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
///上传文件流。
        //helianthusmall bucket 名称
        InputStream inputStream = new FileInputStream("C:\\Users\\25420\\Desktop\\cat.jpeg");
        ossClient.putObject("helianthusmall", "cat.jpeg", inputStream);
//gulimall-hello 存储空间名
//bug.jpg 对象名
        ossClient.shutdown();
        System.out.println("上传完成...");
    }

}