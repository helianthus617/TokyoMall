package com.atguigu.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;

import com.atguigu.gulimall.thirdparty.component.SmsComponent;
import com.atguigu.gulimall.thirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


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


    @Test
    public void testsm() {
        String host = "https://jnlzsms.market.alicloudapi.com";
        String path = "/lundroid/smsSend";
        String method = "GET";
        String appcode = "304e22bb4ded4c6d812c40133ff41b0c";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", "15136938718");
        querys.put("param", "1234");
        querys.put("templateId", "SMS_75800186");

        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testsm1() {
        smsComponent.sendSmsCode("15136938718", "1223");
    }

}