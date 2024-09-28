package net.nugget.bloomerymod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.nugget.bloomerymod.BloomeryMod;
import net.nugget.bloomerymod.block.custom.AbstractBloomeryControllerBlock;

public class BloomeryControllerScreen extends AbstractContainerScreen<BloomeryControllerMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(BloomeryMod.MOD_ID, "textures/gui/bloomery_controller_gui.png");

    public BloomeryControllerScreen(BloomeryControllerMenu menu, Inventory inventory, Component component)
    {
        super(menu, inventory, component);
    }

    @Override
    protected void init()
    {
        super.init();
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        renderFireAndProgressArrow(pPoseStack, x, y);
    }

    private void renderFireAndProgressArrow(PoseStack pPoseStack, int x, int y) {
        if(menu.isCrafting())
            blit(pPoseStack, x + 69, y + 30, 177, 14, menu.getScaledProgress(), 15);

        if (menu.blockEntity.getBlockState().getValue(AbstractBloomeryControllerBlock.LIT))
            blit(pPoseStack, x + 45, y + 41, 176, 0, 14, 14);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
}
