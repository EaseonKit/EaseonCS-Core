package com.easeon.cs.core.gui.section;

import com.easeon.cs.core.api.EaseonFeatureType;
import com.easeon.cs.core.config.EaseonConfig;
import com.easeon.cs.core.config.GuiConfig;
import com.easeon.cs.core.config.StringKey;
import com.easeon.cs.core.config.model.FeatureStruct;
import com.easeon.cs.core.config.model.ToggleConfig;
import com.easeon.cs.core.gui.EaseonScreen;
import com.easeon.cs.core.gui.common.GuiRenderable;
import com.easeon.cs.core.gui.widget.EaseonButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ToggleSection implements GuiRenderable {
    private final EaseonScreen _screen;
    private final FeatureStruct _struct;
    private final EaseonFeatureType _type;
    private final ToggleConfig _config;

    public final ButtonWidget toggleButton;
    public final ButtonWidget resetButton;
    private int _y = 0;

    public ToggleSection(EaseonScreen screen, EaseonFeatureType type)
    {
        this._struct = EaseonConfig.configMap.get(type);
        this._config = (ToggleConfig)this._struct;
        this._screen = screen;
        this._type = type;

        toggleButton = new EaseonButton(
                getToggleButtonText(),
                GuiConfig.getToggleButtonX(),
                btn -> {
                    this._config.Enabled = !this._config.Enabled;
                    this.refreshUI();
                }
        );

        resetButton = new EaseonButton(
                StringKey.BUTTON_RESET.asText(),
                GuiConfig.getResetButtonX(),
                btn -> {
                    EaseonConfig.reset(this._type);
                    this.refreshUI();
                }
        );

        screen.registerChild(toggleButton);
        screen.registerChild(resetButton);
        refreshUI();
    }

    private Text getToggleButtonText()
    {
        return this._config.Enabled
            ? StringKey.BUTTON_TOGGLE_ON.asText()
            : StringKey.BUTTON_TOGGLE_OFF.asText();
    }

    @Override
    public void refreshUI() {
        this.toggleButton.setMessage(getToggleButtonText());
        this.resetButton.active = _type.isChanged(this._struct);
        _screen.refreshUI();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        var tr = MinecraftClient.getInstance().textRenderer;
        int fontH = tr.fontHeight;
        int rowH   = toggleButton.getHeight();  // WIDGET_HEIGHT 와 동일
        int textY = _y + (rowH - fontH) / 2;

        context.drawText(tr, this._type.getTitle(), GuiConfig.PADDING * 3, textY, GuiConfig.FONT_COLOR, GuiConfig.FONT_SHADOW);

        toggleButton.render(context, mouseX, mouseY, delta);
        resetButton.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void SetY(int y) {
        _y = y;
        this.toggleButton.setY(y);
        this.resetButton.setY(y);
    }
}
