package com.test.intelliartstestapp.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LatestCurrencyRateDto {
    private boolean success;
    private float timestamp;
    private String base;
    private String date;
    private Map<String, String> rates;
}
