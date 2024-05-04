package com.hamusuke.fallingattack.client.gui;

import com.hamusuke.fallingattack.config.Config;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Minecraft ignored, Screen parent) {
        super(Component.translatable("options.title"));
        this.parent = parent;
    }

    protected void init() {
        this.addRenderableWidget(new Button((this.width / 2) - (this.width / 4), this.height / 2 - 10, this.width / 2, 20, CommonComponents.optionStatus(Component.translatable("options.fallingattack.third.person"), Config.Client.THIRD_PERSON.get()), p_onPress_1_ -> {
            Config.Client.THIRD_PERSON.set(!Config.Client.THIRD_PERSON.get());
            p_onPress_1_.setMessage(CommonComponents.optionStatus(Component.translatable("options.fallingattack.third.person"), Config.Client.THIRD_PERSON.get()));
        }, (p_onTooltip_1_, p_onTooltip_2_, p_onTooltip_3_, p_onTooltip_4_) -> {
            this.renderWithTooltip(p_onTooltip_2_, Component.translatable("options.fallingattack.third.person.desc"), p_onTooltip_3_, p_onTooltip_4_);
        }));

        this.addRenderableWidget(new Button((this.width / 2) - (this.width / 4), this.height - 20, this.width / 2, 20, CommonComponents.GUI_DONE, p_onPress_1_ -> this.onClose()));
    }

    public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        drawCenteredString(p_230430_1_, this.font, this.getTitle(), this.width / 2, 10, 16777215);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    public void removed() {
        Config.Client.CONFIG.save();
    }

    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
