package com.example.tgbot.service;

import com.example.tgbot.repositories.DailyDomainRepository;
import com.example.tgbot.repositories.UserRepository;
import com.example.tgbot.entities.User;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class MessageReceiveService {
    private final UserRepository userRepository;
    private final DailyDomainRepository dailyDomainRepository;
    private final MyTelegramBot telegramBot;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public MessageReceiveService(UserRepository userRepository, DailyDomainRepository dailyDomainRepository, MyTelegramBot telegramBot) {
        this.userRepository = userRepository;
        this.dailyDomainRepository = dailyDomainRepository;
        this.telegramBot = telegramBot;
    }

    public void sendRegistrationMessageToUsers() {
        List<User> registeredUsers = userRepository.findAll();
        long totalDomains = dailyDomainRepository.count();
        Date currentDate = new Date();
        for (User user : registeredUsers) {
            long chatId = user.getChatId();
            String message = String.format("(%s) Зарегистрировано %d доменов", dateFormat.format(currentDate), totalDomains);
            telegramBot.sendMessage(chatId, message);
        }
    }
}
