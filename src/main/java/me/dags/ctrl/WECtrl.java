package me.dags.ctrl;

import me.dags.ctrl.input.Input;
import me.dags.ctrl.input.Listener;
import me.dags.ctrl.input.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = "wectrl")
@Mod.EventBusSubscriber
public class WECtrl {

    private static final List<Input> inputs = new ArrayList<>();

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        inputs.add(Input.key(Keyboard.KEY_C, Listener.pressed(() -> sendCommand("//copy")).requireCtrl()));

        inputs.add(Input.key(Keyboard.KEY_V, Listener.pressed(() -> sendCommand("//paste")).requireCtrl()));

        inputs.add(Input.key(Keyboard.KEY_X, Listener.pressed(() -> sendCommand("//cut")).requireCtrl()));

        inputs.add(Input.key(Keyboard.KEY_Z, Listener.pressed(() -> {
            if (Input.isShiftDown()) {
                sendCommand("//redo");
            } else {
                sendCommand("//undo");
            }
        }).requireCtrl()));

        inputs.add(Input.scroll(Listener.scrolled(state -> {
            if (state == State.SCROLL_DOWN) {
                sendCommand("//pos2");
            } else {
                sendCommand("//pos1");
            }
        }).requireCtrl()));
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        for (Input input : inputs) {
            input.update();
        }
    }

    private static void sendCommand(String command) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player != null) {
            player.sendChatMessage(command);
        }
    }
}
