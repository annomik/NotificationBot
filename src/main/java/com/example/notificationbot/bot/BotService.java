package com.example.notificationbot.bot;

import com.example.notificationbot.model.Task;
import com.example.notificationbot.model.User;
import com.example.notificationbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Component
@EnableScheduling
public class BotService extends TelegramLongPollingBot {

    private final BotConfig config;
    private final UserService userService;

    @Autowired
    public BotService(BotConfig config, UserService userService) {
        super(config.getToken());
        this.config = config;
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();  //same
            long userId = update.getMessage().getChat().getId();    //same

            switch (messageText) {
                case ("/start") -> {
                    String firstName = update.getMessage().getChat().getFirstName();
                    if (!isUserExist(userId)) {
                        startCommandReceived(chatId, firstName);
                        sendMessage(chatId, "User with Id " + userId + " was successfully registered");
                        sendMessage(chatId, showLoginData(userId));
                    } else {
                        sendMessage(chatId, "User with Id " + userId + " is already registered");
                    }
                }
                case ("/info") -> {
                    if (!isUserExist(userId)) {
                        sendMessage(chatId, "You aren't registered yet, try with '/start' command");
                    } else {
                        sendMessage(chatId, showLoginData(userId));
                    }
                }

                default -> sendMessage(chatId, "I don't know this command, try another one");
            }
        }
    }

    private boolean isUserExist(long userId) {
        return userService.findByTelegramUserId(userId).isPresent();
    }

    private String showLoginData(long userId) {
        return "Your Telegram ID is " + userId;
    }

    private void startCommandReceived(long chatId, String userFirstName) {
        String answer = "Hi, " + userFirstName + ", nice to meet you!";

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        executeMessage(message);
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.getMessage();
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendNotification() {
        LocalDateTime current = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<User> users = userService.findAll();

        for (User user : users) {
            Set<Task> tasks = user.getTasks();
            for (Task task : tasks) {
                if (task.getNotificationTime() != null
                        && task.getNotificationTime().truncatedTo(ChronoUnit.MINUTES).equals(current)) {
                    System.out.println(task.getText() + " " + user.getTelegramUserId());
                    prepareAndSendMessage(user.getTelegramUserId(), task.getText());
                }
            }
        }
    }
}
