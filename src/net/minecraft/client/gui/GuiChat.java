package net.minecraft.client.gui;

import com.google.common.collect.Lists;

import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import me.memorial.Memorial;
import me.memorial.module.modules.client.Notifications;
import me.memorial.module.modules.client.TargetHUD;
import me.memorial.ui.font.Fonts;
import me.memorial.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiChat extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private String historyBuffer = "";
    private int sentHistoryCursor = -1;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private boolean dragTH;
    private int x1,y1;
    private int autocompleteIndex;
    private List<String> foundPlayerNames = Lists.<String>newArrayList();
    protected GuiTextField inputField;
    private String defaultInputFieldText = "";

    public GuiChat()
    {
    }

    public GuiChat(String defaultText)
    {
        this.defaultInputFieldText = defaultText;
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(100);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    public void updateScreen()
    {
        this.inputField.updateCursorCounter();
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.waitingOnAutocomplete = false;

        if (keyCode == 15) {
            this.autocompletePlayerNames();
        } else {
            this.playerNamesFound = false;
        }

        if (keyCode == 1) {
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 200) {
                this.getSentHistory(-1);
            } else if (keyCode == 208) {
                this.getSentHistory(1);
            } else if (keyCode == 201) {
                this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
            } else if (keyCode == 209) {
                this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
            } else {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        } else {
            String s = this.inputField.getText().trim();

            if (s.length() > 0) {
                this.sendChatMessage(s);
            }

            this.mc.displayGuiScreen(null);
        }

        if (!inputField.getText().startsWith(String.valueOf(Memorial.commandManager.getPrefix()))) return;
        Memorial.commandManager.autoComplete(inputField.getText());

        if (inputField.getText().startsWith(String.valueOf(Memorial.commandManager.getPrefix())) || inputField.getText().startsWith("/"))
            inputField.setMaxStringLength(10000);
        else
            inputField.setMaxStringLength(100);
    }

    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }

            if (i < -1)
            {
                i = -1;
            }

            if (!isShiftKeyDown())
            {
                i *= 7;
            }

            this.mc.ingameGUI.getChatGUI().scroll(i);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int x = getScaledMouseCoordinates(mc, mouseX, mouseY)[0];
            int y = getScaledMouseCoordinates(mc, mouseX, mouseY)[1];
            TargetHUD targetHUD = (TargetHUD) Memorial.moduleManager.getModule(TargetHUD.class);
            if (hover(mouseX, mouseY,targetHUD)){
                x1 = targetHUD.getX() - x;
                y1 = targetHUD.getY() - y;
                dragTH = true;
            }

            IChatComponent ichatcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (this.handleComponentClick(ichatcomponent))
            {
                return;
            }
        }

        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0){
            dragTH = false;
        }
    }
    private boolean hover(int mouseX, int mouseY, TargetHUD targetHUD) {
        return mouseX >= targetHUD.getX() && mouseX <= targetHUD.getX() + targetHUD.getWidth() && mouseY >= targetHUD.getY() && mouseY <= targetHUD.getY() + targetHUD.getHeight();
    }


    private int[] getScaledMouseCoordinates(Minecraft mc, int mouseX, int mouseY){
        int x = mouseX;
        int y = mouseY;
        switch (mc.gameSettings.guiScale){
            case 0:
                x*=2;
                y*=2;
                break;
            case 1:
                x*=0.5;
                y*=0.5;
                break;
            case 3:
                x*=1.4999999999999999998;
                y*=1.4999999999999999998;
        }
        return new int[]{x,y};
    }
    private void scale(Minecraft mc){
            switch (mc.gameSettings.guiScale){
                case 0:
                    GlStateManager.scale(0.5,0.5,0.5);
                    break;
                case 1:
                    GlStateManager.scale(2,2,2);
                    break;
                case 3:
                    GlStateManager.scale(0.6666666666666667,0.6666666666666667,0.6666666666666667);

            }
    }
    protected void setText(String newChatText, boolean shouldOverwrite)
    {
        if (shouldOverwrite)
        {
            this.inputField.setText(newChatText);
        }
        else
        {
            this.inputField.writeText(newChatText);
        }
    }

    public void autocompletePlayerNames()
    {
        foundPlayerNames.sort(Comparator.comparing(s -> !Memorial.fileManager.friendsConfig.isFriend(s)));

        if (this.playerNamesFound)
        {
            this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());

            if (this.autocompleteIndex >= this.foundPlayerNames.size())
            {
                this.autocompleteIndex = 0;
            }
        }
        else
        {
            int i = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String s = this.inputField.getText().substring(i).toLowerCase();
            String s1 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(s1, s);

            if (this.foundPlayerNames.isEmpty())
            {
                return;
            }

            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(i - this.inputField.getCursorPosition());
        }

        if (this.foundPlayerNames.size() > 1)
        {
            StringBuilder stringbuilder = new StringBuilder();

            for (String s2 : this.foundPlayerNames)
            {
                if (stringbuilder.length() > 0)
                {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(s2);
            }

            this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringbuilder.toString()), 1);
        }

        this.inputField.writeText((String)this.foundPlayerNames.get(this.autocompleteIndex++));
    }

    private void sendAutocompleteRequest(String p_146405_1_, String p_146405_2_)
    {
        if (Memorial.commandManager.autoComplete(p_146405_1_)) {
            waitingOnAutocomplete = true;

            String[] latestAutoComplete = Memorial.commandManager.getLatestAutoComplete();

            if (!p_146405_1_.toLowerCase().endsWith(latestAutoComplete[latestAutoComplete.length - 1].toLowerCase())){
                this.onAutocompleteResponse(latestAutoComplete);
                return;
            }
        }

        if (p_146405_1_.length() >= 1)
        {
            BlockPos blockpos = null;

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                blockpos = this.mc.objectMouseOver.getBlockPos();
            }

            this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(p_146405_1_, blockpos));
            this.waitingOnAutocomplete = true;
        }
    }

    public void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp_int(i, 0, j);

        if (i != this.sentHistoryCursor)
        {
            if (i == j)
            {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            }
            else
            {
                if (this.sentHistoryCursor == j)
                {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {


        drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.inputField.drawTextBox();
        if (Memorial.commandManager.getLatestAutoComplete().length > 0 && !inputField.getText().isEmpty() && inputField.getText().startsWith(String.valueOf(Memorial.commandManager.getPrefix()))) {
            String[] latestAutoComplete = Memorial.commandManager.getLatestAutoComplete();
            String[] textArray = inputField.getText().split(" ");
            String trimmedString = latestAutoComplete[0].replaceFirst("(?i)" + textArray[textArray.length - 1], "");

            mc.fontRendererObj.drawStringWithShadow(trimmedString, inputField.xPosition + mc.fontRendererObj.getStringWidth(inputField.getText()), inputField.yPosition, new Color(165, 165, 165).getRGB());
        }


        IChatComponent ichatcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null && ichatcomponent.getChatStyle().getChatHoverEvent() != null)
        {
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
        }
        ScaledResolution sr = new ScaledResolution(mc);
        int x = getScaledMouseCoordinates(mc, mouseX, mouseY)[0];
        int y = getScaledMouseCoordinates(mc, mouseX, mouseY)[1];
        TargetHUD targetHUD = (TargetHUD) Memorial.moduleManager.getModule(TargetHUD.class);

        if (targetHUD.getState()){
            if (dragTH){
                targetHUD.setX(MathHelper.clamp_int(x1 + x,1, (int) (targetHUD.x.getMaximum() - targetHUD.getWidth())));
                targetHUD.setY(MathHelper.clamp_int(y1 + y, 1, (int) (targetHUD.y.getMaximum() - targetHUD.getHeight())));
            }
            if (hover(x, y, targetHUD)) {
                if (targetHUD.getX() > sr.getScaledWidth() - 50) {
                    targetHUD.setX(sr.getScaledWidth() - 50);
                }

                if (targetHUD.getY() > sr.getScaledHeight() - 50) {
                    targetHUD.setY(sr.getScaledHeight() - 50);
                }
                scale(mc);
                RenderUtils.drawBorderedRect(targetHUD.getX(), targetHUD.getY(), targetHUD.getX() + targetHUD.getWidth(), targetHUD.getY() + targetHUD.getHeight(), 1, -1,0);
            }
        }



        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void onAutocompleteResponse(String[] p_146406_1_)
    {
        if (this.waitingOnAutocomplete)
        {
            this.playerNamesFound = false;
            this.foundPlayerNames.clear();

            for (String s : p_146406_1_)
            {
                if (s.length() > 0)
                {
                    this.foundPlayerNames.add(s);
                }
            }

            String s1 = this.inputField.getText().substring(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false));
            String s2 = StringUtils.getCommonPrefix(p_146406_1_);

            if (s2.length() > 0 && !s1.equalsIgnoreCase(s2))
            {
                this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
                this.inputField.writeText(s2);
            }
            else if (this.foundPlayerNames.size() > 0)
            {
                this.playerNamesFound = true;
                if (Memorial.commandManager.getLatestAutoComplete().length != 0) return;
                this.autocompletePlayerNames();
            }
        }
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
