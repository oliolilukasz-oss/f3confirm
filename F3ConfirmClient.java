package pl.f3confirm;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.text.Text;

public class F3ConfirmClient implements ClientModInitializer {

    private static boolean wasVisible = false;
    private static boolean bypass = false; // true = gracz kliknął "Tak"

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(F3ConfirmClient::tick);
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null) {
            wasVisible = false;
            return;
        }

        boolean visible = client.getDebugHud().shouldShowDebugHud();

        // F3 właśnie włączyło debug, a gracz tego jeszcze nie potwierdził
        if (visible && !wasVisible &&!bypass) {
            client.getDebugHud().toggleVisibility(); // natychmiast wyłącz
            visible = false;

            client.setScreen(new ConfirmScreen(
                    confirmed -> {
                        client.setScreen(null); // zamknij okno
                        if (confirmed) {
                            bypass = true;
                            client.getDebugHud().toggleVisibility(); // TAK -> pokaż koordy
                        }
                        // NIE -> nic się nie dzieje
                    },
                    Text.literal("F3"),
                    Text.literal("Czy chcesz włączyć F3?"),
                    Text.literal("Tak"),
                    Text.literal("Nie")
            ));
        } else if (visible && bypass) {
            bypass = false; // potwierdzone, wyczyść flagę
        }

        wasVisible = visible;
    }
}
 