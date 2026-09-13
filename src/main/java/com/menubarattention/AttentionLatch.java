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

final class AttentionLatch
{
	private volatile boolean pending;
	private ActivitySnapshot previous;

	void raise(ActivitySnapshot snapshot)
	{
		pending = true;
		previous = snapshot;
	}

	boolean observe(ActivitySnapshot snapshot)
	{
		if (!pending)
		{
			return false;
		}

		boolean resumed = snapshot.gameplayResumedSince(previous);
		previous = snapshot;
		if (resumed)
		{
			pending = false;
		}

		return resumed;
	}

	void clear()
	{
		pending = false;
		previous = null;
	}

	boolean isPending()
	{
		return pending;
	}
}
