package edu.yorgo.library.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LoanPolicyTest {

    private final LoanPolicy policy = new LoanPolicy();

    @Test
    void calculatesDueDateAndLateFeeBoundaries() {
        LocalDate borrowedOn = LocalDate.of(2026, 11, 10);
        LocalDate dueDate = policy.dueDateFor(borrowedOn);

        assertThat(dueDate).isEqualTo(LocalDate.of(2026, 11, 24));
        assertThat(policy.lateFee(dueDate, dueDate).amount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(policy.lateFee(dueDate, dueDate.plusDays(4)).amount())
                .isEqualByComparingTo(new BigDecimal("2.00"));
    }
}
