package com.anataw12.arccommand

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.network.NetworkCheckHandler
import net.minecraftforge.fml.relauncher.Side

@Mod(modid = RtmBezier.MOD_ID)
object RtmBezier {
    const val MOD_ID = "arc_command"

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        event.registerServerCommand(CommandArc)
    }

    @NetworkCheckHandler
    fun netCheckHandler(mods: Map<String?, String?>?, side: Side?): Boolean {
        return true
    }

    @Mod.InstanceFactory @JvmStatic fun get() = this
}

