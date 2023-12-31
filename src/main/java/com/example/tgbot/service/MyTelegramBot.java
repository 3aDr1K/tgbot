package com.example.tgbot.service;

import com.example.tgbot.repositories.MessageRepository;
import com.example.tgbot.config.BotConfig;
import com.example.tgbot.entities.PGMessage;
import com.example.tgbot.entities.User;
import com.example.tgbot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger("com.example.tgbot.info");


    final BotConfig botConfig;
    private final UserRepository userRepository;
    private final MessageRepository pgMessageRepository;

    public MyTelegramBot(BotConfig botConfig, UserRepository userRepository, MessageRepository pgMessageRepository){
        this.botConfig = botConfig;
        this.userRepository = userRepository;
        this.pgMessageRepository = pgMessageRepository;
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
            logger.info("Succesfully message sending: " + chatId.toString() + " " + msg);
        } catch (TelegramApiException e){
            deleteUser(chatId);
            logger.error("An error has occurred: ", e);
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

    public void deleteUser(Long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user != null) {
            userRepository.delete(user);
        }
    }
}
