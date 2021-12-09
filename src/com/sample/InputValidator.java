package com.sample;

public class InputValidator {

    public static boolean isPatientId(String s) {
        return s.matches("^[a-zA-Z0-9]+$");
    }

    public static boolean isName(String s) {
        return s.matches("^ *([a-zA-Z]+ *)+$");

    }

    public static boolean isPhone(String s) {
        return s.matches("^[0-9]{5,}$");
    }

    public static boolean isEmail(String s) {
        return s.matches("^[\\w+\\\\.?\\w+]+@[a-zA-Z_]+?\\.[a-zA-Z]+$");
    }

    public static boolean isMedicalConditionsString(String s) {
        return s.matches("^( *[a-zA-Z]+ *[a-zA-Z]*,)*( *[a-zA-Z]+ *[a-zA-Z]*){1}$");
    }
}
