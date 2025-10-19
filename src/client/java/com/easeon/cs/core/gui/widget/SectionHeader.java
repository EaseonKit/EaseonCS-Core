package com.easeon.cs.core.gui.widget;

import com.easeon.cs.core.config.GuiConfig;
import com.easeon.cs.core.gui.common.GuiRenderable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class SectionHeader implements GuiRenderable {
    private final Text _text;
    private int _y = 0;
    public SectionHeader(String stringKey) {
        _text = Text.translatable(stringKey);
    }

    @Override
    public void SetY(int y) { _y = y; }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        var tr = MinecraftClient.getInstance().textRenderer;
        var x = GuiConfig.PADDING * 3;
        var fontH = tr.fontHeight;
        var textY = _y + (GuiConfig.WIDGET_HEIGHT - fontH) / 2;

        context.drawText(tr, _text, x, textY, GuiConfig.GROUP_TITLE_FONT_COLOR, GuiConfig.FONT_SHADOW);
    }

    @Override public void refreshUI() {}
}
