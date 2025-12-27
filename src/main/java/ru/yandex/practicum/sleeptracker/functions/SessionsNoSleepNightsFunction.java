package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.classes.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.classes.SleepingSession;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

//Бессонные ночи
public class SessionsNoSleepNightsFunction  implements SleepAnalysisFunctionInterface {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        //если не зафиксировано ни одной сессии - то и бессонных ночей не посчитать
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0L);
        }

        //Находим минимальную и максимальную дату в данных
        //В исходном списке в задании - есть большой пропуск с 11 до 30 числа,
        //но поскольку мы считаем, что часы не снимались - значит либо это был сбой,
        //либо несчастный испытуемый провёл более 2х недель без сна. Надеюсь - сбой...
        LocalDate minDate = sessions.stream()
                .map(session -> session.getStartTime().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate maxDate = sessions.stream()
                .map(session -> session.getEndTime().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        // Общее количество ночей между 2мя граничными датами
        long totalNights = Period.between(minDate, maxDate).getDays();

        //на случай, если у нас 1 запись в логе, и она в пределах 1 суток
        //например сон с 2 до 7 (не бессонная), или же - один только дневной сон(бессонная)
        if (totalNights == 0) totalNights = 1;

        // Считаем ночи, в которые был сон,
        // на случай, если будет 2 сна, один с 11и, пересекает полночь, и до 2х,
        // а второй - с 4 до 7, например - добавим distinct()
        // по той же причине - анализируем endTime, а не startTime
        // таким образом получим количество УНИКАЛЬНЫХ дат(и соответственно - ночей),
        // в которые сон был хорошим
        long nightsWithSleep = sessions.stream()
                .filter(SleepingSession::overlapsNightInterval)
                .map(session -> session.getEndTime().toLocalDate())
                .distinct()
                .count();

        //отнимаем количество "хороших" ночей от общего количества ночей в периоде анализа
        long sleeplessNights = totalNights - nightsWithSleep;

        return new SleepAnalysisResult("Количество бессонных ночей", sleeplessNights);
    }
}
