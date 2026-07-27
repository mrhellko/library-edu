package ru.mrhellko.library.Enum;

import lombok.Getter;

@Getter
public enum Quality {
    EXCELLENT(0),
    GOOD(1),
    SATISFACTORY(2),
    UNSATISFACTORY(3);

    private final int value;

    Quality(int value) {
        this.value = value;
    }

    public static Quality parse(int value) {
        for (Quality quality : Quality.values()) {
            if (quality.value == value) {
                return quality;
            }
        }
        throw new IllegalArgumentException("Unknown quality value: " + value);
    }
}
