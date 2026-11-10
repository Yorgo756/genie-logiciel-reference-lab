package edu.yorgo.library.web;

import java.math.BigDecimal;

public record LateFeeResponse(String currency, long lateDays, BigDecimal amount) {
}
