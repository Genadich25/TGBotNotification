package com.tgbot.tgbotnotification.controller;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.telegrambot.configuration.TelegramBotConfiguration;

@RestController
public class MainController {
    private final TelegramBotConfiguration configuration;

    public MainController(TelegramBotConfiguration configuration) {
        this.configuration = configuration;
        TelegramBot bot = configuration.telegramBot();
    }
}
