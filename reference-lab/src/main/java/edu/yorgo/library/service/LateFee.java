package edu.yorgo.library.service;

import java.math.BigDecimal;

public record LateFee(long lateDays, BigDecimal amount) {
}
