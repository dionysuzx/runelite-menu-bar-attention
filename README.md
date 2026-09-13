# Menu Bar Attention

A RuneLite external plugin for macOS. When RuneLite fires a notification while
it is in the background, the existing RuneLite menu-bar icon flashes orange and
stays in its attention state until the character genuinely resumes gameplay.
Merely focusing RuneLite does not clear it.

The indicator clears after any of these transitions following the most recent
notification:

- the player moves to a different world tile;
- a new animation starts; or
- a new interaction with another actor starts.

Logging out or disabling the plugin restores the original icon immediately.

## Try it locally

This uses RuneLite's official external-plugin development workflow and connects
to the live game. Plugin Hub approval is not required for local testing.

1. Install Java 11, as recommended by RuneLite.
2. Run `./gradlew run` from this directory.
3. If you use a Jagex Account, follow RuneLite's
   [development-client login instructions](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).
4. Make sure RuneLite's **Enable tray icon** option is enabled.
5. Enable **Menu Bar Attention** in the development client.
6. Put RuneLite in the background and let Idle Notifier fire.
7. Bring RuneLite forward without acting. The orange indicator should remain.
8. Move, start an animation, or interact with something. The original icon
   should return.

The development client uses your normal RuneLite configuration directory. It
does not automate or inject game input. Run the checks with:

```shell
./gradlew test
```

## Plugin Hub

To submit or update the plugin, follow the
[Plugin Hub contribution guide](https://github.com/runelite/plugin-hub#creating-new-plugins).

## License

BSD 2-Clause. See `LICENSE`.
