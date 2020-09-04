@file:JvmName("MainKt")

package org.hydev

import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import org.zeroturnaround.exec.ProcessExecutor
import java.awt.*
import java.awt.Toolkit.getDefaultToolkit
import java.awt.event.KeyEvent
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.exitProcess

private lateinit var currentIcon: TrayIcon

class GlobalKeyListener : NativeKeyListener {
    override fun nativeKeyTyped(p0: NativeKeyEvent?) {}
    override fun nativeKeyPressed(p0: NativeKeyEvent?) {}

    override fun nativeKeyReleased(p0: NativeKeyEvent?) {
        if (p0?.keyCode == NativeKeyEvent.VC_CAPS_LOCK)
            currentIcon.image = getTrayImage()
    }
}

fun main() {
    // Hide java icon in dock.
    System.setProperty("apple.awt.UIElement", "true")

    // Disable debug log.
    val logger = Logger.getLogger(GlobalScreen::class.java.getPackage().name)
    logger.level = Level.WARNING

    // Build quit menu.
    val quitMenuItem = MenuItem("Quit").apply { addActionListener { exitProcess(0) } }
    val popupMenu = PopupMenu().apply { add(quitMenuItem) }

    // Set tray icon & popup menu.
    currentIcon = TrayIcon(getTrayImage(), "Caps lock Indicator")
    currentIcon.popupMenu = popupMenu
    SystemTray.getSystemTray().add(currentIcon)

    // Register keyboard hook and add listener.
    GlobalScreen.registerNativeHook()
    GlobalScreen.addNativeKeyListener(GlobalKeyListener())
}

fun getTrayImage(): Image {
    val capsLockStatusString = if (getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) "On" else "Off"
    val styleModeString = if (ProcessExecutor().command("defaults", "read", "-g", "AppleInterfaceStyle")
            .readOutput(true).execute().outputUTF8() == "Dark\n"
    ) "Dark" else "Light"

    // On_Dark.png | On_Light.png | Off_Dark.png | Off_Light.png
    val fileName = "${capsLockStatusString}_$styleModeString.png"
    return getDefaultToolkit().createImage(object {}.javaClass.getResource("/$fileName").readBytes())
}
