package com.example.demo.util;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class HolidayUtils {
	private static final Set<LocalDate> holidays = new HashSet<>();

    static {
        holidays.add(LocalDate.of(2025, 1, 1));   // 元日
        holidays.add(LocalDate.of(2025, 1, 13));  // 成人の日
        holidays.add(LocalDate.of(2025, 2, 11));  // 建国記念の日
        holidays.add(LocalDate.of(2025, 2, 23));  // 天皇誕生日
        holidays.add(LocalDate.of(2025, 4, 29));  // 昭和の日
        holidays.add(LocalDate.of(2025, 5, 3));   // 憲法記念日
        holidays.add(LocalDate.of(2025, 5, 4));   // みどりの日
        holidays.add(LocalDate.of(2025, 5, 5));   // こどもの日
        holidays.add(LocalDate.of(2025, 7, 21));  // 海の日
        holidays.add(LocalDate.of(2025, 8, 11));  // 山の日
        holidays.add(LocalDate.of(2025, 9, 15));  // 敬老の日
        holidays.add(LocalDate.of(2025, 9, 23));  // 秋分の日
        holidays.add(LocalDate.of(2025, 10, 13)); // スポーツの日
        holidays.add(LocalDate.of(2025, 11, 3));  // 文化の日
        holidays.add(LocalDate.of(2025, 11, 23)); // 勤労感謝の日
    }

    /**
     * 指定日が祝日（固定リスト）であれば true を返します。
     */
    public static boolean isHoliday(LocalDate date) {
        return holidays.contains(date);
    }
}
