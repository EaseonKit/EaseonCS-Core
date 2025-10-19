package com.easeon.cs.core.config.model;

public class ToggleConfig implements FeatureStruct {
    public boolean Enabled = false;

    @Override
    public void applyDefaults() {
        FeatureStruct.super.applyDefaults();
    }
}