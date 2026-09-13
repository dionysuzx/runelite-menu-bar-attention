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

import java.awt.Canvas;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResponsiveClickListenerTest
{
	private final AtomicInteger focusRequests = new AtomicInteger();
	private final ResponsiveClickListener listener = new ResponsiveClickListener(focusRequests::incrementAndGet);

	@Test
	public void primaryPressRequestsFocus()
	{
		listener.mousePressed(press(MouseEvent.BUTTON1));

		assertEquals(1, focusRequests.get());
	}

	@Test
	public void secondaryPressDoesNotRequestFocus()
	{
		listener.mousePressed(press(MouseEvent.BUTTON3));

		assertEquals(0, focusRequests.get());
	}

	private static MouseEvent press(int button)
	{
		return new MouseEvent(
			new Canvas(), MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, 1, false, button);
	}
}
