package ch.spacebase.openclassic.client.sound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;
import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.math.MathHelper;
import ch.spacebase.openclassic.api.math.Vector;
import ch.spacebase.openclassic.api.player.Player;
import ch.spacebase.openclassic.api.sound.AudioManager;

import com.mojang.minecraft.Minecraft;

import de.cuina.fireandfuel.CodecJLayerMP3;

public class ClientAudioManager implements AudioManager {

	private static final Random rand = new Random();
	private static int nextSoundId = 0;

	private final Map<String, List<URL>> sounds = new HashMap<String, List<URL>>();
	private final Map<String, List<URL>> music = new HashMap<String, List<URL>>();

	private SoundSystem system;
	private Minecraft mc;
	public long nextBGM = System.currentTimeMillis();

	public ClientAudioManager(Minecraft mc) {
		this.mc = mc;

		Class<? extends Library> lib = Library.class;

		if(SoundSystem.libraryCompatible(LibraryLWJGLOpenAL.class)) {
			lib = LibraryLWJGLOpenAL.class;
		} else if(SoundSystem.libraryCompatible(LibraryJavaSound.class)) {
			lib = LibraryJavaSound.class;
		}

		try {
			this.system = new SoundSystem(lib);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			SoundSystemConfig.setCodec("mp3", CodecJLayerMP3.class);
		} catch(SoundSystemException e) {
			e.printStackTrace();
		}
	}

	public void update(com.mojang.minecraft.entity.player.LocalPlayer player) {
		if(player != null && OpenClassic.getClient().isInGame()) {
			this.system.setListenerPosition(player.x, player.y, player.z);
			Vector vec = MathHelper.toForwardVec(player.yaw, player.pitch);
			this.system.setListenerOrientation(vec.getX(), vec.getY(), vec.getZ(), (float) Math.sin(Math.toRadians(player.pitch)), (float) Math.sin(Math.toRadians(player.yaw)), 1);
		} else {
			this.system.setListenerPosition(0, 0, 0);
			this.system.setListenerOrientation(0, 0, -1, 0, 1, 0);
		}
	}

	public void cleanup() {
		this.system.cleanup();
	}

	public void registerSound(String sound, URL file, boolean included) {
		Validate.notNull(sound, "Sound cannot be null.");
		Validate.notNull(file, "URL cannot be null.");
		if(!included) {
			this.download(file);
		}

		if(!this.sounds.containsKey(sound)) this.sounds.put(sound, new ArrayList<URL>());
		this.sounds.get(sound).add(file);
	}

	public void registerMusic(String music, URL file, boolean included) {
		Validate.notNull(music, "Music cannot be null.");
		Validate.notNull(file, "URL cannot be null.");
		if(!included) {
			this.download(file);
		}

		if(!this.music.containsKey(music)) this.music.put(music, new ArrayList<URL>());
		this.music.get(music).add(file);
	}

	private void download(URL url) {
		File file = new File(this.mc.dir, "cache/" + (this.mc.server != null && !this.mc.server.equals("") ? this.mc.server : "local") + "/" + url.getFile());
		if(!file.exists()) {
			if(!file.getParentFile().exists()) {
				try {
					file.getParentFile().mkdirs();
				} catch(SecurityException e) {
					e.printStackTrace();
				}
			}

			try {
				file.createNewFile();
			} catch(SecurityException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}

			OpenClassic.getLogger().info(String.format(OpenClassic.getGame().getTranslator().translate("http.downloading"), file.getName()));

			byte[] data = new byte[4096];
			DataInputStream in = null;
			DataOutputStream out = null;

			try {
				in = new DataInputStream(url.openStream());
				out = new DataOutputStream(new FileOutputStream(file));

				int length = 0;
				while(OpenClassic.getClient().isRunning()) {
					length = in.read(data);
					if(length < 0) break;
					out.write(data, 0, length);
				}
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}

			OpenClassic.getLogger().info(String.format(OpenClassic.getGame().getTranslator().translate("http.downloaded"), file.getName()));
		}
	}

	public boolean playSound(String sound, float x, float y, float z, float volume, float pitch) {
		if(!this.mc.settings.getBooleanSetting("options.sound").getValue()) return true;

		List<URL> files = this.sounds.get(sound);
		if(files != null) {
			URL file = files.get(rand.nextInt(files.size()));

			nextSoundId = (nextSoundId + 1) % 256;
			String source = "sound_" + nextSoundId;

			float attenuation = 16;
			if(volume > 1) attenuation = volume * 16;

			this.system.newSource(volume > 1, source, file, file.getFile(), false, x, y, z, SoundSystemConfig.ATTENUATION_LINEAR, attenuation);

			if(volume > 1) volume = 1;
			this.system.setVolume(source, volume);
			this.system.setPitch(source, pitch);

			this.system.play(source);
			return true;
		}

		return false;
	}

	public boolean playSound(String sound, float volume, float pitch) {
		return this.playSound(sound, this.system.getListenerData().position.x, this.system.getListenerData().position.y, this.system.getListenerData().position.z, volume, pitch);
	}

	public boolean playMusic(String music) {
		return this.playMusic(music, false);
	}

	public boolean playMusic(String music, boolean loop) {
		if(!this.mc.settings.getBooleanSetting("options.music").getValue()) return true;
		List<URL> files = this.music.get(music);
		if(files != null) {
			URL file = files.get(rand.nextInt(files.size()));
			if(this.isPlaying(music)) return true;
			if(this.isPlayingMusic()) {
				this.stopMusic();
			}

			this.system.backgroundMusic(music, file, file.getFile(), loop);
			this.system.play(music);
			return true;
		}

		return false;
	}

	public boolean isPlayingMusic() {
		for(String music : this.music.keySet()) {
			if(this.isPlaying(music)) return true;
		}

		return false;
	}

	public void stopMusic() {
		for(String music : this.music.keySet()) {
			if(this.isPlaying(music)) this.stop(music);
		}
	}

	public boolean isPlaying(String music) {
		return this.system.playing(music);
	}

	public void stop(String music) {
		this.system.stop(music);
	}

	@Override
	public boolean playSound(Player player, String sound, float volume, float pitch) {
		return this.playSound(sound, volume, pitch);
	}

	@Override
	public boolean playSound(Player player, String sound, float x, float y, float z, float volume, float pitch) {
		return this.playSound(sound, x, y, z, volume, pitch);
	}

	@Override
	public boolean playMusic(Player player, String music) {
		return this.playMusic(music);
	}

	@Override
	public boolean playMusic(Player player, String music, boolean loop) {
		return this.playMusic(music, loop);
	}

	@Override
	public void stopMusic(Player player) {
		this.stopMusic();
	}

	@Override
	public void stop(Player player, String music) {
		this.stop(music);
	}

}