package com.jp.parkapi.repositories.projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ClientSpaceProjection {

    String getPlate();

    String getManufacturer();

    String getModel();

    String getColor();

    String getClientCpf();

    String getReceipt();

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    LocalDateTime getEntryDate();

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    LocalDateTime getExitDate();

    String getParkingSpaceCode();

    BigDecimal getValue();

    BigDecimal getDiscount();
}
