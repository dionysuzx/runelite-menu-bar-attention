/*
 * Copyright (c) 2026, Menu Bar Attention contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES ARE DISCLAIMED.
 */
package com.menubarattention;

import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

final class ActivitySnapshot
{
	private final boolean loggedIn;
	@Nullable
	private final WorldPoint position;
	private final int animation;
	private final boolean interacting;

	ActivitySnapshot(boolean loggedIn, @Nullable WorldPoint position, int animation, boolean interacting)
	{
		this.loggedIn = loggedIn;
		this.position = position;
		this.animation = animation;
		this.interacting = interacting;
	}

	static ActivitySnapshot capture(Client client)
	{
		Player player = client.getLocalPlayer();
		if (client.getGameState() != GameState.LOGGED_IN || player == null)
		{
			return new ActivitySnapshot(false, null, -1, false);
		}

		return new ActivitySnapshot(
			true,
			player.getWorldLocation(),
			player.getAnimation(),
			player.getInteracting() != null);
	}

	boolean gameplayResumedSince(ActivitySnapshot previous)
	{
		if (!loggedIn || !previous.loggedIn)
		{
			return false;
		}

		boolean moved = position != null
			&& previous.position != null
			&& !position.equals(previous.position);
		boolean startedAnimation = animation != -1 && previous.animation == -1;
		boolean startedInteraction = interacting && !previous.interacting;

		return moved || startedAnimation || startedInteraction;
	}
}
