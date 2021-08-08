package ru.otus.lesson2.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("path")
public class Paths {
    private String auth;
    private String business;
}
