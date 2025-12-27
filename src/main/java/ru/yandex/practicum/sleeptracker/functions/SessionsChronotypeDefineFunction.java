package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.classes.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.classes.SleepingSession;
import ru.yandex.practicum.sleeptracker.enums.Chronotype;

import java.util.*;
import java.util.stream.Collectors;

public class SessionsChronotypeDefineFunction implements SleepAnalysisFunctionInterface {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        // Считаем количество ночей каждого типа
        Map<Chronotype, Long> counts = sessions.stream()
                .map(SleepingSession::classifyNight) //прогоним через метод все сессии
                .filter(Objects::nonNull) // отфильтруем те, что вернули null - дневные или бессонные
                .collect(Collectors.groupingBy(
                        chronotype -> chronotype,
                        Collectors.counting()
                )); //соберем, сгруппировав по типу - получим количество каждого хронотипа

        // на случай, если не нашлось ни одной ночи для анализа - считаем - голубь
        if (counts.isEmpty()) {
            return new SleepAnalysisResult("Хронотип пользователя", Chronotype.PIGEON);
        }

        //найдем максимальное значение ночей среди типов
        long maxCount = counts.values().stream().mapToLong(Long::longValue).max().orElse(0);

        //а теперь соберем в список типы с этим колличеством, если есть максимальный - то попадет только он
        List<Chronotype> maxTypes = counts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        //и если, например, будет максимум 3, и 3 совы, и 3 жаворонка, то попадёт 2 максимальных, и тогда - голубь,
        //в другом случае - получим элемент из списка с индексом 0, он будет единственным, и его тип - как раз искомый
        Chronotype resultType = (maxTypes.size() > 1)
                ? Chronotype.PIGEON
                : maxTypes.get(0);

        return new SleepAnalysisResult("Хронотип пользователя", resultType);
    }
}