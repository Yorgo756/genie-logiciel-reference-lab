package edu.yorgo.library.dto;

import java.math.BigDecimal;

public record LateFeeResponse(String currency, long lateDays, BigDecimal amount) {
}
