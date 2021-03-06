package ocean;

import org.glassfish.jersey.message.internal.TracingLogger;
import org.json.HTTP;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {

    Set<String> voted = new HashSet<>();

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        try {
            api.registerBot(new Bot());
            System.out.println("Server started");
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(String chatId, String message, boolean setButtons) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        if (setButtons)
            setButtons(sendMessage);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("1"));
        keyboardFirstRow.add(new KeyboardButton("2"));
        keyboardFirstRow.add(new KeyboardButton("3"));
        keyboardFirstRow.add(new KeyboardButton("4"));
        keyboardFirstRow.add(new KeyboardButton("5"));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("6"));
        keyboardSecondRow.add(new KeyboardButton("7"));
        keyboardSecondRow.add(new KeyboardButton("8"));
        keyboardSecondRow.add(new KeyboardButton("9"));
        keyboardSecondRow.add(new KeyboardButton("10"));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private void sendMessageToServer(int mark) {
        try {
            URL url = new URL("http://10.20.2.219:8000/actum/poll/" + mark);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        System.out.printf("Got message: %s\n", text);
        String chatId = update.getMessage().getChatId().toString();
        if (text.matches("/start \\d+")) {
            int id = Integer.parseInt(text.split("/start ")[1]);
            String name = "Ocean"; // TODO: Get name from Egor.
            sendMsg(chatId, String.format("Оцените презентацию команды \"%s\" по шкале от 1 до 10", name), true);
            return;
        }
        if (text.equals("/start")) {
            sendMsg(chatId, "Пожалуйста, воспользуйтесь предоставленной ссылкой!", false);
            return;
        }
        if (text.matches("\\d+")) {
            try {
                if (voted.contains(chatId)) {
                    sendMsg(chatId, "Вы уже проголосовали!", false);
                    return;
                }

                int mark = Integer.parseInt(text);
                if (mark < 0 || mark > 10)
                    throw new NumberFormatException("Недопустимое число!");
                if (mark <= 6)
                    throw new Exception("Маловато будет(");
                sendMessageToServer(mark);
                sendMsg(chatId, "Спасибо за участие в оценке!", false);
                voted.add(chatId);
            } catch (Exception e) {
                sendMsg(chatId, e.getMessage(), true);
            }
            return;
        }
    }

    @Override
    public String getBotUsername() {
        return "TelegramBotGazprom";
    }

    @Override
    public String getBotToken() {
        return "813366465:AAEUQ4bkD_SQkJ7OcIjP_hagduse_WxILzU";
    }
}
