package com.easeon.cs.core.gui.widget;

import com.easeon.cs.core.config.GuiConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class EaseonButton extends Button.Plain {
    public EaseonButton(Component text, int x, OnPress onPress) {
        super(x, 0, GuiConfig.BUTTON_WIDTH, GuiConfig.WIDGET_HEIGHT, text, onPress, Button.DEFAULT_NARRATION);
    }

    public EaseonButton(Component text, int x, int w, OnPress onPress) {
        super(x, 0, w, GuiConfig.WIDGET_HEIGHT, text, onPress, Button.DEFAULT_NARRATION);
    }

    public static EaseonButton create(Component text, int x, OnPress onPress) {
        return new EaseonButton(text, x, onPress);
    }

    public static EaseonButton create(Component text, int x, int w, OnPress onPress) {
        return new EaseonButton(text, x, w, onPress);
    }
}