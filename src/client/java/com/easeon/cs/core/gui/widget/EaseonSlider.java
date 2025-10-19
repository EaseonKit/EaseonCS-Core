package com.easeon.cs.core.gui.widget;

import com.easeon.cs.core.api.EaseonFeatureType;
import com.easeon.cs.core.config.model.SliderConfig;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class EaseonSlider extends SliderWidget {
    private Runnable onValueChanged;
    private final SliderConfig _config;
    private final EaseonFeatureType _type;

    public EaseonSlider(int x, int y, int width, int height, double initial, SliderConfig config, EaseonFeatureType type) {
        super(x, y, width, height, Text.empty(), initial);
        _config = config;
        _type = type;
        updateMessage();  // 초기 텍스트 표시
    }

    @Override
    protected void updateMessage() {
        this.setMessage(_type.getSliderText(_config.Value));
    }

    @Override
    protected void applyValue() {
        int min = _type.MinValue;
        int max = _type.MaxValue;

        // 0~1 → min~max 변환
        int newVal = (int)Math.round(this.value * (max - min)) + min;
        _config.Value = MathHelper.clamp(newVal, min, max);

        updateMessage();
        if (onValueChanged != null) {
            onValueChanged.run();
        }
    }

    public void SetValue() {
        double min = _type.MinValue;
        double max = _type.MaxValue;

        // min~max → 0~1 변환
        this.value = (_config.Value - min) / (max - min);
        updateMessage();
    }

    public void setOnValueChanged(Runnable callback) {
        this.onValueChanged = callback;
    }
}