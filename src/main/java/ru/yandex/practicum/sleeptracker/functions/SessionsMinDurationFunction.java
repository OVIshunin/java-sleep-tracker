package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.classes.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.classes.SleepingSession;

import java.util.List;

//Минимальное время сессии сна
public class SessionsMinDurationFunction implements SleepAnalysisFunctionInterface {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long minDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)  // Преобразуем в LongStream
                .min()                                              // Получаем OptionalLong
                .orElse(0);
        return new SleepAnalysisResult("Минимальное время сессии сна, (мин.)", minDuration);
    }
}