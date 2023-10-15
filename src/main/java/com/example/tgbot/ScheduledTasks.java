package com.example.tgbot;

import com.example.tgbot.service.DailyDomainService;
import com.example.tgbot.service.MessageReceiveService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final DailyDomainService dailyDomainService;
    private final MessageReceiveService messageReceiveService;

    public ScheduledTasks(DailyDomainService dailyDomainService, MessageReceiveService messageReceiveService) {
        this.dailyDomainService = dailyDomainService;
        this.messageReceiveService = messageReceiveService;
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Запускать каждые 24 часа
    public void fetchDataAndSave() {
        dailyDomainService.deleteAllDailyDomains();
        dailyDomainService.fetchDataAndSaveToDatabase();
        messageReceiveService.sendRegistrationMessageToUsers();
    }
}