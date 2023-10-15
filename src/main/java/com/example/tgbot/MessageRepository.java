package com.example.tgbot;

import com.example.tgbot.entities.PGMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<PGMessage, Long> {
}