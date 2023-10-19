package com.example.tgbot.service;

import com.example.tgbot.repositories.DailyDomainRepository;
import com.example.tgbot.entities.DailyDomain;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class DailyDomainService {
    private static final Logger logger = LoggerFactory.getLogger("com.example.tgbot.info");

    private final DailyDomainRepository dailyDomainRepository;
    private final String jsonUrl;

    @Autowired
    public DailyDomainService(DailyDomainRepository dailyDomainRepository, @Value("${json.url}") String jsonUrl) {
        this.dailyDomainRepository = dailyDomainRepository;
        this.jsonUrl = jsonUrl;
    }

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
            logger.info("Successfully json-parse");
        } catch (IOException e) {
            logger.error("Error in json parse: ", e);
        }
    }

    public void deleteAllDailyDomains() {
        dailyDomainRepository.deleteAll();
    }
}
