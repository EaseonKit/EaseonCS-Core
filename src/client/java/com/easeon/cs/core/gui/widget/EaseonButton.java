package com.easeon.cs.core.gui.widget;

import com.easeon.cs.core.config.GuiConfig;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EaseonButton extends ButtonWidget {
    public EaseonButton(Text text, int x, PressAction onPress) {
        super(x, 0,
            GuiConfig.BUTTON_WIDTH,
            GuiConfig.WIDGET_HEIGHT,
            text,
            onPress,
            ButtonWidget.DEFAULT_NARRATION_SUPPLIER
        );
    }

    public EaseonButton(Text text, int x, int w, PressAction onPress) {
        super(x, 0, w, GuiConfig.WIDGET_HEIGHT, text, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
    }
}
