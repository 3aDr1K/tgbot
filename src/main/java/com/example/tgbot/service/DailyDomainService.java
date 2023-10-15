package com.example.tgbot.service;

import com.example.tgbot.repositories.DailyDomainRepository;
import com.example.tgbot.entities.DailyDomain;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class DailyDomainService {

    @Autowired
    private DailyDomainRepository dailyDomainRepository; // Ваш репозиторий для работы с базой данных

    @Value("${json.url}")
    private String jsonUrl;
    public void fetchDataAndSaveToDatabase() {
        RestTemplate restTemplate = new RestTemplate();

        // Загрузка JSON-данных с URL
        String jsonString = restTemplate.getForObject(jsonUrl, String.class);

        // Парсинг JSON и сохранение данных в базе
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            DailyDomain[] dailyDomains = objectMapper.readValue(jsonString, DailyDomain[].class);
            for (DailyDomain domain : dailyDomains) {
                dailyDomainRepository.save(domain);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void deleteAllDailyDomains() {
        dailyDomainRepository.deleteAll();
    }
}