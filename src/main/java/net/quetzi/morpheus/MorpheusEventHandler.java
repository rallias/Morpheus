package net.quetzi.morpheus;

import java.util.Iterator;
import java.util.Map.Entry;

import net.quetzi.morpheus.world.WorldSleepState;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public class MorpheusEventHandler {
	@SubscribeEvent
	public void loggedInEvent(PlayerLoggedInEvent event) {
		if (Morpheus.playerSleepStatus.get(event.player.dimension) == null) {
			Morpheus.playerSleepStatus.put(event.player.dimension,
					new WorldSleepState(event.player.dimension));
		}
		Morpheus.playerSleepStatus.get(event.player.dimension).setPlayerAwake(
				event.player.getCommandSenderName());
	}

	@SubscribeEvent
	public void loggedOutEvent(PlayerLoggedOutEvent event) {
		Morpheus.playerSleepStatus.get(event.player.dimension).removePlayer(event.player.getCommandSenderName());
//		// Remove player from all world states
//		Iterator<Entry<Integer, WorldSleepState>> entry = Morpheus.playerSleepStatus
//				.entrySet().iterator();
//		while (entry.hasNext()) {
//			Morpheus.playerSleepStatus.get(entry.next().getKey()).removePlayer(
//					event.player.getCommandSenderName());
//		}
	}

	@SubscribeEvent
	public void changedDimensionEvent(PlayerChangedDimensionEvent event) {
		if (Morpheus.playerSleepStatus.get(event.player.dimension) == null) {
			Morpheus.playerSleepStatus.put(event.player.dimension,
					new WorldSleepState(event.player.dimension));
		}
		// Remove player from all world states
		Iterator<Entry<Integer, WorldSleepState>> entry = Morpheus.playerSleepStatus
				.entrySet().iterator();
		while (entry.hasNext()) {
			Morpheus.playerSleepStatus.get(entry.next().getKey()).removePlayer(
					event.player.getCommandSenderName());
		}
		// Add player to new world state
		Morpheus.playerSleepStatus.get(event.player.dimension).setPlayerAwake(
				event.player.getCommandSenderName());
	}

	@SubscribeEvent
	public void worldTickEvent(WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			// This is called every tick, do something every 20 ticks
			if (event.world.getWorldTime() % 20L == 10) {
				if (event.world.playerEntities.size() > 0) {
					if (Morpheus.playerSleepStatus
							.get(event.world.provider.dimensionId) == null) {
						Morpheus.playerSleepStatus.put(
								event.world.provider.dimensionId,
								new WorldSleepState(
										event.world.provider.dimensionId));
					}
					Morpheus.checker.updatePlayerStates(event.world);
				} else {
					Morpheus.playerSleepStatus
							.remove(event.world.provider.dimensionId);
				}
			}
		}
	}
}