package com.easeon.cs.core.gui.section;

import com.easeon.cs.core.api.EaseonFeatureType;
import com.easeon.cs.core.config.EaseonConfig;
import com.easeon.cs.core.config.GuiConfig;
import com.easeon.cs.core.config.StringKey;
import com.easeon.cs.core.config.model.FeatureStruct;
import com.easeon.cs.core.config.model.HotkeyConfig;
import com.easeon.cs.core.gui.EaseonScreen;
import com.easeon.cs.core.gui.common.GuiRenderable;
import com.easeon.cs.core.gui.widget.EaseonButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class HotkeySection implements GuiRenderable {
    private final EaseonScreen _screen;
    private final FeatureStruct _struct;
    private final EaseonFeatureType _type;
    private final HotkeyConfig _config;

    public final Button hotkeyButton;
    public final Button toggleButton;
    public final Button resetButton;
    private int _y = 0;

    public HotkeySection(EaseonScreen screen, EaseonFeatureType type)
    {
        this._struct = EaseonConfig.configMap.get(type);
        this._config = (HotkeyConfig)this._struct;
        this._screen = screen;
        this._type = type;

        this.hotkeyButton = new EaseonButton(
            getHotkeyText(),
            GuiConfig.getHotkeyButtonX(),
            GuiConfig.SLIDER_WIDTH,
            btn -> {
                screen.activeCaptureSection = this;
                btn.setMessage(Component.literal("..."));
            }
        );

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

        screen.registerChild(this.hotkeyButton);
        screen.registerChild(this.toggleButton);
        screen.registerChild(this.resetButton);
        refreshUI();
    }

    public void handleCapturedKey(int key, int mod) {
        this._config.Key = key;
        this._config.Mod = mod;
    }

    private Component getHotkeyText() {
        int key = this._config.Key;
        int mod = this._config.Mod;
        if (key == 0)
            return StringKey.BUTTON_HOTKEY_EMPTY.asText();

        var sb = new StringBuilder();
        if ((mod & 1) != 0) sb.append("Shift + ");
        if ((mod & 2) != 0) sb.append("Ctrl + ");
        if ((mod & 4) != 0) sb.append("Alt + ");

        String keyName = GLFW.glfwGetKeyName(key, 0);
        if (keyName == null) {
            // 일부 키는 직접 이름 정의
            keyName = switch (key) {
                case GLFW.GLFW_KEY_LEFT_SHIFT -> "Left Shift";
                case GLFW.GLFW_KEY_RIGHT_SHIFT -> "Right Shift";
                case GLFW.GLFW_KEY_LEFT_CONTROL -> "Left Ctrl";
                case GLFW.GLFW_KEY_RIGHT_CONTROL -> "Right Ctrl";
                case GLFW.GLFW_KEY_LEFT_ALT -> "Left Alt";
                case GLFW.GLFW_KEY_RIGHT_ALT -> "Right Alt";
                case GLFW.GLFW_KEY_ENTER -> "Enter";
                case GLFW.GLFW_KEY_TAB -> "Tab";
                case GLFW.GLFW_KEY_BACKSPACE -> "Backspace";
                case GLFW.GLFW_KEY_DELETE -> "Delete";
                case GLFW.GLFW_KEY_UP -> "Up";
                case GLFW.GLFW_KEY_DOWN -> "Down";
                case GLFW.GLFW_KEY_LEFT -> "Left";
                case GLFW.GLFW_KEY_RIGHT -> "Right";
                case GLFW.GLFW_KEY_F1 -> "F1";
                case GLFW.GLFW_KEY_F2 -> "F2";
                case GLFW.GLFW_KEY_F3 -> "F3";
                case GLFW.GLFW_KEY_F4 -> "F4";
                case GLFW.GLFW_KEY_F5 -> "F5";
                case GLFW.GLFW_KEY_F6 -> "F6";
                case GLFW.GLFW_KEY_F7 -> "F7";
                case GLFW.GLFW_KEY_F8 -> "F8";
                case GLFW.GLFW_KEY_F9 -> "F9";
                case GLFW.GLFW_KEY_F10 -> "F10";
                case GLFW.GLFW_KEY_F11 -> "F11";
                case GLFW.GLFW_KEY_F12 -> "F12";
                default -> "Unknown(" + key + ")";
            };
        }

        sb.append(keyName.toUpperCase());
        return Component.literal(sb.toString());
    }


    private Component getToggleButtonText()
    {
        return this._config.Enabled
            ? StringKey.BUTTON_TOGGLE_ON.asText()
            : StringKey.BUTTON_TOGGLE_OFF.asText();
    }

    @Override
    public void refreshUI() {
        this.hotkeyButton.setMessage(getHotkeyText());
        this.hotkeyButton.active = this._config.Enabled;
        this.toggleButton.setMessage(getToggleButtonText());
        this.resetButton.active = _type.isChanged(this._struct);
        _screen.refreshUI();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        Font font = Minecraft.getInstance().font;
        int fontH = font.lineHeight;
        int rowH   = toggleButton.getHeight();  // WIDGET_HEIGHT 와 동일
        int textY = _y + (rowH - fontH) / 2;

        guiGraphics.drawString(font, this._type.getTitle(), GuiConfig.PADDING * 3, textY, GuiConfig.FONT_COLOR, GuiConfig.FONT_SHADOW);

        hotkeyButton.render(guiGraphics, mouseX, mouseY, delta);
        toggleButton.render(guiGraphics, mouseX, mouseY, delta);
        resetButton.render(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public void SetY(int y) {
        _y = y;
        this.hotkeyButton.setY(y);
        this.toggleButton.setY(y);
        this.resetButton.setY(y);
    }
}
