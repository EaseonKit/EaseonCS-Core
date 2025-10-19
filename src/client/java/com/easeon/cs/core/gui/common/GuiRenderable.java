package com.easeon.cs.core.gui.common;

import net.minecraft.client.gui.DrawContext;

public interface GuiRenderable {
    void refreshUI();
    void SetY(int y);
    void render(DrawContext context, int mouseX, int mouseY, float delta);
}
