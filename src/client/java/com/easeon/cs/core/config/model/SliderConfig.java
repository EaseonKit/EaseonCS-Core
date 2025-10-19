package com.easeon.cs.core.config.model;

public class SliderConfig implements FeatureStruct {
    public boolean Enabled = false;
    public int Value = 0;

    @Override
    public void applyDefaults() {
        FeatureStruct.super.applyDefaults();
    }
}

