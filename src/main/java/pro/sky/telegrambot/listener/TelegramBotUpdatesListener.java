package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.services.NotificationService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    @Autowired
    private NotificationService service;

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
            Long chatId = msg.chat().id();
            String startString = "Отправь боту текст в формате: 01.01.2020 10:00 Напоминание";

            if(msg.text().equals("/start")){
                request = new SendMessage(chatId, startString);
            }

            Matcher matcher = pattern.matcher(msg.text());
            if(matcher.matches()){
                String[] timeDateArray = matcher.group(1).split(" ");
                LocalDate date = LocalDate.parse(timeDateArray[0], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                LocalTime time = LocalTime.parse(timeDateArray[1], DateTimeFormatter.ofPattern("HH:mm"));
                String textMsg = matcher.group(3);
                if((date.isAfter(LocalDate.now()) || date.equals(LocalDate.now())) && time.isAfter(LocalTime.now().truncatedTo(ChronoUnit.MINUTES))){
                    NotificationTask task = new NotificationTask();
                    task.setChatId(chatId);
                    task.setDate(date);
                    task.setTime(time);
                    task.setTextNotification(textMsg);
                    service.saveToRepository(task);
                    telegramBot.execute(new SendMessage(chatId, "Я напомню " + date.toString() + " в " + time.toString()));
                    logger.info("{} task saved {}", LocalTime.now(), task);
                } else {
                    telegramBot.execute(new SendMessage(chatId, "Неправильная форма сообщения!"));
                    telegramBot.execute(new SendMessage(chatId, startString));
                    logger.warn("{} task not saved. wrong form", LocalTime.now());
                }
            }

            if (request != null ){
                telegramBot.execute(request);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void run(){
        List<NotificationTask> listToSend = service.listToSend();
        if(!listToSend.isEmpty()) {
            for (int i = 0; i < service.listToSend().size(); i++) {
                NotificationTask task = service.listToSend().get(i);
                telegramBot.execute(new SendMessage(task.getChatId(), task.getTextNotification()));
                logger.info("{} sent to user - {}", task, LocalTime.now());
                service.deleteNotificationById(task.getId());
            }
        }
    }

}
