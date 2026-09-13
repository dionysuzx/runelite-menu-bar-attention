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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.image.BufferedImage;

final class AttentionIconRenderer
{
	private AttentionIconRenderer()
	{
	}

	static BufferedImage render(Image source, Color color)
	{
		Dimension traySize = SystemTray.getSystemTray().getTrayIconSize();
		int width = Math.max(16, traySize.width);
		int height = Math.max(16, traySize.height);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();

		try
		{
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics.setColor(color);
			int arc = Math.max(4, Math.min(width, height) / 3);
			graphics.fillRoundRect(0, 0, width, height, arc, arc);

			int inset = Math.max(2, Math.min(width, height) / 7);
			graphics.drawImage(source, inset, inset, width - inset * 2, height - inset * 2, null);
		}
		finally
		{
			graphics.dispose();
		}

		return image;
	}
}
