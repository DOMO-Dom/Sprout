package com.habitdev.sprout.ui.menu.home.enums;

public enum ConfigurationKeys {

    HOME_SHAREDPREF("HOME_SHARED.PREF"),
    HOME_HABIT_ON_MODIFY_SHARED_PREF("HOME_HABIT_ON_MODIFY_SHARED.PREF"),

    IS_ON_ADD_DEFAULT("ON_ADD_DEAFAULT.BOOL"),
    SELECTED_COLOR("SELECTED_COLOR.INT"),
    SELECTED_HABIT("SELECTED_HABIT.STRING"),



    IS_ON_ADD_NEW("ON_ADD_NEW.BOOL"),
    IS_ON_ITEM_CLICK("ON_ITEM_CLICK"),
    IS_ON_MODIFY("ON_MODIFY.BOOL"),

    HABIT("TITLE.HABIT"),
    TITLE("TITLE.STRING"),
    DESCRIPTION("DESCRIPTION.STRING"),
    POSITION("POSITION.INT"),
    HINT_TEXT("HINT.STRING");

    final String value;

    ConfigurationKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
