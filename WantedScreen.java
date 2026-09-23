package net.wantedvisuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

public class WantedScreen extends Screen {
    private final int windowWidth = 440;
    private final int windowHeight = 280;
    private float animationProgress = 0.0f;
    private int currentCategory = 0;
    private final String[] categories = {"Render", "Hud", "Utility", "ReallyWorld"};
    private String searchQuery = "";
    private Module selectedModule = null;
    private Module bindingModule = null;

    public WantedScreen() { super(Text.of("WantedVisuals GUI")); }

    @Override
    protected void init() { animationProgress = 0.0f; }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (animationProgress < 1.0f) {
            animationProgress += delta * 0.15f;
            if (animationProgress > 1.0f) animationProgress = 1.0f;
        }
        this.renderBackground(context, mouseX, mouseY, delta);
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int x = centerX - (windowWidth / 2);
        int y = centerY - (windowHeight / 2);

        context.getMatrices().push();
        context.getMatrices().translate(centerX, centerY, 0);
        context.getMatrices().scale(animationProgress, animationProgress, 1.0f);
        context.getMatrices().translate(-centerX, -centerY, 0);

        int mainBgColor = new Color(22, 22, 22, 245).getRGB();
        int neonRedColor = new Color(255, 0, 55).getRGB();
        int panelBgColor = new Color(30, 30, 30).getRGB();

        context.fill(x, y, x + windowWidth, y + windowHeight, mainBgColor);
        context.fill(x, y, x + windowWidth, y + 1, neonRedColor);
        context.fill(x, y + windowHeight - 1, x + windowWidth, y + windowHeight, neonRedColor);
        context.fill(x, y, x + 1, y + windowHeight, neonRedColor);
        context.fill(x + windowWidth - 1, y, x + windowWidth, y + windowHeight, neonRedColor);

        int sidebarWidth = 95;
        context.fill(x + sidebarWidth, y, x + sidebarWidth + 1, y + windowHeight, neonRedColor);

        for (int i = 0; i < categories.length; i++) {
            int buttonY = y + 45 + (i * 24);
            int textColor = (i == currentCategory) ? neonRedColor : new Color(170, 170, 170).getRGB();
            if (i == currentCategory) {
                context.fill(x + 6, buttonY - 3, x + sidebarWidth - 6, buttonY + 11, new Color(255, 0, 55, 35).getRGB());
            }
            context.drawText(this.textRenderer, categories[i], x + 14, buttonY, textColor, false);
        }

        int searchY = y + 15;
        context.fill(x + 8, searchY, x + sidebarWidth - 8, searchY + 14, new Color(32, 32, 32).getRGB());
        context.fill(x + 8, searchY, x + sidebarWidth - 8, searchY + 1, neonRedColor);
        String displayText = searchQuery.isEmpty() ? "Search..." : searchQuery;
        context.drawText(this.textRenderer, displayText, x + 12, searchY + 3, searchQuery.isEmpty() ? new Color(90, 90, 90).getRGB() : Color.WHITE.getRGB(), false);

        String currentCatName = categories[currentCategory];
        List<Module> filteredModules = ModuleManager.getModules().stream()
                .filter(m -> (searchQuery.isEmpty() ? m.getCategory().equalsIgnoreCase(currentCatName) : m.getName().toLowerCase().contains(searchQuery.toLowerCase())))
                .collect(Collectors.toList());

        int startX = x + sidebarWidth + 15;
        int startY = y + 20;
        context.drawText(this.textRenderer, searchQuery.isEmpty() ? currentCatName.toUpperCase() : "FOUND", startX, startY, new Color(130, 130, 130).getRGB(), false);

        for (int i = 0; i < filteredModules.size(); i++) {
            Module mod = filteredModules.get(i);
            int modY = startY + 20 + (i * 28);
            context.fill(startX, modY, x + windowWidth - 15, modY + 22, panelBgColor);
            if (mod.isEnabled()) context.fill(startX, modY, startX + 3, modY + 22, neonRedColor);
            context.drawText(this.textRenderer, mod.getName(), startX + 8, modY + 7, mod.isEnabled() ? neonRedColor : Color.WHITE.getRGB(), false);
            
            String bindText = mod.getBind() == 0 ? "[NONE]" : "[" + GLFW.glfwGetKeyName(mod.getBind(), 0).toUpperCase() + "]";
            if (bindingModule == mod) bindText = "[...]";
            context.drawText(this.textRenderer, bindText, x + windowWidth - 60, modY + 7, new Color(120, 120, 120).getRGB(), false);
        }

        if (selectedModule != null) {
            int subW = 160; int subH = 100; int subX = centerX - (subW / 2); int subY = centerY - (subH / 2);
            context.fill(x, y, x + windowWidth, y + windowHeight, new Color(0,0,0, 100).getRGB());
            context.fill(subX, subY, subX + subW, subY + subH, new Color(28, 28, 28).getRGB());
            context.fill(subX, subY, subX + subW, subY + 1, neonRedColor);
            context.drawText(this.textRenderer, selectedModule.getName(), subX + 10, subY + 10, neonRedColor, false);
            context.drawText(this.textRenderer, selectedModule.getDescription(), subX + 10, subY + 45, Color.WHITE.getRGB(), false);
        }

        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2; int centerY = this.height / 2;
        int x = centerX - (windowWidth / 2); int y = centerY - (windowHeight / 2);
        int sidebarWidth = 95;

        if (selectedModule != null) return super.mouseClicked(mouseX, mouseY, button);

        if (button == 0 && mouseX >= x + 5 && mouseX <= x + sidebarWidth - 5) {
            for (int i = 0; i < categories.length; i++) {
                int buttonY = y + 45 + (i * 24);
                if (mouseY >= buttonY - 3 && mouseY <= buttonY + 11) { currentCategory = i; searchQuery = ""; return true; }
            }
        }

        String currentCatName = categories[currentCategory];
        List<Module> filteredModules = ModuleManager.getModules().stream()
                .filter(m -> (searchQuery.isEmpty() ? m.getCategory().equalsIgnoreCase(currentCatName) : m.getName().toLowerCase().contains(searchQuery.toLowerCase())))
                .collect(Collectors.toList());

        int startX = x + sidebarWidth + 15; int startY = y + 20;
        for (int i = 0; i < filteredModules.size(); i++) {
            Module mod = filteredModules.get(i); int modY = startY + 20 + (i * 28);
            if (mouseX >= startX && mouseX <= x + windowWidth - 15 && mouseY >= modY && mouseY <= modY + 22) {
                if (button == 0) { mod.toggle(); return true; }
                else if (button == 1) { selectedModule = mod; return true; }
                else if (button == 2) { bindingModule = mod; return true; }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectedModule != null && keyCode == GLFW.GLFW_KEY_ESCAPE) { selectedModule = null; return true; }
        if (bindingModule != null) {
            if (keyCode == GLFW.GLFW_KEY_DELETE) bindingModule.setBind(0);
            else if (keyCode != GLFW.GLFW_KEY_ESCAPE) bindingModule.setBind(keyCode);
            bindingModule = null;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) { searchQuery = searchQuery.substring(0, searchQuery.length() - 1); return true; }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) { this.close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (selectedModule == null && bindingModule == null && textRenderer.getWidth(searchQuery + chr) < 70) { searchQuery += chr; return true; }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }
}
