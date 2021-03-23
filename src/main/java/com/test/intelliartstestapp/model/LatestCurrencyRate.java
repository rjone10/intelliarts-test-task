package com.test.intelliartstestapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LatestCurrencyRate {
    private boolean success;
    private float timestamp;
    private String base;
    private String date;
    private Map<String, String> rates;
}
