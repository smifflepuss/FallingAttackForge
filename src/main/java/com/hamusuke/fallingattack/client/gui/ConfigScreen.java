package com.hamusuke.fallingattack.client.gui;

import com.hamusuke.fallingattack.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
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
        this.addRenderableWidget(
                Button.builder(CommonComponents.optionStatus(Component.translatable("options.fallingattack.third.person"), Config.Client.THIRD_PERSON.get()), button -> {
                    Config.Client.THIRD_PERSON.set(!Config.Client.THIRD_PERSON.get());
                    button.setMessage(CommonComponents.optionStatus(Component.translatable("options.fallingattack.third.person"), Config.Client.THIRD_PERSON.get()));
                })
                        .pos((this.width / 2) - (this.width / 4), this.height / 2 - 10)
                        .size(this.width / 2, 20)
                        .tooltip(Tooltip.create(Component.translatable("options.fallingattack.third.person.desc")))
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                        .pos((this.width / 2) - (this.width / 4), this.height - 20)
                        .size(this.width / 2, 20)
                        .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, 10, 16777215);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void removed() {
        Config.Client.CONFIG.save();
    }

    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
