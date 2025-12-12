package com.easeon.cs.core.config;

import net.minecraft.client.Minecraft;

public final class GuiConfig {
    private GuiConfig() {}

    // Widget
    public static final int SIDE_VIEW_WIDTH = 90;
    public static final int PADDING = 5;
    public static final int WIDGET_HEIGHT = 20;
    public static final int WIDGET_WIDTH = 80;
    public static final int BUTTON_WIDTH = 50;
    public static final int SLIDER_WIDTH = 80;
    public static final int SPACING = 2;
    public static final float TAB_WIDTH_RATIO = 0.25f; // ScrollView.width 대비 백분율

    // Font
    public static final int GROUP_TITLE_FONT_COLOR = 0xFFFFF555;
    public static final int FONT_COLOR = 0xFFFFFFFF;
    public static final boolean FONT_SHADOW = true;

    // HUD Font
    public static final int HUD_FONT_COLOR =  0xFFFFFFFF;
    public static final int HUD_BG_COLOR = 0x00000000;
    public static final int HUD_LIGHT = 15728880;
    public static final int HUD_DIRECTION_COLOR = 0xFFFFF555;

    // Scroll
    public static final int SCROLL_VIEW_BACKGROUND_COLOR = 0x80000000;
    public static final int SCROLL_VIEW_BORDER_COLOR1 = 0x80808080;
    public static final int SCROLL_VIEW_BORDER_COLOR2 = 0xC0000000;

    public static final int SCROLL_TRACK_COLOR = 0xFF000000;
    public static final int SCROLL_BAR_INNER    = 0x80C0C0C0;
    public static final int SCROLL_BAR_BORDER   = 0x80808080;

    public static int getResetButtonX() {
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int scrollViewX2 = width - SIDE_VIEW_WIDTH - PADDING * 2;
        return scrollViewX2 - PADDING * 3 - BUTTON_WIDTH;
    }

    public static int getToggleButtonX() {
        return getResetButtonX() - SPACING - BUTTON_WIDTH;
    }

    public static int getHotkeyButtonX() {
        return getToggleButtonX() - SPACING - SLIDER_WIDTH;
    }
}