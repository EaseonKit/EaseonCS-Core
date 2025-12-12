package com.easeon.cs.core.gui.widget;

import com.easeon.cs.core.config.GuiConfig;
import com.easeon.cs.core.gui.common.GuiRenderable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class SectionHeader implements GuiRenderable {
    private final Component _text;
    private int _y = 0;

    public SectionHeader(String stringKey) {
        _text = Component.translatable(stringKey);
    }

    @Override
    public void SetY(int y) { _y = y; }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        Font font = Minecraft.getInstance().font;
        var x = GuiConfig.PADDING * 3;
        var fontH = font.lineHeight;
        var textY = _y + (GuiConfig.WIDGET_HEIGHT - fontH) / 2;

        guiGraphics.drawString(font, _text, x, textY, GuiConfig.GROUP_TITLE_FONT_COLOR, GuiConfig.FONT_SHADOW);
    }

    @Override
    public void refreshUI() {}
}