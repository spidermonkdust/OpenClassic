package com.mojang.minecraft.net;

public final class PacketType {

	public static final PacketType[] packets = new PacketType[256];
	public static final PacketType IDENTIFICATION;
	public static final PacketType PING;
	public static final PacketType LEVEL_INIT;
	public static final PacketType LEVEL_DATA;
	public static final PacketType LEVEL_FINALIZE;
	public static final PacketType CLIENT_SET_BLOCK;
	public static final PacketType SET_BLOCK;
	public static final PacketType SPAWN_PLAYER;
	public static final PacketType POSITION_ROTATION;
	public static final PacketType POSITION_ROTATION_UPDATE;
	public static final PacketType POSITION_UPDATE;
	public static final PacketType ROTATION_UPDATE;
	public static final PacketType DESPAWN_PLAYER;
	public static final PacketType CHAT_MESSAGE;
	public static final PacketType DISCONNECT;
	public static final PacketType UPDATE_PLAYER_TYPE;
	
	// Custom
	public static final PacketType CLIENT_INFO;
	public static final PacketType CUSTOM_BLOCK;
	public static final PacketType BLOCK_MODEL;
	public static final PacketType QUAD;
	
	private static int lastOpcode;
	
	public final int length;
	public final byte opcode;
	public Class<?>[] params;

	private PacketType(Class<?>... params) {
		this.opcode = (byte) lastOpcode++;
		packets[this.opcode] = this;
		this.params = params;

		int length = 0;
		for (Class<?> param : params) {
			if (param == Long.TYPE) {
				length += 8;
			} else if (param == Integer.TYPE) {
				length += 4;
			} else if (param == Short.TYPE) {
				length += 2;
			} else if (param == Byte.TYPE) {
				++length;
			} else if (param == Float.TYPE) {
				length += 4;
			} else if (param == Double.TYPE) {
				length += 8;
			} else if (param == byte[].class) {
				length += 1024;
			} else if (param == String.class) {
				length += 64;
			}
		}

		this.length = length;
	}

	static {
		IDENTIFICATION = new PacketType(new Class[] { Byte.TYPE, String.class, String.class, Byte.TYPE });
		PING = new PacketType(new Class[0]);
		LEVEL_INIT = new PacketType(new Class[0]);
		LEVEL_DATA = new PacketType(new Class[] { Short.TYPE, byte[].class, Byte.TYPE });
		LEVEL_FINALIZE = new PacketType(new Class[] { Short.TYPE, Short.TYPE, Short.TYPE });
		CLIENT_SET_BLOCK = new PacketType(new Class[] { Short.TYPE, Short.TYPE, Short.TYPE, Byte.TYPE, Byte.TYPE });
		SET_BLOCK = new PacketType(new Class[] { Short.TYPE, Short.TYPE, Short.TYPE, Byte.TYPE });
		SPAWN_PLAYER = new PacketType(new Class[] { Byte.TYPE, String.class, Short.TYPE, Short.TYPE, Short.TYPE, Byte.TYPE, Byte.TYPE });
		POSITION_ROTATION = new PacketType(new Class[] { Byte.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Byte.TYPE, Byte.TYPE });
		POSITION_ROTATION_UPDATE = new PacketType(new Class[] { Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE });
		POSITION_UPDATE = new PacketType(new Class[] { Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE });
		ROTATION_UPDATE = new PacketType(new Class[] { Byte.TYPE, Byte.TYPE, Byte.TYPE });
		DESPAWN_PLAYER = new PacketType(new Class[] { Byte.TYPE });
		CHAT_MESSAGE = new PacketType(new Class[] { Byte.TYPE, String.class });
		DISCONNECT = new PacketType(new Class[] { String.class });
		UPDATE_PLAYER_TYPE = new PacketType(new Class[] { Byte.TYPE });
		
		// Custom
		CLIENT_INFO = new PacketType(new Class[] { String.class });
		CUSTOM_BLOCK = new PacketType(new Class[] { Byte.TYPE, Byte.TYPE, Byte.TYPE, String.class, Byte.TYPE, Integer.TYPE, Byte.TYPE, Byte.TYPE });
		BLOCK_MODEL = new PacketType(new Class[] { Byte.TYPE, String.class, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE });
		QUAD = new PacketType(new Class[] { Byte.TYPE, Integer.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, String.class, Byte.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE });
		
		lastOpcode = 0;
	}
}