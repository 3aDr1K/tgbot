package com.example.tgbot.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "daily_domains")
@Getter
@Setter
public class DailyDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String domainname;
    private Integer hotness;
    private Integer price;
    private Integer x_value;
    private Integer yandex_tic;
    private Integer links;
    private Integer visitors;
    private String registrar;
    private Integer old;
    private Timestamp delete_date;
    private Boolean rkn;
    private Boolean judicial;
    private Boolean block;
}
