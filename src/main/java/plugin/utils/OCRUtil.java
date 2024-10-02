package plugin.utils;

import cn.hutool.json.JSONUtil;
import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeAdvancedRequest;
import com.aliyun.ocr_api20210707.models.RecognizeAdvancedResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import plugin.pojo.Config;
import plugin.pojo.OCRDataInfo;

import static plugin.utils.ConfigUtil.logger;


public class OCRUtil {
    private static Client client;
    public static boolean isSupported;

    static {
        //读取密钥
        Config pluginConfig = ConfigUtil.getConfig();
        String accessKeyId = pluginConfig.getAccessKeyId();
        String accessKeySecret = pluginConfig.getAccessKeySecret();
        if (accessKeySecret == null || accessKeyId == null) {
            isSupported = false;
            logger.warning("未检测到阿里云OCR配置信息，图片文字识别将不可用。");
        } else {
            isSupported = true;
            com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret);
            config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
            try {
                client = new Client(config);
            } catch (Exception e) {
                logger.error("创建client出错");
                logger.error(e.getMessage());
            }
            logger.info("阿里云OCR已配置。");
        }
    }

    public static String getContentOfImage(String url) {
        //设置请求信息
        RecognizeAdvancedRequest recognizeAdvancedRequest = new RecognizeAdvancedRequest()
                .setUrl(url).setNeedRotate(Boolean.TRUE)
                .setNeedRotate(Boolean.TRUE);

        try {
            //发送请求并处理回应
            RecognizeAdvancedResponse response = client.recognizeAdvancedWithOptions(recognizeAdvancedRequest, new RuntimeOptions());
            OCRDataInfo ocrDataInfo = JSONUtil.toBean(response.getBody().getData(), OCRDataInfo.class);
            if (!ocrDataInfo.getContent().isEmpty()) {
                StringBuilder str = new StringBuilder();
                ocrDataInfo.getPrism_wordsInfo().forEach(prismWordsInfoBean -> {
                    str.append(prismWordsInfoBean.getWord());
                    if (!ocrDataInfo.getPrism_wordsInfo().get(ocrDataInfo.getPrism_wordsInfo().size() - 1).equals(prismWordsInfoBean)) {
                        str.append("\r\n");
                    }
                });
                return str.toString();
            } else {
                return null;
            }
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            logger.error(error.getMessage());
            // 诊断地址
            logger.error(error.getData().get("Recommend").toString());
            com.aliyun.teautil.Common.assertAsString(error.message);
            return "ERROR";
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            logger.error(error.getMessage());
            // 诊断地址
            logger.error(error.getData().get("Recommend").toString());
            com.aliyun.teautil.Common.assertAsString(error.message);
            return "ERROR";
        }
    }
}
