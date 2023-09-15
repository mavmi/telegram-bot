package mavmi.telegram_bot.chat_gpt_bot.telegram_bot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import mavmi.telegram_bot.common.auth.BotNames;
import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.logger.Logger;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static mavmi.telegram_bot.chat_gpt_bot.constants.Phrases.EMTPY_REQ_MSG;
import static mavmi.telegram_bot.chat_gpt_bot.constants.Phrases.ERROR_MSG;

public class Bot extends AbsTelegramBot {
    private final TelegramBot telegramBot;
    private final String chatGptToken;
    private final UserAuthentication userAuthentication;

    private OkHttpClient okHttpClient;

    public Bot(String telegramBotToken, String chatGptToken, Logger logger, UserAuthentication userAuthentication){
        super(logger);
        this.telegramBot = new TelegramBot(telegramBotToken);
        this.chatGptToken = chatGptToken;
        this.userAuthentication = userAuthentication;
    }

    @Override
    public void run(){
        logger.log("CHAT-GPT-BOT IS RUNNING");
        okHttpClient = initClient();
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                Message message = update.message();
                long clientChatId = message.chat().id();
                String clientMsg = message.text();

                if (!userAuthentication.isPrivilegeGranted(update.message().from().id(), BotNames.CHAT_GPT_BOT)) continue;
                if (clientMsg == null) continue;
                logEvent(message);
                String response = gptRequest(clientMsg);
                sendMsg(new SendMessage(clientChatId, response).parseMode(ParseMode.Markdown));

                logger.log("REQUEST: " + clientMsg);
                logger.log("RESPONSE: " + response);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            logger.err(e.getMessage());
        });
    }

    private <T extends BaseRequest<T, R>, R extends BaseResponse> R sendMsg(BaseRequest<T, R> baseRequest){
        return telegramBot.execute(baseRequest);
    }

    private String gptRequest(String msg){
        MediaType mediaType = MediaType.parse("application/json");

        JsonObject msgJsonObject = new JsonObject();
        msgJsonObject.addProperty("role", "user");
        msgJsonObject.addProperty("content", msg.replaceAll("\"", "\\\\\"") +"\"");

        JsonArray msgsJsonArray = new JsonArray();
        msgsJsonArray.add(msgJsonObject);

        JsonObject requestJsonObject = new JsonObject();
        requestJsonObject.addProperty("model", "gpt-3.5-turbo");
        requestJsonObject.add("messages", msgsJsonArray);

        RequestBody requestBody = RequestBody.create(mediaType, requestJsonObject.toString());
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + chatGptToken)
                .build();

        int attempt = 0;
        while (true){
            attempt++;
            try {
                return sendRequest(request);
            } catch (IOException | JsonSyntaxException | NullPointerException e) {
                logger.err(e.getMessage());
                if (attempt == 2) return ERROR_MSG;
                initClient();
            }
        }
    }

    private String sendRequest(Request request) throws IOException {
        Response response = null;
        String pureResponse = null;
        JsonObject jsonObject = null;

        try {
            response = okHttpClient.newCall(request).execute();
            pureResponse = response.body().string();
            jsonObject = JsonParser.parseString(pureResponse)
                    .getAsJsonObject()
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject();
        } catch (Exception e) {
            logger.err(
                    "Status code: " +
                            response.code() +
                            "\n" +
                            "Server response: " +
                            pureResponse
            );
            throw e;
        }

        if (jsonObject.has("message")) {
            jsonObject = jsonObject.getAsJsonObject("message");
            if (jsonObject.has("content")) {
                return jsonObject.get("content").getAsString();
            }
        }

        return EMTPY_REQ_MSG;
    }

    private OkHttpClient initClient(){
        final long connectionTimeOut = 0;
        final long readTimeOut = 200;
        final long writeTimeOut = 200;

        return new OkHttpClient.Builder()
                .connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
                .readTimeout(readTimeOut, TimeUnit.SECONDS)
                .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                .cookieJar(
                        new CookieJar() {
                            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                            @Override
                            public void saveFromResponse(
                                    @NotNull HttpUrl httpUrl,
                                    @NotNull List<Cookie> list
                            ){
                                cookieStore.put(httpUrl.host(), list);
                            }

                            @NotNull
                            @Override
                            public List<Cookie> loadForRequest(
                                    @NotNull HttpUrl httpUrl
                            ){
                                List<Cookie> cookies = cookieStore.get(httpUrl.host());
                                return (cookies != null) ? cookies : new ArrayList<>();
                            }
                        }
                )
                .build();
    }
}
