import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.console.terminal.MiraiConsoleImplementationTerminal;
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import plugin.App;
import plugin.constant.ChatConstant;
import plugin.constant.ConfigConstant;
import plugin.listener.OwnerMessageListener;
import plugin.utils.AIUtil;
import plugin.utils.ConfigUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static kotlin.io.ConsoleKt.readln;

public class MyTest {

    @Test
    public void sseTest() throws IOException {

        @Data
        class Content {
            private String type;
            private String text;

            public Content() {
            }

            public Content(String type, String text) {
                this.type = type;
                this.text = text;
            }
        }

        @Data
        class Message {
            private String role;
            private Content[] content;
        }

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://open.bigmodel.cn/api/paas/v4/assistant");
        httpPost.setHeaders(new Header[]{
                new BasicHeader("content-type", "application/json"),
                new BasicHeader("authorization", "")
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("assistant_id", "65a265419d72d299a9230616");
        jsonObject.put("model", "glm-4-assistant");
        jsonObject.put("stream", "True");
        Message message = new Message();
        message.role = "user";
        Content[] contents = new Content[]{new Content("text", "改写提示词：现在你是一名Java开发工程师...")};
        message.content = contents;
        Message[] messages = new Message[]{message};
        jsonObject.put("messages", messages);
        HttpEntity httpEntity = new StringEntity(jsonObject.toString(), "UTF-8");

        httpPost.setEntity(httpEntity);

        CloseableHttpResponse response = client.execute(httpPost);
        System.out.println(response.getStatusLine());

        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity, "UTF-8");
        String[] strs = result.split("\n\n");
        for (String str : strs) {
            pojo.Data data = JSONObject.parseObject(str.substring(6), pojo.Data.class);
            if (data.getStatus().equals("completed")) {
                break;
            }
            //解析返回结果
//            System.out.println(data.getChoices().get(0).getDelta());
            String delta = data.getChoices().get(0).getDelta();
            JSONObject deltaJsonObject = JSONObject.parseObject(delta);

            String role = deltaJsonObject.get("role").toString();
            if ("assistant".equals(role)) {
                //输出content
                System.out.print(deltaJsonObject.get("content"));
            } else if ("tool".equals(role)) {
                //解析tool_call内容，根据字段type
                JSONObject toolCall = deltaJsonObject.getJSONArray("tool_calls").getJSONObject(0);
                String type = toolCall.get("type").toString();
                if ("function".equals(type)) {
                    //如果调用了函数
                    JSONObject functionJsonObject = toolCall.getJSONObject("function");
                    if (functionJsonObject.get("outputs") != null) {
                        String contentUrl = functionJsonObject.getJSONArray("outputs").getJSONObject(0).getJSONObject("content").get("url").toString();
                        System.out.println("\r\n" + contentUrl);
                    }
                } else if ("drawing_tool".equals(type)) {
                    //如果是绘画工具
                    JSONObject drawingToolJsonObject = toolCall.getJSONObject("drawing_tool");
                    if (drawingToolJsonObject.get("outputs") != null) {
                        String imageUrl = drawingToolJsonObject.getJSONArray("outputs").getJSONObject(0).get("image").toString();
                        System.out.println(imageUrl);
                    }
                } else if ("web_browser".equals(type)) {
                    JSONObject webBrowserJsonObject = toolCall.getJSONObject("web_browser");
                    if (webBrowserJsonObject.get("outputs") != null) {
                        for (Object o : webBrowserJsonObject.getJSONArray("outputs")) {
                            System.out.println(JSONObject.parseObject(o.toString()).get("title"));
                            System.out.println(JSONObject.parseObject(o.toString()).get("link"));
                            System.out.println(JSONObject.parseObject(o.toString()).get("content"));
                        }
                    }
                }
            }

        }
        response.close();
        client.close();
    }

    @Test
    public void ownerTest() throws ClassNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        ConfigUtil.load();

        Class clazz = OwnerMessageListener.class;
        Method handleCommand = clazz.getDeclaredMethod("handleCommand", String.class, String.class);
        handleCommand.setAccessible(true);
        while (true){
            String input = readln();
            String command = input.split(ChatConstant.BLANK)[0];
            String arguments = input.split(ChatConstant.BLANK)[1];

            Object invoke = handleCommand.invoke(clazz.getDeclaredConstructor().newInstance(), command, arguments);
            System.out.println(invoke);
        }

    }

    @Test
    public void chatTest() throws IOException, ClassNotFoundException {
        ConfigUtil.load();

        Long id = 1L;
        String content = "你好";
        String chatCommand = "/c ";

        String customChat = AIUtil.customChat(id, content, null, chatCommand);
        System.out.println(customChat);

        String defaultChat = AIUtil.defaultChat(id, content, null);
        System.out.println(defaultChat);

        String chatOnce = AIUtil.chatOnce(content, null);
        System.out.println(chatOnce);
    }

    @Test
    public void regexTest(){
        String str = "/c glm";
        System.out.println(str.matches(ConfigConstant.MODEL_CHANGE));
    }

    @Test
    public void terminalTest() throws InterruptedException {
        MiraiConsoleTerminalLoader.INSTANCE.startAsDaemon(new MiraiConsoleImplementationTerminal());
        PluginManager.INSTANCE.loadPlugin(App.INSTANCE);
        PluginManager.INSTANCE.enablePlugin(App.INSTANCE);
        while (true);
    }
}
