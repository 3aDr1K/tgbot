package com.example.tgbot;

import com.example.tgbot.entities.DailyDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyDomainRepository extends JpaRepository<DailyDomain, Long> {
}