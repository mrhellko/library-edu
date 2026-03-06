package ru.mrhellko.library.Enum;

public enum StatusCopy {
    ISSUED(0),
    AVAILABLE_FOR_ISSUE(1),
    ARCHIVED(2),
    UNDER_RESTORATION(3);

    private final int value;

    StatusCopy(int value) {
        this.value = value;
    }

    public static StatusCopy parse(int value) {
        for (StatusCopy statusCopy : StatusCopy.values()) {
            if (statusCopy.value == value) {
                return statusCopy;
            }
        }
        throw new IllegalArgumentException("Unknown status copy value: " + value);
    }
}
