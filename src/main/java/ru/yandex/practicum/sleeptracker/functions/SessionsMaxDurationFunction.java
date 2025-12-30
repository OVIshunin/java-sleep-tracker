package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.classes.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.classes.SleepingSession;

import java.util.List;

//Максимальное время сессии сна
public class SessionsMaxDurationFunction implements SleepAnalysisFunctionInterface {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long maxDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)  // Преобразуем в LongStream
                .max()                                              // Получаем OptionalLong
                .orElse(0);
        return new SleepAnalysisResult("Максимальное время сессии сна, (мин.)", maxDuration);
    }
}