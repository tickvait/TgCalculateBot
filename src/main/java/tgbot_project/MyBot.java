package tgbot_project;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyBot extends TelegramLongPollingBot{
    
    private final String BOT_USERNAME = "";
    private final String BOT_TOKEN = "";
    private enum State {
        NONE,
        BIN_TO_DEC,
        DEC_TO_BIN,
        OCT_TO_DEC,
        DEC_TO_OCT,
        HEX_TO_DEC,
        DEC_TO_HEX
    }

    private Map<Long, State> userStates = new HashMap<>();

    @Override
    public String getBotUsername() {
        return BOT_USERNAME; 
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN; 
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery().getMessage(), update.getCallbackQuery().getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(Message message) throws TelegramApiException {
        Long chatId = message.getChatId();
        String text = message.getText().trim();
        State state = userStates.getOrDefault(chatId, State.NONE);

        if (text.equals("/start")) {
            sendWelcomeMessage(chatId);
            userStates.put(chatId, State.NONE);
            return;
        }

        switch (state) {
            case BIN_TO_DEC:
                if (isBinaryNumber(text)) {
                    int decimal = Integer.parseInt(text, 2);
                    sendText(chatId, "Двоичное число " + text + " в десятичном формате: " + decimal);
                } else {
                    sendText(chatId, "Ошибка: введено не двоичное число. Попробуйте еще раз.");
                    return;
                }
                break;

            case DEC_TO_BIN:
                if (isDecimalNumber(text)) {
                    int decimal = Integer.parseInt(text);
                    String binary = Integer.toBinaryString(decimal);
                    sendText(chatId, "Десятичное число " + text + " в двоичном формате: " + binary);
                } else {
                    sendText(chatId, "Ошибка: введено не десятичное число. Попробуйте еще раз.");
                    return;
                }
                break;

            case OCT_TO_DEC:
                if (isOctalNumber(text)) {
                    int decimal = Integer.parseInt(text, 8);
                    sendText(chatId, "Восьмеричное число " + text + " в десятичном формате: " + decimal);
                } else {
                    sendText(chatId, "Ошибка: введено не восьмеричное число. Попробуйте еще раз.");
                    return;
                }
                break;

            case DEC_TO_OCT:
                if (isDecimalNumber(text)) {
                    int decimal = Integer.parseInt(text);
                    String octal = Integer.toOctalString(decimal);
                    sendText(chatId, "Десятичное число " + text + " в восьмеричном формате: " + octal);
                } else {
                    sendText(chatId, "Ошибка: введено не десятичное число. Попробуйте еще раз.");
                    return;
                }
                break;

            case HEX_TO_DEC:
                if (isHexNumber(text)) {
                    int decimal = Integer.parseInt(text, 16);
                    sendText(chatId, "Шестнадцатеричное число " + text.toUpperCase() + " в десятичном формате: " + decimal);
                } else {
                    sendText(chatId, "Ошибка: введено не шестнадцатеричное число. Попробуйте еще раз.");
                    return;
                }
                break;

            case DEC_TO_HEX:
                if (isDecimalNumber(text)) {
                    int decimal = Integer.parseInt(text);
                    String hex = Integer.toHexString(decimal).toUpperCase();
                    sendText(chatId, "Десятичное число " + text + " в шестнадцатеричном формате: " + hex);
                } else {
                    sendText(chatId, "Ошибка: введено не десятичное число. Попробуйте еще раз.");
                    return;
                }
                break;

            case NONE:
            default:
                sendText(chatId, "Пожалуйста, выберите действие, используя кнопки ниже.");
                sendWelcomeMessage(chatId);
                return;
        }

       
        userStates.put(chatId, State.NONE);
        sendWelcomeMessage(chatId);
    }

    private void handleCallback(Message message, String data) throws TelegramApiException {
        Long chatId = message.getChatId();

        switch (data) {
            case "BIN_TO_DEC":
                userStates.put(chatId, State.BIN_TO_DEC);
                sendText(chatId, "Напиши двоичное число:");
                break;
            case "DEC_TO_BIN":
                userStates.put(chatId, State.DEC_TO_BIN);
                sendText(chatId, "Напиши десятичное число:");
                break;
            case "OCT_TO_DEC":
                userStates.put(chatId, State.OCT_TO_DEC);
                sendText(chatId, "Напиши восьмеричное число:");
                break;
            case "DEC_TO_OCT":
                userStates.put(chatId, State.DEC_TO_OCT);
                sendText(chatId, "Напиши десятичное число:");
                break;
            case "HEX_TO_DEC":
                userStates.put(chatId, State.HEX_TO_DEC);
                sendText(chatId, "Напиши шестнадцатеричное число:");
                break;
            case "DEC_TO_HEX":
                userStates.put(chatId, State.DEC_TO_HEX);
                sendText(chatId, "Напиши десятичное число:");
                break;
        }
    }

    private void sendWelcomeMessage(Long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите действие:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("Двоичное число в десятичное", "BIN_TO_DEC"));
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("Десятичное число в двоичное", "DEC_TO_BIN"));
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(createButton("Восьмеричное число в десятичное", "OCT_TO_DEC"));
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(createButton("Десятичное число в восьмеричное", "DEC_TO_OCT"));
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(createButton("Шестнадцатеричное число в десятичное", "HEX_TO_DEC"));
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        row6.add(createButton("Десятичное число в шестнадцатеричное", "DEC_TO_HEX"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        rows.add(row6);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        execute(message);
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private void sendText(Long chatId, String text) throws TelegramApiException {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(text);
        execute(msg);
    }

    private boolean isBinaryNumber(String str) {
        return str.matches("[01]+");
    }

    private boolean isDecimalNumber(String str) {
        return str.matches("\\d+");
    }

    private boolean isOctalNumber(String str) {
        return str.matches("[0-7]+");
    }

    private boolean isHexNumber(String str) {
        return str.matches("[0-9a-fA-F]+");
    }
}
