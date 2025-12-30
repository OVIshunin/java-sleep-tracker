package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.classes.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.classes.SleepingSession;

import java.util.List;

//проверка на количество сессий сна
public class SessionsCountFunction implements SleepAnalysisFunctionInterface {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long count = sessions.stream().count();
        return new SleepAnalysisResult("Общее количество сессий сна", count);
    }
}