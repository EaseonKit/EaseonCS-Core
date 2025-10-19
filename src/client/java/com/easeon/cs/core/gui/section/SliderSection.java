package com.easeon.cs.core.gui.section;

import com.easeon.cs.core.api.EaseonFeatureType;
import com.easeon.cs.core.config.EaseonConfig;
import com.easeon.cs.core.config.GuiConfig;
import com.easeon.cs.core.config.StringKey;
import com.easeon.cs.core.config.model.FeatureStruct;
import com.easeon.cs.core.config.model.SliderConfig;
import com.easeon.cs.core.gui.EaseonScreen;
import com.easeon.cs.core.gui.common.GuiRenderable;
import com.easeon.cs.core.gui.widget.EaseonButton;
import com.easeon.cs.core.gui.widget.EaseonSlider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SliderSection implements GuiRenderable {
    private final EaseonScreen _screen;
    private final FeatureStruct _struct;
    private final EaseonFeatureType _type;
    private final SliderConfig _config;

    public final EaseonSlider slider;
    public final ButtonWidget toggleButton;
    private final ButtonWidget resetButton;

    private int _y = 0;

    public SliderSection(EaseonScreen screen, EaseonFeatureType type) {
        this._struct = EaseonConfig.configMap.get(type);
        this._config = (SliderConfig)this._struct;
        this._screen = screen;
        this._type = type;

        this.slider = new EaseonSlider(
            GuiConfig.getHotkeyButtonX(),
            _y,
            GuiConfig.SLIDER_WIDTH,
            GuiConfig.WIDGET_HEIGHT,
            this._config.Value / (double)type.MaxValue,
            this._config,
            this._type
        );
        this.slider.setOnValueChanged(this::refreshUI);

        this.toggleButton = new EaseonButton(
            getToggleButtonText(),
            GuiConfig.getToggleButtonX(),
            btn -> {
                this._config.Enabled = !this._config.Enabled;
                this.refreshUI();
            }
        );

        this.resetButton = new EaseonButton(
            StringKey.BUTTON_RESET.asText(),
            GuiConfig.getResetButtonX(),
            btn -> {
                EaseonConfig.reset(this._type);
                this.refreshUI();
            }
        );

        screen.registerChild2(this.slider);
        screen.registerChild(this.toggleButton);
        screen.registerChild(this.resetButton);
        refreshUI();
    }

    @Override
    public void refreshUI() {
        this.slider.SetValue();
        this.toggleButton.setMessage(getToggleButtonText());
        this.resetButton.active = this._type.isChanged(this._struct);
        this._screen.refreshUI();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        var tr    = MinecraftClient.getInstance().textRenderer;
        int fontH = tr.fontHeight;
        int rowH  = this.toggleButton.getHeight();  // WIDGET_HEIGHT 와 동일
        int textY = _y + (rowH - fontH) / 2;

        context.drawText(tr, this._type.getTitle(), GuiConfig.PADDING * 3, textY, GuiConfig.FONT_COLOR, GuiConfig.FONT_SHADOW);

        this.slider.render(context, mouseX, mouseY, delta);
        this.toggleButton.render(context, mouseX, mouseY, delta);
        this.resetButton.render(context, mouseX, mouseY, delta);
    }

    private Text getToggleButtonText()
    {
        return Text.translatable(
            this._config.Enabled
                ? StringKey.BUTTON_TOGGLE_ON.getKey()
                : StringKey.BUTTON_TOGGLE_OFF.getKey()
        );
    }

    @Override
    public void SetY(int y) {
        _y = y;
        this.slider.setY(y);
        this.toggleButton.setY(y);
        this.resetButton.setY(y);
    }
}