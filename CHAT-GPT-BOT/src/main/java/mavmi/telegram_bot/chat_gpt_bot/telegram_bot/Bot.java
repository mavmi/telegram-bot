package mavmi.telegram_bot.chat_gpt_bot.telegram_bot;

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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static mavmi.telegram_bot.chat_gpt_bot.constants.Phrases.EMTPY_REQ_MSG;
import static mavmi.telegram_bot.chat_gpt_bot.constants.Phrases.ERROR_MSG;

public class Bot extends AbsTelegramBot {
    private final TelegramBot telegramBot;
    private final String chatGptToken;
    private final UserAuthentication userAuthentication;

    public Bot(String telegramBotToken, String chatGptToken, Logger logger, UserAuthentication userAuthentication){
        super(logger);
        this.telegramBot = new TelegramBot(telegramBotToken);
        this.chatGptToken = chatGptToken;
        this.userAuthentication = userAuthentication;
    }

    @Override
    public void run(){
        logger.log("CHAT-GPT-BOT IS RUNNING");
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
}
