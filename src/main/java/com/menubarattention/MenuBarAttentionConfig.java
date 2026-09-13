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

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(MenuBarAttentionConfig.GROUP)
public interface MenuBarAttentionConfig extends Config
{
	String GROUP = "menu-bar-attention";

	@ConfigItem(
		keyName = "attentionColor",
		name = "Attention color",
		description = "Color shown behind the RuneLite menu-bar icon"
	)
	default Color attentionColor()
	{
		return new Color(255, 128, 0);
	}

	@ConfigItem(
		keyName = "blink",
		name = "Blink",
		description = "Alternate between the normal and attention icons until gameplay resumes"
	)
	default boolean blink()
	{
		return true;
	}

	@Range(min = 250, max = 2_000)
	@ConfigItem(
		keyName = "blinkInterval",
		name = "Blink interval",
		description = "Milliseconds between icon changes"
	)
	default int blinkInterval()
	{
		return 700;
	}

	@ConfigItem(
		keyName = "whenFocused",
		name = "Alert while focused",
		description = "Also show attention for notifications fired while RuneLite is already focused"
	)
	default boolean whenFocused()
	{
		return false;
	}
}
