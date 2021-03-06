package com.mojang.minecraft.entity.mob;

import com.mojang.minecraft.entity.mob.ai.BasicAttackAI;
import com.mojang.minecraft.level.Level;

public class Zombie extends HumanoidMob {

	public Zombie(Level level, float x, float y, float z) {
		super(level, x, y, z);
		this.modelName = "zombie";
		this.textureName = "/mob/zombie.png";
		this.heightOffset = 1.62F;
		this.deathScore = 80;
		this.ai = new BasicAttackAI();
		this.ai.defaultLookAngle = 30;
		((BasicAttackAI) this.ai).runSpeed = 1.0F;
	}
}
