package name.carryclipboard.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import net.minecraft.ChatFormatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarryClipboardClient implements ClientModInitializer {
	private int ticksRemaining = -1;
	private String outputText = "";
	private boolean tradeOngoing = false;
	private boolean dungeonEndOngoing = false;
	//private boolean dungeonEndOdinOngoing = false;

	public static final Logger LOGGER = LoggerFactory.getLogger("carry-clipboard");

	@Override
	public void onInitializeClient() { // This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			String messageText = ChatFormatting.stripFormatting(message.getString());
			//LOGGER.info(messageText);
			if (messageText.startsWith("Trade completed with ")) {
				LOGGER.info("Trade started. Setting tradeOngoing to true.");
				tradeOngoing = true;
				ticksRemaining = 8;
				outputText = " ";
				outputText += messageText;
			}
			if (tradeOngoing) {
				if (messageText.startsWith(" - ") || messageText.startsWith(" + ")){
					outputText += messageText;
				}
			}

			if (messageText.contains("  The Catacombs - Floor ")){
				LOGGER.info("Dungeon ended. Setting dungeonEndOngoing to true.");
				dungeonEndOngoing = true;
				ticksRemaining = 10;
			}
			if (dungeonEndOngoing){
				if (messageText.contains("  Team Score: ")){
					outputText += messageText.substring(messageText.indexOf(" Score: "));
				}
				if (messageText.contains(" Defeated ")) {
					outputText += messageText.substring(messageText.indexOf(" in "));
				}
			}

			if (messageText.contains("-----------------------------------------------------") && messageText.contains(" The Catacombs - ")){
				LOGGER.info("Dungeon ended. Parsing Odin dungeon stats");
				//LOGGER.info(message.getString());
				//LOGGER.info(messageText);

				String OdinScoreText = "";
				String OdinTimeText = "";
				String OdinStatsText = "";

				String[] splitOdinMessage = messageText.split("\\r?\\n");
				for(String splitOdinMessageIndividual : splitOdinMessage){
					LOGGER.info(splitOdinMessageIndividual);
					if (splitOdinMessageIndividual.contains("Score: ")){
						OdinScoreText = splitOdinMessageIndividual.substring(splitOdinMessageIndividual.indexOf(" Score: "));
					}
					if (splitOdinMessageIndividual.contains("Defeated ")) {
						OdinTimeText = splitOdinMessageIndividual.substring(splitOdinMessageIndividual.indexOf(" in "));
					}
					if (splitOdinMessageIndividual.contains(" / ")){
						OdinStatsText = splitOdinMessageIndividual.trim();
					}
				}
				outputText = OdinScoreText+OdinTimeText+" | "+OdinStatsText;
				ticksRemaining = 1;
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (ticksRemaining > 0) {
				ticksRemaining--;
				if (ticksRemaining == 0){
					ticksRemaining = -1;
					tradeOngoing = false;
					dungeonEndOngoing = false;
					LOGGER.info("Trade or Dungeon ended. Copying to clipboard.");
					client.keyboardHandler.setClipboard(outputText);
					outputText = "";
				}
			}
		});
	}
}