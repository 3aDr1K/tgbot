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

        if (update.getMessage() != null && update.getMessage().hasText()) {
            User user = userRepository.findByChatId(update.getMessage().getChatId());
            if (user == null) {
               user = registerNewUserMsgPg(update.getMessage());
                sendMessage(user.getChatId(), "Hello new user!");
            }
            else {
                registerMsgPg(user, update.getMessage());
                sendMessage(user.getChatId(), "Hello old user!");
            }
        } else {
            System.out.println("Error on UpdateReceived.");
        }
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
    private User registerNewUserMsgPg(Message message) {
        User user = new User();
        PGMessage pgMessage = new PGMessage();

        pgMessage.setUser(user);
        pgMessage.setText(message.getText());
        pgMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));

        user.setChatId(message.getChatId());
        user.setUsername(message.getChat().getUserName());
        user.setLastMessageAt(new Timestamp(System.currentTimeMillis()));
        user.setLastMessage(message.getText());

        userRepository.save(user);
        pgMessageRepository.save(pgMessage);

        return user;
    }
    private void registerMsgPg(User user, Message message)
    {
        PGMessage pgMessage = new PGMessage();

        user.setChatId(message.getChatId());
        user.setUsername(message.getChat().getUserName());
        user.setLastMessageAt(new Timestamp(System.currentTimeMillis()));
        user.setLastMessage(message.getText());

        pgMessage.setUser(user);
        pgMessage.setText(message.getText());
        pgMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));


        userRepository.save(user);
        pgMessageRepository.save(pgMessage);
    }
}
