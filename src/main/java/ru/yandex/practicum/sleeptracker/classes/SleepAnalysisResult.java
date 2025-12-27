package ru.yandex.practicum.sleeptracker.classes;

//класс, который был рекомандован к созданию по ТЗ для хранения результата анализа
//и возможности вывода результата, понятного для пользователя (описание метода - результат)
public class SleepAnalysisResult {
    private final String description;
    private final Object result;

    public SleepAnalysisResult(String description, Object result) {
        this.description = description;
        this.result = result;
    }

    @Override
    public String toString() {
        return description + ": " + result;
    }

    public String getDescription() {
        return description;
    }

    public Object getResult() {
        return result;
    }
}