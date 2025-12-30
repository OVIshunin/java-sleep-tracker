package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.classes.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.classes.SleepingSession;

import java.util.List;
import java.util.function.Function;

//Интерфейс, общий для всех новых аналитических функций,
//для добавления новой - необходимо создать класс, реализующий
//этот интерфейс и добавить объект в список проверок-аналитик

public interface SleepAnalysisFunctionInterface extends Function<List<SleepingSession>, SleepAnalysisResult> {
}