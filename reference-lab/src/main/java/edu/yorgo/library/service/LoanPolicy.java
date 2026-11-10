package edu.yorgo.library.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class LoanPolicy {

    static final int LOAN_DAYS = 14;
    private static final BigDecimal DAILY_LATE_FEE = new BigDecimal("0.50");

    public LocalDate dueDateFor(LocalDate borrowedOn) {
        return borrowedOn.plusDays(LOAN_DAYS);
    }

    public LateFee lateFee(LocalDate dueDate, LocalDate chargeThrough) {
        long lateDays = Math.max(0, ChronoUnit.DAYS.between(dueDate, chargeThrough));
        BigDecimal amount = BigDecimal.valueOf(lateDays).multiply(DAILY_LATE_FEE).setScale(2);
        return new LateFee(lateDays, amount);
    }
}
