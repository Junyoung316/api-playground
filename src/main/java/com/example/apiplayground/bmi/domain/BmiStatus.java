package com.example.apiplayground.bmi.domain;

import lombok.Getter;

@Getter
public enum BmiStatus {
    UNDERWEIGHT("저체중"),
    NORMAL("정상"),
    OVERWEIGHT("과체중"),
    OBESE("비만");

    private final String description;

    BmiStatus(String description) {
        this.description = description;
    }

    public static BmiStatus from(double bmi) {
        if (bmi < 18.5) return UNDERWEIGHT;
        else if (bmi < 23) return NORMAL;
        else if (bmi < 25) return OVERWEIGHT;
        else return OBESE;
    }

}
