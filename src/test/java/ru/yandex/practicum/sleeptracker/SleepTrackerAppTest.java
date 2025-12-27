package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.classes.SleepingSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.enums.Chronotype;
import ru.yandex.practicum.sleeptracker.enums.SleepQuality;
import ru.yandex.practicum.sleeptracker.functions.*;
import java.util.List;

import static java.time.LocalDateTime.of;
import static org.junit.jupiter.api.Assertions.*;


//возможно, стоит в будущем делать отдельные классы для тестирования отдельных блоков логики

public class SleepTrackerAppTest {
    // Общие тестовые сессии
    private SleepingSession session1;  // 01.10.25 22:15 – 02.10.25 08:00 (GOOD)
    private SleepingSession session2;  // 02.10.25 23:00 – 03.10.25 08:00 (NORMAL)
    private SleepingSession session3;  // 03.10.25 14:30 – 03.10.25 15:20 (NORMAL) — дневная
    private SleepingSession session4;  // 03.10.25 23:30 – 04.10.25 06:20 (BAD)

    //инициализируем
    @BeforeEach
    void setUp() {
        session1 = new SleepingSession(
                of(2025, 10, 1, 22, 15),
                of(2025, 10, 2, 8, 0),
                SleepQuality.GOOD
        );
        session2 = new SleepingSession(
                of(2025, 10, 2, 23, 0),
                of(2025, 10, 3, 8, 0),
                SleepQuality.NORMAL
        );
        session3 = new SleepingSession(
                of(2025, 10, 3, 14, 30),
                of(2025, 10, 3, 15, 20),
                SleepQuality.NORMAL
        );
        session4 = new SleepingSession(
                of(2025, 10, 3, 23, 30),
                of(2025, 10, 4, 6, 20),
                SleepQuality.BAD
        );
    }

    // === Тесты для SessionsCountFunction ===
    @Test
    void testSessionsCountNormalCase() {
        var function = new SessionsCountFunction();
        var result = function.apply(List.of(session1, session2, session3));
        assertEquals("Общее количество сессий сна", result.getDescription());
        assertEquals(3L, result.getResult());
    }

    @Test
    void testSessionsCountEmptyList() {
        var function = new SessionsCountFunction();
        var result = function.apply(List.of());
        assertEquals(0L, result.getResult());
    }

    // === Тесты для SessionsMinDurationFunction ===
    @Test
    void testSessionsMinDurationNormalCase() {
        var function = new SessionsMinDurationFunction();
        var result = function.apply(List.of(session1, session2, session3, session4));
        long minDuration = List.of(session1, session2, session3, session4)
                .stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .min()
                .orElse(0);
        assertEquals(minDuration, result.getResult());
    }

    @Test
    void testSessionsMinDurationSingleSession() {
        var function = new SessionsMinDurationFunction();
        var result = function.apply(List.of(session1));
        assertEquals(session1.getDurationInMinutes(), result.getResult());
    }

    @Test
    void testSessionsMinDurationEmptyList() {
        var function = new SessionsMinDurationFunction();
        var result = function.apply(List.of());
        assertEquals(0L, result.getResult());
    }

    // === Тесты для SessionsMaxDurationFunction ===
    @Test
    void testSessionsMaxDurationNormalCase() {
        var function = new SessionsMaxDurationFunction();
        var result = function.apply(List.of(session1, session2, session3, session4));
        long maxDuration = List.of(session1, session2, session3, session4)
                .stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .max()
                .orElse(0);
        assertEquals(maxDuration, result.getResult());
    }

    @Test
    void testSessionsMaxDurationSingleSession() {
        var function = new SessionsMaxDurationFunction();
        var result = function.apply(List.of(session3));
        assertEquals(session3.getDurationInMinutes(), result.getResult());
    }

    @Test
    void testSessionsMaxDurationEmptyList() {
        var function = new SessionsMaxDurationFunction();
        var result = function.apply(List.of());
        assertEquals(0L, result.getResult());
    }

    // === Тесты для SessionsAvgDurationFunction ===
    @Test
    void testSessionsAvgDurationNormalCase() {
        var function = new SessionsAvgDurationFunction();
        List<SleepingSession> sessions = List.of(session1, session2, session3);
        double avg = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .average()
                .orElse(0.0);
        var result = function.apply(sessions);
        assertEquals(avg, (Double) result.getResult(), 0.001);
    }

    @Test
    void testSessionsAvgDurationSingleSession() {
        var function = new SessionsAvgDurationFunction();
        var result = function.apply(List.of(session4));
        assertEquals((double) session4.getDurationInMinutes(), (Double) result.getResult(), 0.001);
    }

    @Test
    void testSessionsAvgDurationEmptyList() {
        var function = new SessionsAvgDurationFunction();
        var result = function.apply(List.of());
        assertEquals(0.0, (Double) result.getResult(), 0.001);
    }

    // === Тесты для SessionsBadCountFunction ===
    @Test
    void testSessionsBadCountNormalCase() {
        var function = new SessionsBadCountFunction();
        var result = function.apply(List.of(session1, session2, session4));  // session4 — BAD
        assertEquals(1L, result.getResult());
    }

    @Test
    void testSessionsBadCountNoBadSessions() {
        var function = new SessionsBadCountFunction();
        var result = function.apply(List.of(session1, session2));
        assertEquals(0L, result.getResult());
    }

    @Test
    void testSessionsBadCountAllBadSessions() {
        var function = new SessionsBadCountFunction();
        SleepingSession bad1 = new SleepingSession(session1.getStartTime(), session1.getEndTime(), SleepQuality.BAD);
        SleepingSession bad2 = new SleepingSession(session2.getStartTime(), session2.getEndTime(), SleepQuality.BAD);
        var result = function.apply(List.of(bad1, bad2));
        assertEquals(2L, result.getResult());
    }

    @Test
    void testSessionsBadCountEmptyList() {
        var function = new SessionsBadCountFunction();
        var result = function.apply(List.of());
        assertEquals(0L, result.getResult());
    }

    // === Тесты для SessionsNoSleepNightsFunction ===
    @Test
    void testSessionsNoSleepNights_NoSleeplessNights() {
        var function = new SessionsNoSleepNightsFunction();
        var result = function.apply(List.of(session1, session2, session4));
        assertEquals(0L, result.getResult());
    }
    @Test
    void testSessionsNoSleepNightsEmptyList() {
        var function = new SessionsNoSleepNightsFunction();
        var result = function.apply(List.of());
        assertEquals(0L, result.getResult());
    }

    @Test
    void testSessionsNoSleepNightsOnlyDaytimeSessions() {
        var function = new SessionsNoSleepNightsFunction();
        var result = function.apply(List.of(session3));  // только дневная сессия
        assertEquals(1L, result.getResult());  // ночь 3–4 октября
    }

    // === Тесты для SessionsChronotypeDefineFunction ===
    @Test
    void testSessionsChronotypeOwl() {
        var function = new SessionsChronotypeDefineFunction();
        SleepingSession owl1 = new SleepingSession(
                of(2025, 10, 1, 23, 15),
                of(2025, 10, 2, 9, 15),
                SleepQuality.GOOD
        );
        SleepingSession owl2 = new SleepingSession(
                of(2025, 10, 2, 23, 45),
                of(2025, 10, 3, 10, 0),
                SleepQuality.NORMAL
        );

        var result = function.apply(List.of(owl1, owl2, session3));  // session3 — дневная, игнорируется
        assertEquals(Chronotype.OWL, result.getResult());
    }

    @Test
    void testSessionsChronotypeLark() {
        var function = new SessionsChronotypeDefineFunction();
        SleepingSession lark1 = new SleepingSession(
                of(2025, 10, 1, 21, 0),
                of(2025, 10, 2, 6, 30),
                SleepQuality.GOOD
        );
        SleepingSession lark2 = new SleepingSession(
                of(2025, 10, 2, 20, 45),
                of(2025, 10, 3, 6, 45),
                SleepQuality.NORMAL
        );

        var result = function.apply(List.of(lark1, lark2, session3));
        assertEquals(Chronotype.LARK, result.getResult());
    }

    @Test
    void testSessionsChronotypePigeonByMajority() {
        var function = new SessionsChronotypeDefineFunction();
        List<SleepingSession> sessions = List.of(
                // Сова
                new SleepingSession(of(2025, 10, 1, 23, 15), of(2025, 10, 2, 9, 15), SleepQuality.GOOD),
                // Жаворонок
                new SleepingSession(of(2025, 10, 2, 21, 0), of(2025, 10, 3, 6, 30), SleepQuality.NORMAL),
                // Голубь (засыпание до 23, пробуждение после 7)
                new SleepingSession(of(2025, 10, 3, 22, 0), of(2025, 10, 4, 8, 0), SleepQuality.BAD)
                );


        var result = function.apply(sessions);
        assertEquals(Chronotype.PIGEON, result.getResult());  // по 1 ночи каждого типа - голубь
    }

    @Test
    void testSessionsChronotypePigeonEmpty() {
        var function = new SessionsChronotypeDefineFunction();
        var result = function.apply(List.of());
        assertEquals(Chronotype.PIGEON, result.getResult());  // нет данных - голубь
    }

    @Test
    void testSessionsChronotypePigeonOnlyDaytime() {
        var function = new SessionsChronotypeDefineFunction();
        var result = function.apply(List.of(session3));  // только дневная сессия
        assertEquals(Chronotype.PIGEON, result.getResult());  // нет учтённых ночей - голубь
    }
}