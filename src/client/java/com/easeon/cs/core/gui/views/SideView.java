package com.easeon.cs.core.gui.views;

import com.easeon.cs.core.config.EaseonConfig;
import com.easeon.cs.core.config.GuiConfig;
import com.easeon.cs.core.config.StringKey;
import com.easeon.cs.core.gui.EaseonScreen;
import com.easeon.cs.core.gui.common.GuiRenderable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;

public class SideView implements GuiRenderable {
    private final int width, height;
    public final ButtonWidget save;
    public final ButtonWidget reset;
    public final ButtonWidget close;

    public SideView(EaseonScreen screen) {
        var x = MinecraftClient.getInstance().getWindow().getScaledWidth() - GuiConfig.PADDING * 2 - GuiConfig.WIDGET_WIDTH;
        width = screen.width;
        height = screen.height;

        save = ButtonWidget.builder(StringKey.BUTTON_SAVE.asText(),
            btn -> {
                EaseonConfig.save();
                screen.close();
            }
        )
        .dimensions(x, calcY(0), GuiConfig.WIDGET_WIDTH, GuiConfig.WIDGET_HEIGHT)
        .build();
        reset = ButtonWidget.builder(StringKey.BUTTON_RESET.asText(),
            btn -> {
                EaseonConfig.resetAll();
                for (var widget : screen.widgets) {
                    widget.refreshUI();
                }
            }
        )
        .dimensions(x, calcY(1), GuiConfig.WIDGET_WIDTH, GuiConfig.WIDGET_HEIGHT)
        .build();
        close = ButtonWidget.builder(StringKey.BUTTON_CLOSE.asText(),
            btn -> {
                EaseonConfig.load();
                screen.close();
            }
        )
        .dimensions(x, calcY(2), GuiConfig.WIDGET_WIDTH, GuiConfig.WIDGET_HEIGHT)
        .build();

        screen.registerChild(save);
        screen.registerChild(reset);
        screen.registerChild(close);
    }

    private static int calcY(int idx) {
        int y = MinecraftClient.getInstance().getWindow().getScaledHeight();
        y -= GuiConfig.PADDING * 2;
        y -= GuiConfig.WIDGET_HEIGHT * (3 - idx);
        y -= GuiConfig.SPACING * (3 - idx);
        return y;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Side
        var x1 = this.width - GuiConfig.SIDE_VIEW_WIDTH - GuiConfig.PADDING;
        var x2 = this.width - GuiConfig.PADDING;
        var y1 = GuiConfig.PADDING + 2 + GuiConfig.WIDGET_HEIGHT;
        var y2 = this.height - GuiConfig.PADDING - 2;

        context.fill(x1, y1 - 2, x2, y1 - 1, GuiConfig.SCROLL_VIEW_BORDER_COLOR1);
        context.fill(x1, y1 - 1, x2, y1, GuiConfig.SCROLL_VIEW_BORDER_COLOR2);
        context.fill(x1, y1, x2, y2, GuiConfig.SCROLL_VIEW_BACKGROUND_COLOR);
        context.fill(x1, y2, x2, y2 + 1, GuiConfig.SCROLL_VIEW_BORDER_COLOR2);
        context.fill(x1, y2 + 1, x2, y2 + 2, GuiConfig.SCROLL_VIEW_BORDER_COLOR1);

        save.render(context, mouseX, mouseY, delta);
        reset.render(context, mouseX, mouseY, delta);
        close.render(context, mouseX, mouseY, delta);
    }

    @Override public void SetY(int y) { }
    @Override public void refreshUI() {}
}
