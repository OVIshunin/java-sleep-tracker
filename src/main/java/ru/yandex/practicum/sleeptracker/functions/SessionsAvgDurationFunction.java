package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.classes.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.classes.SleepingSession;

import java.util.List;

//Среднее время сессии сна
public class SessionsAvgDurationFunction implements SleepAnalysisFunctionInterface {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        double avgDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)  // Преобразуем в LongStream
                .average()                                              // Получаем OptionalLong
                .orElse(0.0);
        return new SleepAnalysisResult("Среднее время сессии сна, (мин.)", avgDuration);
    }
}