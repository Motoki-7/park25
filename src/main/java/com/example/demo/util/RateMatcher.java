package com.example.demo.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.demo.entity.RangeEntity;

public class RateMatcher {

    /**
     * dateTime に対応する RangeEntity が有効かどうかを返します。
     * 祝日フラグが true のレンジが優先されます。
     */
    public static boolean isRateApplicable(LocalDateTime dateTime, RangeEntity rangeEnt) {
        LocalDate date = dateTime.toLocalDate();

        // 祝日判定が最優先
        if (HolidayUtils.isHoliday(date)) {
            return rangeEnt.isHoliday();
        }

        DayOfWeek day = dateTime.getDayOfWeek();
        switch (day) {
            case MONDAY:    return rangeEnt.isMonday();
            case TUESDAY:   return rangeEnt.isTuesday();
            case WEDNESDAY: return rangeEnt.isWednesday();
            case THURSDAY:  return rangeEnt.isThursday();
            case FRIDAY:    return rangeEnt.isFriday();
            case SATURDAY:  return rangeEnt.isSaturday();
            case SUNDAY:    return rangeEnt.isSunday();
            default:        return false;
        }
    }
}
