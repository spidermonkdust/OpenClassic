package ch.spacebase.openclassic.client.network.handler.custom;

import java.net.MalformedURLException;
import java.net.URL;

import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.network.msg.custom.audio.AudioRegisterMessage;
import ch.spacebase.openclassic.api.player.Player;
import ch.spacebase.openclassic.game.network.ClassicSession;
import ch.spacebase.openclassic.game.network.MessageHandler;

public class AudioRegisterMessageHandler extends MessageHandler<AudioRegisterMessage> {

	@Override
	public void handle(ClassicSession session, Player player, AudioRegisterMessage message) {
		try {
			if(message.isMusic()) {
				OpenClassic.getClient().getAudioManager().registerMusic(message.getIdentifier(), new URL(message.getUrl()), message.isIncluded());
			} else {
				OpenClassic.getClient().getAudioManager().registerSound(message.getIdentifier(), new URL(message.getUrl()), message.isIncluded());
			}
		} catch(MalformedURLException e) {
			OpenClassic.getLogger().warning("Audio URL \"" + message.getUrl() + "\" is invalid!");
		}
	}

}
