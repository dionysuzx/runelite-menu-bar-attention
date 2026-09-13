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

import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttentionLatchTest
{
	private static final WorldPoint START = new WorldPoint(3200, 3200, 0);

	private final AttentionLatch latch = new AttentionLatch();

	@Test
	public void unchangedIdleStateDoesNotClearAttention()
	{
		latch.raise(snapshot(START, -1, false));

		assertFalse(latch.observe(snapshot(START, -1, false)));
		assertTrue(latch.isPending());
	}

	@Test
	public void movingClearsAttention()
	{
		latch.raise(snapshot(START, -1, false));

		assertTrue(latch.observe(snapshot(new WorldPoint(3201, 3200, 0), -1, false)));
		assertFalse(latch.isPending());
	}

	@Test
	public void startingAnimationClearsAttention()
	{
		latch.raise(snapshot(START, -1, false));

		assertTrue(latch.observe(snapshot(START, 867, false)));
		assertFalse(latch.isPending());
	}

	@Test
	public void startingInteractionClearsAttention()
	{
		latch.raise(snapshot(START, -1, false));

		assertTrue(latch.observe(snapshot(START, -1, true)));
		assertFalse(latch.isPending());
	}

	@Test
	public void aNewNotificationResetsTheActivityBaseline()
	{
		WorldPoint moved = new WorldPoint(3201, 3200, 0);
		latch.raise(snapshot(START, -1, false));
		latch.raise(snapshot(moved, -1, false));

		assertFalse(latch.observe(snapshot(moved, -1, false)));
		assertTrue(latch.isPending());
	}

	private static ActivitySnapshot snapshot(WorldPoint position, int animation, boolean interacting)
	{
		return new ActivitySnapshot(true, position, animation, interacting);
	}
}
