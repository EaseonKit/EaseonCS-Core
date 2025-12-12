package com.easeon.cs.core.config;

import net.minecraft.network.chat.Component;

public enum StringKey {
    TITLE_SETTINGS("easeon.title.settings"),
    BUTTON_SAVE("easeon.button.save"),
    BUTTON_RESET("easeon.button.reset"),
    BUTTON_CLOSE("easeon.button.close"),
    BUTTON_TOGGLE_ON("easeon.button.toggle_on"),
    BUTTON_TOGGLE_OFF("easeon.button.toggle_off"),
    BUTTON_HOTKEY_EMPTY("easeon.button.hotkey.empty");

    private final String key;
    StringKey(String key) { this.key = key; }
    public String getKey() { return key; }
    public Component asText(Object... args) { return Component.translatable(key, args); }
    public String asString(Object... args) { return Component.translatable(key, args).getString(); }
}