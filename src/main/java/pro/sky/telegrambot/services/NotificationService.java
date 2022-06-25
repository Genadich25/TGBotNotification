package pro.sky.telegrambot.services;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public void saveToRepository(NotificationTask task){
        repository.save(task);
    }

    public List<NotificationTask> listToSend(){
        return repository.findByTimeEquals(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    public void deleteNotificationById(Long id){
        repository.deleteById(id);
    }
}
