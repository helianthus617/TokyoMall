package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "2016092200568607";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCDzOlpVaLq8T6mDLtSuE/i5YTlW8DvPfX39FVMz19Vd//zMNiklkeYdPUoWrTvPcrV+MgX0nVequNh8o8NorkJs+XiaYNcVXmymg/rGDz11KzmMRzrNKGqNRmM36NSsjy2LzNc4b76LplhiC+3lTc8YOM5Ex7it5bPwU4+H+ZXkorfingLQ3VLlR8JzgolvDMGbgf5RdG3CI8W45zzYQ+2ZBY+RMMhL5bsez+6X03Ep2DCI9bRdrYzb5bH37cmBqY3yDJ74c4/wvbsnagLVLxlypwlpYoMSfSzyrRh53WI71GWHdDvxXap3jIYY9xSnRcw9nTWg2Rd9o+0RoobNYBBAgMBAAECggEAbv9uf4hgeU6MfVlxlPumQ6caIfMA7DsZYCYEhHiMiDVUd+HmQc5SI4I0STUC9gzrOwKWuQFEWWK/tmiJs2kcPqCj9Ob5exR9Nj3rNAmopTvkFMS93xPnJqorh2l8aZOz75aWwh1YMeh2wh3oWcqvxwittA53T2Zf+z2IAGfrHEoSQn3BAatZzqcaaB9QUHpCiOvpivevq/Ra4l5bPSt7D3XJKILEGhFkn6ueJhsw9cGV9uHGBDzF1bB06pglGdGloIGbGaBbl5b+spt7TaBfnF8lKBrsKSM3h1UiTMYpDzczN7EbleodAdWbLyYzDQdUiNVmT1a7zQ/zlib1rKnDJQKBgQDc3U1SO3ZhamhAI3LVvWeK/k/x05SGS0cuNNMzb2K2+iYPUCsSEhEidjUSwLOrL5koqxhWZumNXux5z6+fit8l7w3w2wy/jM6F2a/+hgLBnWIcGA97PHLhnQ7kYqYOFHxlmYxYZz1y1hxEOXH/nfIvWBrx+Y7/124pgUHXnPtp1wKBgQCYxHrw6QlHueTpjYoSiQS/itE23bu/DfzuEGF47kaxesdKBXHzTugjOPDwZWgWcRs+oegaUr6ARfVPJpMtJEgBCE9pOC1GusWyJWR5/L910Zo18j9yO59bSRooFmhX2Zldzjr9MA7GjR5VK8CzP3FetOWzZ1/UCPw7evgjOluTpwKBgQC1v36jTr9JxrBnJ0SWUrhkDoZm779nC4dVpK6vwtcuz4aGT2hCBJAJFdDyz5SKKC8W4kSyxRkn9pcOXjOdTD/5DVEhhIBnGE3So6JKBqk5/nL/v2Tt+n5m3kGk+vC+4WjEiqiNDtfoKf00vzjqKmVjRFNYEA72xaanjPBLp6jUHQKBgBQioYmrTXvUtqVJ8I7s3GFarKSsqRhPgaznH9HOpiayZh4Nha1qExLBaJBm7Uc39T1WHb+KdG13yxN5pCBpDcyFCeJ2cxFFgNUwNIQXmiyvScIegdpOnXxssDPkcDBUD/DbQ88nTGtDImJbd/SoQgsXkMcKswjBfm1gkeAiyvs1AoGBAJR8aVlvDaR7jPZMUXEtsI+MS4jie0NiLV2o7KVkSQ2fr0BlFslgOJVL+OHGsUAfWh9YYilku9zbxyr4P/ddOTbc50RB2HyzJ2vBjAjKk7K2GSESLNP4ty40/bMtXmXGY48p5TERt+XVfHlvSSO1rR1Ig/H3YH7so79uLcp7DWZ/";   // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoofo7OGYwMdDCbBAUb4qLZgsJZrE3lEa0PlP5hGRCbmIq4Xe3TSnGAoNDn14eu2b1EUNnRmVvEnLYrfpFnnEaMBFuQdlQIZCIRg3jvy8l+98pZgsrft4SNb1cv5xyyvsh0EHIBoIVaAj6I/feH86/pJhSfIwJ4UE3B7Hx45zhGgCB8xMuunEG+ZOPdVSB378VYfdofx4wUMibDIkHJFA7LhsmNA1Y72tsIkLnPXQ8/UECoVk9Wi46u1TKIxWWaZtF7yWyXsdL+GrhJGr5nD0tarCcIK85Tel14smm7AuytTTvl9uyfRB4KHkOgbAuPiyBFbtrt4PJ8eLmY+nvMCnUwIDAQAB";  // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url;

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"1m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();
        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);
        return result;
    }
}
