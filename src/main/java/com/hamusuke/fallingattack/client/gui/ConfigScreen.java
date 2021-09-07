package com.hamusuke.fallingattack.client.gui;

import com.hamusuke.fallingattack.config.Config;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Minecraft minecraft, Screen parent) {
        super(new TranslationTextComponent("options.title"));
        this.parent = parent;
    }

    protected void init() {
        this.addButton(new Button((this.width / 2) - (this.width / 4), this.height / 2 - 10, this.width / 2, 20, DialogTexts.optionStatus(new TranslationTextComponent("options.fallingattack.third.person"), Config.thirdPerson.get()), p_onPress_1_ -> {
            Config.thirdPerson.set(!Config.thirdPerson.get());
            p_onPress_1_.setMessage(DialogTexts.optionStatus(new TranslationTextComponent("options.fallingattack.third.person"), Config.thirdPerson.get()));
        }, (p_onTooltip_1_, p_onTooltip_2_, p_onTooltip_3_, p_onTooltip_4_) -> {
            this.renderTooltip(p_onTooltip_2_, new TranslationTextComponent("options.fallingattack.third.person.desc"), p_onTooltip_3_, p_onTooltip_4_);
        }));

        this.addButton(new Button((this.width / 2) - (this.width / 4), this.height - 20, this.width / 2, 20, DialogTexts.GUI_DONE, p_onPress_1_ -> {
            this.onClose();
        }));
    }

    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        drawCenteredString(p_230430_1_, this.font, this.getTitle(), this.width / 2, 10, 16777215);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    public void removed() {
        Config.config.save();
    }

    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
