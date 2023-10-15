package com.example.tgbot.service;

import com.example.tgbot.MessageRepository;
import com.example.tgbot.config.BotConfig;
import com.example.tgbot.entities.PGMessage;
import com.example.tgbot.entities.User;
import com.example.tgbot.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Service
public class MyTelegramBot extends TelegramLongPollingBot {
    final BotConfig botConfig;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository pgMessageRepository;

    public MyTelegramBot(BotConfig botConfig){
        this.botConfig = botConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByChatId(update.getMessage().getChatId()));

        userOptional.ifPresentOrElse(
                user -> {
                    // Если пользователь существует
                    processUserMessage(user, update.getMessage());
                },
                () -> {
                    // Если пользователя нет
                    User newUser = new User();
                    processUserMessage(newUser, update.getMessage());
                }
        );
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken(){
        return botConfig.getBotToken();
    }

    void sendMessage(Long chatId, String msg) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(msg);

        try{
            execute(message);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }

    }

    private void processUserMessage(User user, Message message) {
        PGMessage pgMessage = new PGMessage();
        String response = "Вы написали: " + message.getText() + " :)";

        user.setChatId(message.getChatId());
        user.setUsername(message.getChat().getUserName());
        user.setLastMessageAt(new Timestamp(System.currentTimeMillis()));
        user.setLastMessage(message.getText());

        pgMessage.setUser(user);
        pgMessage.setText(message.getText());
        pgMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
        pgMessage.setBotResponse(response);

        sendMessage(message.getChatId(), response);
        userRepository.save(user);
        pgMessageRepository.save(pgMessage);
    }
}
