package com.easeon.cs.core.api.keybind;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.ArrayList;
import java.util.List;

public class EaseonKeyBindingRegistry {
    private static final List<KeyMapping> KEY_BINDINGS = new ArrayList<>();
    private static boolean registered = false;

    public static KeyMapping register(String translationKey, int keyCode, KeyMapping.Category category) {
        return register(translationKey, InputConstants.Type.KEYSYM, keyCode, category);
    }

    public static KeyMapping register(String translationKey, InputConstants.Type type, int keyCode, KeyMapping.Category category) {
        KeyMapping keyMapping = new KeyMapping(translationKey, type, keyCode, category);
        KEY_BINDINGS.add(keyMapping);
        return keyMapping;
    }

    public static List<KeyMapping> getKeyBindings() {
        return new ArrayList<>(KEY_BINDINGS);
    }

    public static void registerAll() {
        if (registered) return;
        registered = true;
        // KeyMapping 객체는 생성만 하면 자동으로 등록됨
        // 추가 작업 불필요
    }
}