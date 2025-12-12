package com.easeon.cs.core.gui.views;

import com.easeon.cs.core.api.EaseonFeatureCategory;
import com.easeon.cs.core.config.GuiConfig;
import com.easeon.cs.core.gui.EaseonScreen;
import com.easeon.cs.core.gui.common.GuiRenderable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TabSection implements GuiRenderable {
    public final List<Button> tabList = new ArrayList<>();

    public TabSection(EaseonScreen screen) {
        var panels = EaseonFeatureCategory.values();
        var width = screen.scrollView.getWidth() / (panels.length + 1);
        for (var i = 0; i < panels.length; i++) {
            final int index = i;
            var aBtn = Button.builder(Component.translatable(panels[index].getTitle()),
                btn -> {
                    screen.tabIndex = index;
                    screen.init();
                }
            )
            .bounds(screen.scrollView.x1 + index * width, GuiConfig.PADDING, width, GuiConfig.WIDGET_HEIGHT)
            .build();
            aBtn.active = screen.tabIndex != index;
            tabList.add(aBtn);
        }

        for (var tab : tabList) {
            screen.registerChild(tab);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        for (var tab : tabList) {
            tab.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public void refreshUI() { }

    @Override
    public void SetY(int y) { }
}
