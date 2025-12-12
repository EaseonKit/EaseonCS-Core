package com.easeon.cs.core.gui.common;

import net.minecraft.client.gui.GuiGraphics;

public interface GuiRenderable {
    void refreshUI();
    void SetY(int y);
    void render(GuiGraphics context, int mouseX, int mouseY, float delta);
}
