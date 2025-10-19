package com.easeon.cs.core.config.model;

public class HotkeyConfig implements FeatureStruct {
    public boolean Enabled = false;
    public int Key = 0;
    public int Mod = 0;

    @Override
    public void applyDefaults() {
        FeatureStruct.super.applyDefaults();
    }
}
