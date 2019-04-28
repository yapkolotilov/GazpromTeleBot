package ocean;

import org.glassfish.jersey.message.internal.TracingLogger;
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {


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

    @Override
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        System.out.printf("Got message: %s\n", text);
        String chatId = update.getMessage().getChatId().toString();
        if (text.matches("/start \\d")) {
            int id = Integer.parseInt(text.split("/start ")[1]);
            String name = "Ocean"; // TODO: Get name from Egor.
            sendMsg(chatId, String.format("Оцените презентацию команды \"%s\" по шкале от 1 до 10", name), true);
            return;
        }
        if (text.equals("/start")) {
            sendMsg(chatId, "Пожалуйста, воспользуйтесь предоставленной ссылкой!", false);
            return;
        }
        if (text.matches("\\d")) {
            try {
                int mark = Integer.parseInt(text);
                if (mark < 0)
                    throw new NumberFormatException();
                // TODO: Send request to Egor.
                sendMsg(chatId, "Спасибо за участие в оценке!", false);
            } catch (NumberFormatException e) {
                sendMsg(chatId, "Недопустимое число!", true);
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
