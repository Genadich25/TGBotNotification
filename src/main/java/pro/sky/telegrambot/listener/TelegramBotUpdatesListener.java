package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import org.apache.catalina.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.configuration.TelegramBotConfiguration;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            Message msg = update.message();
            BaseRequest request = null;

            if(msg != null){
                request = new SendMessage(msg.chat().id(), "Работает!");
            }

            if (request != null){
                telegramBot.execute(request);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
