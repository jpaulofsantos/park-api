package com.jp.parkapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingUtils {

    private static final double PRIMEIROS_15_MINUTES = 5.00;
    private static final double PRIMEIROS_60_MINUTES = 9.25;
    private static final double ADICIONAL_15_MINUTES = 1.75;

    private static final double DESCONTO_PERCENTUAL = 0.30;

    public static String createReceipt() {
        LocalDateTime date = LocalDateTime.now();
        String receipt = date.toString().substring(0,19); //2024-02-29T18:01:45.61548706   -> 0 from 19 -> 2024-02-29T18:01:45
        return receipt.replace("-", "").replace(":", "").replace("T", "-"); // 20240229-180145
    }

    public static BigDecimal calculateCost(LocalDateTime entrada, LocalDateTime saida) {
        long minutes = entrada.until(saida, ChronoUnit.MINUTES);
        double total = 0.0;

        if (minutes <= 15) {
            total = PRIMEIROS_15_MINUTES;
        } else if (minutes <= 60) {
            total = PRIMEIROS_60_MINUTES;
        } else {
            long addicionalMinutes = minutes - 60;
            Double totalParts = ((double) addicionalMinutes / 15);
            if (totalParts > totalParts.intValue()) {
                total += PRIMEIROS_60_MINUTES + (ADICIONAL_15_MINUTES * (totalParts.intValue() + 1));
            } else {
                total += PRIMEIROS_60_MINUTES + (ADICIONAL_15_MINUTES * totalParts.intValue());
            }
        }
        return new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calculateDiscount(BigDecimal custo, long numeroDeVezes) {
        BigDecimal desconto = null;
        if (numeroDeVezes > 0 && numeroDeVezes % 10 == 0) {
            desconto = custo.multiply(new BigDecimal(DESCONTO_PERCENTUAL));
        } else {
            desconto = new BigDecimal(0);
        }
        return desconto.setScale(2, RoundingMode.HALF_EVEN);
    }
}

