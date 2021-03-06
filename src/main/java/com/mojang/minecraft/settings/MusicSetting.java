package com.mojang.minecraft.settings;

import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.settings.BooleanSetting;

public class MusicSetting extends BooleanSetting {

	public MusicSetting(String name, String configKey) {
		super(name, configKey);
	}
	
	@Override
	public void toggle() {
		super.toggle();
		if(!this.getValue()) {
			OpenClassic.getClient().getAudioManager().stopMusic();
		}
	}

}
