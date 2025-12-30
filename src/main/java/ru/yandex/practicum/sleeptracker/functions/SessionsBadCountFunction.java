package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.classes.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.enums.SleepQuality;
import ru.yandex.practicum.sleeptracker.classes.SleepingSession;

import java.util.List;

//Максимальное время сессии сна
public class SessionsBadCountFunction implements SleepAnalysisFunctionInterface {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long badSleepSessionsCount = sessions.stream()
                .filter(session -> session.getQuality().equals(SleepQuality.BAD))
                .count();
        return new SleepAnalysisResult("Количество плохих сессий сна", badSleepSessionsCount);
    }
}