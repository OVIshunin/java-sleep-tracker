package ru.yandex.practicum.sleeptracker.classes;

import ru.yandex.practicum.sleeptracker.enums.Chronotype;
import ru.yandex.practicum.sleeptracker.enums.SleepQuality;

import java.time.LocalDateTime;

//класс для хранения сессии сна,
//в который удобно распарсить каждую отдельную строку
//в нём же методы по определению хорошей-плохой сессии сна,
//и по определению хронотипа для одной конкретной сессии

public class SleepingSession {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime startTime, LocalDateTime endTime, SleepQuality quality) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public long getDurationInMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public boolean overlapsNightInterval() {
        LocalDateTime start = this.startTime;
        LocalDateTime end = this.endTime;

        /*для получения полночи важно брать не начало, а конец сессии сна. Тогда если засыпание и подъем
        были в одних сутках - то и полночь и 6 утра нужно проверять в этих сутках, а если засыпание в первый день,
        а пробуждение во второй - то именно от него мы берем полночь и 6 утра, чтобы проверять переход через сутки

        есть 4 варианта пересечения интервала 0:00 - 6:00,и пятый - без пересечения:
        1. Полное перекрытие, когда легли до 12, и встали после 6, тогда - это условие попадает в 1ю проверку -
        когда старт сна до полуночи, а конец - после
        2. Когда сон полностью входит внутрь этого диапазона (как в примере 02:00-05:00 - не бессонная ночь),
        тогда 2 проверка поймает этот вариант, как хороший, так как она проверяет наличие начала сна - внутри этого
        диапазона
        3. когда сон начался в пределах диапазона 0-6 - это тоже вторая проверка
        4. когда сон закончился в пределах этого диапазона - тут сработает 1 проверка, ведь если сон начался до 12,
        и закончился в пределах 0-6 - это пересечение полуночи
        5. Пятый же вариант - исключает все предыдущие, поэтому если первые 4 проверки не прошли - то сессия сна будет
        считаться плохой.

        Этот метод позволит отловить плохие сессии, а вот считать плохие ночи мы будем уже в функции, поскольку она
        позволит учитывать ещё и варианты, когда в день было 2 сна, и один днём, а второй - с хорошей ночью

        п.с. возможно - много написал, но на эту часть алгоритма было потрачено больше всего времени :)
        */
        LocalDateTime nightStart = end.toLocalDate().atTime(0, 0);
        LocalDateTime nightEnd = nightStart.plusHours(6);

//        System.out.println(nightStart + " - полночь");              //на время теста функции
//        System.out.println(nightEnd + " - конец ночи, утро");       //на время теста функции
//        System.out.println(start + " - " + end);                    //на время теста функции

        //если сон начался до полуночи и закончился после
        if (start.isBefore(nightStart) && end.isAfter(nightStart)) {
            //System.out.println("хорошая сессия - через полночь");//на время теста функции
            return true;
        }
        //если спал в пределах периода между 0 и 6 утра
        //переделал на нестрогие границы - вместо isBefore - !(isAfter), и наоборот
        if ((!nightStart.isAfter(start)) && (!nightEnd.isBefore(start))) {
            //System.out.println("хорошая сессия - начало сна попало в диапазон 0-6");//на время теста функции
            return true;
        }

        //переделал на нестрогие границы - вместо isBefore - !(isAfter), и наоборот
        if (!(nightStart.isAfter(end)) && (!nightEnd.isBefore(end))) {
            //System.out.println("хорошая сессия - конец сна попал в диапазон 0-6");//на время теста функции
            return true;
        }

        //System.out.println("плохая сессия");//на время теста функции
        return false;

    }

    //Определяем хронотип для каждой отдельной сессии
    public Chronotype classifyNight() {
        LocalDateTime start = this.startTime;
        LocalDateTime end = this.endTime;

        // Игнорируем бессонные ночи и дневные сессии
        //у нас уже есть метод, определяющий "плохие сессии",
        // в них не попадут дневные сны и бессонные ночи
        if (!overlapsNightInterval()) {
            return null; // не учитываем
        }

        //далее - вынем "часы" из времени засыпания и просыпаня и сравним
        int sleepHour = start.getHour();
        int wakeHour = end.getHour();

        //засыпание после 23 и пробуждение после 9
        if (sleepHour >= 23 && wakeHour >= 9) {
            return Chronotype.OWL;
        }

        //засыпание до 22 и пробуждение до 7
        if (sleepHour < 22 && wakeHour < 7) {
            return Chronotype.LARK;
        }

        //остальное - голуби
        return Chronotype.PIGEON;
    }
}