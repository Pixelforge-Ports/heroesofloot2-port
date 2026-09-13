# Input inspection — Heroes of Loot 2

Inspected the supplied Windows/GOG 1.5.2 installation without executing its EXE or installer. All 150 files were inventoried with paths, sizes, format signatures and SHA256 hashes. Every member of `HeroesOfLoot2.jar` (3368 entries) and `webcache.zip` (9 entries) was read, hashed and ZIP CRC checked.

`HeroesOfLoot2.jar` is a Java ZIP/JAR despite being part of a Windows installation. Manifest entry point: `com.orangepixel.dungeon2.Main`. Game implementation: `com.orangepixel.dungeon2.myCanvas`. Supported fingerprint: `67cff11e557a3660c4276058a3ebc62b03938a1ab4822652d48abf31b273c9e6`.

The bundled runtime, EXEs, uninstaller, shortcut, store metadata, icons, legal text and web cache were inventoried; the handheld package only needs the unchanged original JAR. Selected launcher, engine, preferences, input and save classes were inspected privately to identify adaptation requirements. This is an inventory of every file and archive member, not a claim that every instruction in every binary was reverse engineered.

## Native library inspection

The JAR contains AArch64 (ELF machine 183) libGDX/FreeType and LWJGL graphics/audio libraries. GLFW and OpenAL require GLIBC 2.27. OpenAL requires compatible GLIBCXX_3.4.22 and CXXABI_1.3.9. The archive also includes Jamepad requiring GLIBC 2.29, but the host disables native gamepad polling; class-loading checks verify that Jamepad's ControllerManager is not initialized in the tested path. Storefront native code is bypassed by the offline social adapter.

## Port decisions

The original desktop launcher uses a 1280x720 window and a 60 FPS ceiling. Heroes of Loot 2 has a first-run introduction and separate ranged and melee character selections before gameplay. Its menus use arrow keys and gameplay uses WASD, so the host mirrors arrows into WASD. The package provides single-player keyboard controls; local co-op is not mapped or tested.

The replacement host restores its graphics bridge after the LWJGL window makes its context current, preserves offscreen framebuffer sizes, and fits a minimum 16:9 view to the physical display. Controller input comes through the PortMaster mapper. Vendor code/assets stay in the original JAR; no decompiled vendor implementation is shipped in the source or BYO ZIP.

Machine-readable inventories remain local under `build/audit` and are excluded from the distributable source. The inspection tool can reproduce them from an owned installation.
