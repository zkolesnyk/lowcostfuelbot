import Entity.FuelsEntity;
import Entity.UsersEntity;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.*;

public class LowCostFuelBot extends TelegramLongPollingBot {

    private Date date = new Date();

    public String getBotUsername() {
        return Config.getBotName();
    }

    public String getBotToken() {
        return Config.getBotToken();
    }

    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String command = update.getMessage().getText();
            int userId = message.getFrom().getId();

            if(!DB.checkMobile(userId)) {
                if (command.matches("[-+]?\\d+")) {
                    if (Integer.parseInt(command) % 10 == 0)
                    add(message, command);
                    else createAnswer(message.getChatId(), "Введите число, кратное 10-и");
                } else if (command.equals(Commands.getFuelCommand) || command.equals(Commands.oneMore) || command.equals(Commands.start)) {
                    getFuel(message);
                } else if (command.equals(Commands.cart)) {
                    showOrder(message);
                } else if (command.equals(Commands.lordCommand)) {
                    sendPht(message);
                } else if (command.equals(Commands.cancel)) {
                    DB.cancelOrder(userId);
                    sendStart(message);
                } else if (command.equals(Commands.help) && isAdmin(message)) {
                    showHelp(message);
                } else if (command.equals(Commands.showFuels) && isAdmin(message)) {
                    showFuels(message);
                } else if (command.equals(Commands.showUsers) && isAdmin(message)) {
                    showUsers(message);
                } else if (command.length() > 8 && command.substring(0, 8).equals(Commands.setCost) && isAdmin(message)) {
                    String str = command.substring(9);
                    String query = str.substring(0, str.indexOf(" "));
                    double cost = Double.parseDouble(str.substring(str.lastIndexOf(' ') + 1));

                    switch (query) {
                        case "pulls95":
                            DB.setPulls95Cost(cost);
                            break;
                        case "pullsdiesel":
                            DB.setPullsdieselCost(cost);
                            break;
                        case "a95euro":
                            DB.setA95euroCost(cost);
                            break;
                        case "a92euro":
                            DB.setA92euroCost(cost);
                            break;
                        case "dieseleuro":
                            DB.setDieseleuroCost(cost);
                            break;
                        case "lpg":
                            DB.setLpgCost(cost);
                            break;
                    }
                    sendMsg(message, "Цена изменена.");
                } else sendMsg(message, "Вы вводите некорректные данные или не существующие команды");
            } else {
                System.out.println(message.getContact());
                getContact(message, "Чтобы начать пользоваться услугой, пройдите регистрацию по номеру телефона:");
            }

        } else if (update.hasMessage() && !update.getMessage().hasText() && !update.getMessage().hasInvoice() && !update.getMessage().hasEntities() && !update.getMessage().hasDocument() && !update.getMessage().hasLocation() && !update.getMessage().hasPhoto() && !update.getMessage().hasSuccessfulPayment()) {
            User from = update.getMessage().getFrom();
            Contact contact = update.getMessage().getContact();
            DB.recordsAdd(contact.getUserID(), contact.getPhoneNumber(), from.getUserName(), from.getFirstName(), from.getLastName(), date);
            sendMsg(update.getMessage(), "Чтобы приступить к покупке, введите команду */get*");

        } else if (update.hasCallbackQuery()) {
            int userId = update.getCallbackQuery().getFrom().getId();
            String callData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            MyResult result = getInlineKeyboardMarkup(callData);
            System.out.println(result.getPrevious());

            switch (callData) {
                case "pulls95": {
                    String query = "pulls95";
                    DB.updateStatus(userId, query);
                    createAnswer(chatId, getHowManyQuery(query));
                    deleteMessage(chatId, messageId);
                    break;
                }
                case "pullsdiesel": {
                    String query = "pullsdiesel";
                    DB.updateStatus(userId, query);
                    createAnswer(chatId, getHowManyQuery(query));
                    deleteMessage(chatId, messageId);
                    break;
                }
                case "a95euro": {
                    String query = "a95euro";
                    DB.updateStatus(userId, query);
                    createAnswer(chatId, getHowManyQuery(query));
                    deleteMessage(chatId, messageId);
                    break;
                }
                case "a92euro": {
                    String query = "a92euro";
                    DB.updateStatus(userId, query);
                    createAnswer(chatId, getHowManyQuery(query));
                    deleteMessage(chatId, messageId);
                    break;
                }
                case "dieseleuro": {
                    String query = "dieseleuro";
                    DB.updateStatus(userId, query);
                    createAnswer(chatId, getHowManyQuery(query));
                    deleteMessage(chatId, messageId);
                    break;
                }
                case "lpg": {
                    String query = "lpg";
                    DB.updateStatus(userId, query);
                    createAnswer(chatId, getHowManyQuery(query));
                    deleteMessage(chatId, messageId);
                    break;
                }
            }
        }
    }

    private boolean isAdmin(Message message) {

        List<UsersEntity> usersEntitys = DB.getUserFromId(message.getFrom().getId());
        UsersEntity user = usersEntitys.get(0);

        return user.getAdmin();
    }

    private void showHelp(Message message) {
        StringBuilder builder = new StringBuilder();
        String[] commands = new String[3];
        String command1 = String.format("%s - показать список видов топлива", Commands.showFuels);
        String command2 = String.format("%s [вид топлива] [цена] - изменить цену на топливо", Commands.setCost) ;
        String command3 = String.format("%s - показать список всех пользователей", Commands.showUsers) ;
        commands[0] = command1;
        commands[1] = command2;
        commands[2] = command3;

        for (int i = 0; i < commands.length; i++) {
            builder.append(String.format("%d. %s%n", i + 1, commands[i]));
        }

        SendMessage msg = new SendMessage()
                .setText(builder.toString())
                .setChatId(message.getChatId());
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void showUsers(Message message) {
        List<UsersEntity> usersEntities = DB.getUsersList();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < usersEntities.size(); i++) {
            builder.append(String.format("%d. `%s`; %d; %s;%n", i+1, usersEntities.get(i).getUsername(), usersEntities.get(i).getUserId(), usersEntities.get(i).getPhone()));
            if (usersEntities.get(i).getAdmin()) builder.insert(builder.lastIndexOf(";") + 2, "`admin`");
        }
        sendMsg(message, builder.toString());
    }

    private void showFuels(Message message) {
        List<FuelsEntity> fuelsEntities = DB.getFuelsList();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fuelsEntities.size(); i++) {
            builder.append(String.format("%d. %s (%.2f грн/л)%n", i+1, fuelsEntities.get(i).getFuel(), fuelsEntities.get(i).getCost()));
        }
        sendMsg(message, builder.toString());
    }

    private void createAnswer(long chatId, String text) {
        SendMessage message = new SendMessage()
                .setReplyMarkup(getHowManyMarkup())
                .setChatId(chatId)
                .setText(text)
                .setParseMode("markdown");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void deleteMessage(long chatId, long messageId) {
        DeleteMessage deleteMessage = new DeleteMessage()
                .setMessageId(toIntExact(messageId))
                .setChatId(String.valueOf(chatId));
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup getHowManyMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("10");
        firstRow.add("20");
        firstRow.add("30");
        keyboard.add(firstRow);

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add("50");
        secondRow.add("100");
        keyboard.add(secondRow);

        replyKeyboardMarkup
                .setKeyboard(keyboard)
                .setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private void add(Message message, String howMany) {
        String status = DB.getStatus(message.getFrom().getId(), howMany);
        String answer = fuelName(status);

        sendMsg(message, String.format("Добавлено: %s — %s л", answer, howMany));
        oneMore(message);
    }

    private void showOrder (Message message) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(Commands.pay);
        keyboard.add(firstRow);

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(Commands.oneMore);
        secondRow.add(Commands.change);
        secondRow.add(Commands.cancel);
        keyboard.add(secondRow);

        replyKeyboardMarkup
                .setKeyboard(keyboard)
                .setSelective(true)
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(true);

        SendMessage msg = new SendMessage()
                .setText(DB.getOrder(message.getFrom().getId()))
                .setChatId(message.getChatId())
                .setReplyMarkup(replyKeyboardMarkup)
                .setParseMode("markdown");

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void oneMore(Message message) {
        SendMessage msg = new SendMessage()
                .setChatId(message.getChatId())
                .setText("Что-нибудь еще?");

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(Commands.oneMore);
        firstRow.add(Commands.cart);

        keyboard.add(firstRow);

        replyKeyboardMarkup
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(true)
                .setSelective(true)
                .setKeyboard(keyboard);

        msg.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getHowManyQuery(String query) {
        return String.format("Выберите необходимое количество *%s* или введите своё (кратно 10-и)", fuelName(query));
    }

    private void getFuel(Message msg) {

        long chat_id = msg.getChatId();
        InlineKeyboardBuilder builder = InlineKeyboardBuilder.create(chat_id)
                .setText("Выбери вид топлива:")
                .row();
        builder.row()
                .button(Fuels.pulls95, "pulls95")
                .button(Fuels.pullsdiesel, "pullsdiesel")
                .button(Fuels.a95euro, "a95euro")
                .endRow();
        builder.row()
                .button(Fuels.a92euro, "a92euro")
                .button(Fuels.dieseleuro, "dieseleuro")
                .button(Fuels.lpg, "lpg")
                .endRow();

        SendMessage message = builder.build();

        try {
            sendApiMethod(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void sendMsg(Message msg, String text){
        SendMessage message = new SendMessage();

        User user = msg.getFrom();
        System.out.println(String.format("%s %s, @%s: %s", user.getFirstName(), user.getLastName(), user.getUserName(), msg.getText()));
        System.out.println(String.format("Bot: %s", text));

        message.setChatId(msg.getChatId())
                .setText(text)
                .setParseMode("markdown")
                .enableMarkdown(true);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendStart(Message message) {
        String text = "Чтобы начать введите комманду */get* или нажмите на кнопку ниже";

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(Commands.start);

        keyboard.add(firstRow);

        replyKeyboardMarkup
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(true)
                .setKeyboard(keyboard);

        SendMessage msg = new SendMessage()
                .setParseMode("markdown")
                .setChatId(message.getChatId())
                .setText(text)
                .setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private MyResult getInlineKeyboardMarkup(String call_data) {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        String call_data_next = call_data + "next";
        String call_data_previous = call_data + "previous";
        rowInline.add(new InlineKeyboardButton()
                .setText("Предыдущий")
                .setCallbackData(call_data_previous));
        rowInline.add(new InlineKeyboardButton()
                .setText("Следующий")
                .setCallbackData(call_data_next));

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return new MyResult(markupInline, call_data_next, call_data_previous);
    }

    private void sendPht(Message msg) {
        SendPhoto photo = new SendPhoto();
        final String photoCaption = "Твой Господин.";
        final String photoUrl = "https://pp.userapi.com/c840431/v840431484/2a926/anY0ALawqH4.jpg";
        photo.setChatId(msg.getChatId())
                .setCaption(photoCaption)
                .setPhoto(photoUrl);

        try {
            sendPhoto(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getContact(Message msg, String text) {
        SendMessage message = new SendMessage();

        KeyboardButton keyboardButton = new KeyboardButton();
        final String contactButtonText = Commands.sendContact;
        keyboardButton
                .setRequestContact(true)
                .setText(contactButtonText);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow buttons = new KeyboardRow();
        buttons.add(keyboardButton);

        keyboard.add(buttons);
        replyKeyboardMarkup
                .setKeyboard(keyboard)
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(true);

        message
                .setChatId(msg.getChatId())
                .setText(text)
                .setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static String fuelName(String query) {
        String answer = "";
        switch (query) {
            case "pulls95":
                answer = Fuels.pulls95;
                break;
            case "pullsdiesel":
                answer = Fuels.pullsdiesel;
                break;
            case "a95euro":
                answer = Fuels.a95euro;
                break;
            case "a92euro":
                answer = Fuels.a92euro;
                break;
            case "dieseleuro":
                answer = Fuels.dieseleuro;
                break;
            case "lpg":
                answer = Fuels.lpg;
                break;
        }
        return answer;
    }
}