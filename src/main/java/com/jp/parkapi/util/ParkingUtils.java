package com.jp.parkapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingUtils {

    public static String createReceipt() {
        LocalDateTime date = LocalDateTime.now();
        String receipt = date.toString().substring(0,19); //2024-02-29T18:01:45.61548706   -> 0 from 19 -> 2024-02-29T18:01:45
        return receipt.replace("-", "").replace(":", "").replace("T", "-"); // 20240229-180145
    }
}
