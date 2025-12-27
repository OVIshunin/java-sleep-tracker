package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.classes.*;
import ru.yandex.practicum.sleeptracker.enums.SleepQuality;
import ru.yandex.practicum.sleeptracker.functions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    //здесь будет список функций для проверки и анализа, у всех общий интерфейс
    private final List<SleepAnalysisFunctionInterface> analysisFunctions = new ArrayList<>();

    public SleepTrackerApp() {
        // Добавляем функции в список
        analysisFunctions.add(new SessionsCountFunction());             //Количество сессий
        analysisFunctions.add(new SessionsMinDurationFunction());       //Минимальная сессия
        analysisFunctions.add(new SessionsMaxDurationFunction());       //Максимальная сессия
        analysisFunctions.add(new SessionsAvgDurationFunction());       //Средняя сессия
        analysisFunctions.add(new SessionsBadCountFunction());          //Сессии с не качественным сном
        analysisFunctions.add(new SessionsNoSleepNightsFunction());     //Бессонные ночи
        analysisFunctions.add(new SessionsChronotypeDefineFunction());  //Хронотипы
    }

    //парсим лог, вытаскиваем строки, режем, прокидываем в конструктор сессии, сессии собираем в список
    public List<SleepingSession> parseLog(String filePath) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        return Files.readAllLines(Paths.get(filePath)).stream()
                .map(line -> line.split(";"))
                .filter(parts -> parts.length == 3)
                .map(parts -> {
                    LocalDateTime start = LocalDateTime.parse(parts[0], formatter);
                    LocalDateTime end = LocalDateTime.parse(parts[1], formatter);
                    SleepQuality quality = SleepQuality.valueOf(parts[2]);
                    return new SleepingSession(start, end, quality);
                })
                .collect(Collectors.toList());
    }

    //полученный список сессий прогоняем через все добавленные функции анализа,
    //результаты возвращаем списком объектов-результатов
    public List<SleepAnalysisResult> analyze(List<SleepingSession> sessions) {
        return analysisFunctions.stream()
                .map(function -> function.apply(sessions))
                .collect(Collectors.toList());
    }

    //проверяем, что в параметрах main указан параметр, создаем объект приложения,
    //загружаем сессии из файла, прогоняем по функциям, возвращаем результаты
    //согласно ТЗ весь вывод должен быть в методе main
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Ошибка: укажите путь к файлу с логом сна");
            return;
        }

        SleepTrackerApp app = new SleepTrackerApp();

        try {
            List<SleepingSession> sessions = app.parseLog(args[0]);
            List<SleepAnalysisResult> results = app.analyze(sessions);

            results.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка в данных файла: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}