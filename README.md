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
The plugin reuses RuneLite's existing menu-bar icon when one is available and
creates its own clickable RuneLite icon when the client did not create one.
An optional **Force focus after** timeout can bring RuneLite to the front when
the attention remains unresolved; it is disabled by default.
On macOS, the optional **Responsive menu-bar click** setting replaces RuneLite's
menu-bar click handler with an immediate, flicker-free handler. The original
handler is restored when the setting or plugin is disabled.

## Try it locally

This uses RuneLite's official external-plugin development workflow and connects
to the live game. Plugin Hub approval is not required for local testing.

1. Install Java 11, as recommended by RuneLite.
2. Run `./gradlew run` from this directory.
3. If you use a Jagex Account, follow RuneLite's
   [development-client login instructions](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).
4. Enable **Menu Bar Attention** in the development client.
5. Put RuneLite in the background and let Idle Notifier fire.
6. Bring RuneLite forward without acting. The orange indicator should remain.
7. Move, start an animation, or interact with something. The original icon
   should return.

The development client uses your normal RuneLite configuration directory. It
does not automate or inject game input. Run the checks with:

```shell
./gradlew test
```

## Install the macOS app

For a normal app icon and one-command updates on a Mac:

1. Install official RuneLite in `/Applications`.
2. Install [Homebrew](https://brew.sh), then run:

   ```shell
   brew install just
   brew install --cask temurin@17
   ```

3. Install the app:

   ```shell
   git clone https://github.com/dionysuzx/runelite-menu-bar-attention.git
   cd runelite-menu-bar-attention
   just install
   ```

4. Open **RuneLite Attention** from `/Applications`, Spotlight, or with
   `just run`.

The installer builds and tests the plugin, creates a native ad-hoc signed macOS
app with the standard JDK packaging tool, and bundles the Java runtime and
artwork from the official RuneLite app. The installed app does not need a
separate Java installation for everyday use. To bind it in Karabiner-Elements,
use this shell command:

```shell
open -a 'RuneLite Attention'
```

### Jagex Accounts

RuneLite's official development-client login setup is required once on each
Mac. Run `just configure-jagex`, add `--insecure-write-credentials` under
**Client arguments**, save, and launch official RuneLite once with the Jagex
Launcher. After that, RuneLite Attention can be opened directly until those
saved credentials expire or are revoked. Keep
`~/.runelite/credentials.properties` private.

## Plugin Hub

To submit or update the plugin, follow the
[Plugin Hub contribution guide](https://github.com/runelite/plugin-hub#creating-new-plugins).

## License

BSD 2-Clause. See `LICENSE`.
