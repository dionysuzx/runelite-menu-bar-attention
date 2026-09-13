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

import com.google.inject.Provides;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientUI;

@Slf4j
@PluginDescriptor(
	name = "Menu Bar Attention",
	description = "Keeps RuneLite's menu-bar icon orange until gameplay resumes",
	tags = {"notification", "alert", "afk", "idle", "mac", "macos", "menu bar", "system tray"}
)
public class MenuBarAttentionPlugin extends Plugin
{
	private static final String ATTENTION_TOOLTIP = "RuneLite - action required";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientUI clientUI;

	@Inject
	private MenuBarAttentionConfig config;

	@Inject
	private ScheduledExecutorService executor;

	private final AttentionLatch attention = new AttentionLatch();
	private final AtomicLong renderGeneration = new AtomicLong();

	private volatile boolean running;
	private ScheduledFuture<?> setupTask;
	private ScheduledFuture<?> blinkTask;
	private ScheduledFuture<?> focusTask;
	private TrayIcon modifiedTrayIcon;
	private TrayIcon ownedTrayIcon;
	private Image originalImage;
	private String originalToolTip;
	private volatile boolean orangePhase;

	@Provides
	MenuBarAttentionConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MenuBarAttentionConfig.class);
	}

	@Override
	protected void startUp()
	{
		running = true;
		if (!SystemTray.isSupported())
		{
			log.debug("System tray is not supported");
			return;
		}

		setupTask = executor.schedule(() ->
		{
			if (running)
			{
				requestIcon(false);
			}
		}, 1, TimeUnit.SECONDS);
	}

	@Override
	protected void shutDown()
	{
		running = false;
		attention.clear();
		cancelTask(setupTask);
		setupTask = null;
		cancelBlinkTask();
		cancelFocusTask();
		orangePhase = false;

		long generation = renderGeneration.incrementAndGet();
		EventQueue.invokeLater(() -> restoreAndRemoveIcon(generation));
	}

	@Subscribe
	public void onNotificationFired(NotificationFired event)
	{
		if (!SystemTray.isSupported() || (!config.whenFocused() && clientUI.isFocused()))
		{
			return;
		}

		clientThread.invokeLater(() -> raiseAttention(ActivitySnapshot.capture(client)));
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (attention.isPending() && attention.observe(ActivitySnapshot.capture(client)))
		{
			clearAttention();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			clearAttention();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!MenuBarAttentionConfig.GROUP.equals(event.getGroup()) || !attention.isPending())
		{
			return;
		}

		startRendering();
		scheduleFocusEscalation();
	}

	private void raiseAttention(ActivitySnapshot snapshot)
	{
		attention.raise(snapshot);
		startRendering();
		scheduleFocusEscalation();
	}

	private void startRendering()
	{
		cancelBlinkTask();
		orangePhase = true;
		requestIcon(true);

		if (config.blink())
		{
			blinkTask = executor.scheduleAtFixedRate(() ->
			{
				if (!attention.isPending())
				{
					return;
				}

				orangePhase = !orangePhase;
				requestIcon(orangePhase);
			}, config.blinkInterval(), config.blinkInterval(), TimeUnit.MILLISECONDS);
		}
	}

	private void clearAttention()
	{
		attention.clear();
		cancelBlinkTask();
		cancelFocusTask();
		orangePhase = false;
		requestIcon(false);
	}

	private void cancelBlinkTask()
	{
		cancelTask(blinkTask);
		blinkTask = null;
	}

	private static void cancelTask(ScheduledFuture<?> task)
	{
		if (task != null)
		{
			task.cancel(false);
		}
	}

	private void scheduleFocusEscalation()
	{
		cancelFocusTask();
		int delayMinutes = config.forceFocusAfter();
		if (delayMinutes == 0)
		{
			return;
		}

		focusTask = executor.schedule(() ->
		{
			if (attention.isPending())
			{
				EventQueue.invokeLater(() ->
				{
					if (attention.isPending())
					{
						clientUI.forceFocus();
					}
				});
			}
		}, delayMinutes, TimeUnit.MINUTES);
	}

	private void cancelFocusTask()
	{
		cancelTask(focusTask);
		focusTask = null;
	}

	private void requestIcon(boolean showAttention)
	{
		long generation = renderGeneration.incrementAndGet();
		EventQueue.invokeLater(() -> renderIcon(generation, showAttention));
	}

	private void renderIcon(long generation, boolean showAttention)
	{
		if (generation != renderGeneration.get())
		{
			return;
		}

		TrayIcon trayIcon = getOrCreateTrayIcon();
		if (trayIcon == null)
		{
			return;
		}

		if (modifiedTrayIcon != trayIcon)
		{
			modifiedTrayIcon = trayIcon;
			originalImage = trayIcon.getImage();
			originalToolTip = trayIcon.getToolTip();
		}

		if (showAttention && attention.isPending())
		{
			trayIcon.setImage(AttentionIconRenderer.render(originalImage, config.attentionColor()));
			trayIcon.setToolTip(ATTENTION_TOOLTIP);
		}
		else
		{
			trayIcon.setImage(originalImage);
			trayIcon.setToolTip(originalToolTip);
		}
	}

	private TrayIcon getOrCreateTrayIcon()
	{
		TrayIcon runeLiteTrayIcon = clientUI.getTrayIcon();
		if (runeLiteTrayIcon != null)
		{
			if (ownedTrayIcon != null)
			{
				SystemTray.getSystemTray().remove(ownedTrayIcon);
				ownedTrayIcon = null;
			}
			return runeLiteTrayIcon;
		}

		if (ownedTrayIcon != null || !running)
		{
			return ownedTrayIcon;
		}

		TrayIcon trayIcon = new TrayIcon(ClientUI.ICON_16, "RuneLite");
		trayIcon.setImageAutoSize(true);
		trayIcon.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				clientUI.forceFocus();
			}
		});

		try
		{
			SystemTray.getSystemTray().add(trayIcon);
			ownedTrayIcon = trayIcon;
			return trayIcon;
		}
		catch (AWTException ex)
		{
			log.warn("Unable to create menu-bar icon", ex);
			return null;
		}
	}

	private void restoreAndRemoveIcon(long generation)
	{
		if (generation != renderGeneration.get())
		{
			return;
		}

		if (modifiedTrayIcon != null)
		{
			modifiedTrayIcon.setImage(originalImage);
			modifiedTrayIcon.setToolTip(originalToolTip);
		}

		if (ownedTrayIcon != null)
		{
			SystemTray.getSystemTray().remove(ownedTrayIcon);
		}

		modifiedTrayIcon = null;
		ownedTrayIcon = null;
		originalImage = null;
		originalToolTip = null;
	}
}
