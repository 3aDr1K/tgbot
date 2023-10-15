package com.example.tgbot.service;

import com.example.tgbot.repositories.DailyDomainRepository;
import com.example.tgbot.repositories.UserRepository;
import com.example.tgbot.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class MessageReceiveService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DailyDomainRepository dailyDomainRepository;

    @Autowired
    private MyTelegramBot telegramBot;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void sendRegistrationMessageToUsers() {
        List<User> registeredUsers = userRepository.findAll(); // Получаем всех зарегистрированных пользователей
        long totalDomains = dailyDomainRepository.count(); // Получаем общее количество доменов
        Date currentDate = new Date();
        for (User user : registeredUsers) {
            long chatId = user.getChatId();
            String message = String.format("(%s) Зарегистрировано %d доменов", dateFormat.format(currentDate), totalDomains);
            // Отправляем сообщение каждому пользователю
            telegramBot.sendMessage(chatId, message);
        }
    }
}
