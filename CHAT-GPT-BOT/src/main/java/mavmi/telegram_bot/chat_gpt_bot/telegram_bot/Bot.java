package mavmi.telegram_bot.chat_gpt_bot.telegram_bot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.common.user_authentication.AvailableUsers;
import mavmi.telegram_bot.common.user_authentication.UserInfo;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static mavmi.telegram_bot.chat_gpt_bot.constants.Phrases.*;

public class Bot {
    private TelegramBot telegramBot;
    private Logger logger;
    private String chatGptToken;
    private AvailableUsers availableUsers;

    public Bot(){

    }

    public Bot setTelegramBot(String telegramBotToken){
        telegramBot = new TelegramBot(telegramBotToken);
        return this;
    }
    public Bot setChatGptToken(String chatGptToken){
        this.chatGptToken = chatGptToken;
        return this;
    }
    public Bot setLogger(Logger logger){
        this.logger = logger;
        return this;
    }
    public Bot setAvailableUsers(AvailableUsers availableUsers){
        this.availableUsers = availableUsers;
        return this;
    }

    public void run(){
        if (!checkValidity()) throw new RuntimeException("Bot is not set up");

        logger.log("CHAT-GPT-BOT IS RUNNING");
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                long clientChatId = update.message().chat().id();
                String clientMsg = update.message().text();

                if (!availableUsers.isUserAvailable(new UserInfo(clientChatId))) continue;
                if (clientMsg == null) continue;
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
        final long secondsTimeOut = 100;
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, "{" +
                                                                            "\"model\": \"gpt-3.5-turbo\", " +
                                                                            "\"messages\":" +
                                                                            "[" +
                                                                                "{" +
                                                                                    "\"role\": \"user\"," +
                                                                                    "\"content\": \"" + msg.replaceAll("\"", "\\\\\"") +"\"" +
                                                                                "}" +
                                                                            "]" +
                                                                        "}");

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + chatGptToken)
                .build();

        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(secondsTimeOut, TimeUnit.SECONDS)
                    .readTimeout(secondsTimeOut, TimeUnit.SECONDS)
                    .writeTimeout(secondsTimeOut, TimeUnit.SECONDS)
                    .build();
            String pureResponse = okHttpClient.newCall(request).execute().body().string();
            JsonObject jsonObject = JsonParser.parseString(pureResponse)
                    .getAsJsonObject()
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject();

            if (jsonObject.has("message")) {
                jsonObject = jsonObject.getAsJsonObject("message");
                if (jsonObject.has("content")) {
                    return jsonObject.get("content").getAsString();
                }
            }
            return EMTPY_REQ_MSG;
        } catch (IOException | JsonSyntaxException | NullPointerException e) {
            logger.err(e.getMessage());
            return ERROR_MSG;
        }
    }

    private boolean checkValidity(){
        return telegramBot != null &&
                logger != null &&
                chatGptToken != null &&
                availableUsers != null;
    }

}
