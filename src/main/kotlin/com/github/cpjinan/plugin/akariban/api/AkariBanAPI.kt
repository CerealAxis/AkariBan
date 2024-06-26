package com.github.cpjinan.plugin.akariban.api

import com.github.cpjinan.plugin.akariban.common.event.ban.OfflinePlayerBanEvent
import com.github.cpjinan.plugin.akariban.common.event.ban.OnlinePlayerBanEvent
import com.github.cpjinan.plugin.akariban.common.event.kick.PlayerKickEvent
import com.github.cpjinan.plugin.akariban.common.event.unban.PlayerUnbanEvent
import com.github.cpjinan.plugin.akariban.common.event.whitelist.PlayerWhitelistEvent
import com.github.cpjinan.plugin.akariban.internal.manager.ConfigManager
import com.github.cpjinan.plugin.akariban.internal.manager.DatabaseManager
import com.github.cpjinan.plugin.akariban.internal.manager.FormatManager
import com.github.cpjinan.plugin.akariban.internal.manager.FormatManager.getPlayerID
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

object AkariBanAPI {
    private inline fun <reified T : BukkitProxyEvent> callPluginEvent(event: T, action: T.() -> Unit) {
        event.call()
        if (event.isCancelled) return
        action(event)
    }

    private fun kick(
        player: Player,
        kickReason: String = "",
        kickTime: String = "",
        kickingAdmin: String = ""
    ) {
        callPluginEvent(PlayerKickEvent(player, kickReason, kickTime, kickingAdmin)) {
            this.player.kickPlayer(
                FormatManager.getKickFormat(
                    this.player,
                    this.kickReason,
                    this.kickTime,
                    this.kickingAdmin
                )
            )
        }
    }

    private fun whitelist(
        playerID: String,
        isWhitelisted: Boolean = false,
        whitelistTime: String = "",
        whitelistingAdmin: String = ""
    ) {
        callPluginEvent(PlayerWhitelistEvent(playerID, isWhitelisted, whitelistTime, whitelistingAdmin)) {
            setPlayerWhitelistData(
                playerID = this.playerID,
                isWhitelisted = this.isWhitelisted,
                whitelistTime = this.whitelistTime,
                whitelistingAdmin = this.whitelistingAdmin
            )
        }
    }

    private fun onlineBan(
        player: Player,
        banReason: String = "",
        banDuration: String = "",
        banTime: String = "",
        banningAdmin: String = ""
    ) {
        callPluginEvent(OnlinePlayerBanEvent(player, banReason, banDuration, banTime, banningAdmin)) {
            setPlayerData(
                playerID = this.player.getPlayerID(),
                isBanned = true,
                banReason = this.banReason,
                banningAdmin = this.banningAdmin,
                banTime = this.banTime,
                banDuration = this.banDuration
            )
            player.kickPlayer(
                FormatManager.getBanFormat(
                    this.player,
                    this.banReason,
                    this.banDuration,
                    this.banTime,
                    ConfigManager.getUnbanTime(this.banTime, this.banDuration),
                    this.banningAdmin
                )
            )
        }
    }

    private fun offlineBan(
        playerName: String = "",
        banReason: String = "",
        banDuration: String = "",
        banTime: String = "",
        banningAdmin: String = ""
    ) {
        callPluginEvent(OfflinePlayerBanEvent(playerName, banReason, banDuration, banTime, banningAdmin)) {
            setPlayerData(
                playerID = playerName.getPlayerID(),
                isBanned = true,
                banReason = this.banReason,
                banningAdmin = this.banningAdmin,
                banTime = this.banTime,
                banDuration = this.banDuration
            )
        }
    }

    private fun unban(
        playerID: String,
    ) {
        callPluginEvent(PlayerUnbanEvent(playerID)) {
            setPlayerData(
                playerID = playerID,
                isBanned = false,
                banReason = "",
                banningAdmin = "",
                banTime = "",
                banDuration = ""
            )
        }
    }

    private fun setPlayerData(
        playerID: String,
        isBanned: Boolean = false,
        banReason: String = "",
        banningAdmin: String = "",
        banTime: String = "",
        banDuration: String = ""
    ) {
        val db = DatabaseManager.getDatabase()
        val data = db.getPlayerByName(playerID)
        data.isBanned = isBanned
        data.banTime = banTime
        data.unbanTime = ConfigManager.getUnbanTime(banTime, banDuration)
        data.banDuration = banDuration
        data.banReason = banReason
        data.banningAdmin = banningAdmin
        db.updatePlayer(playerID, data)
        db.save()
    }

    private fun setPlayerWhitelistData(
        playerID: String,
        isWhitelisted: Boolean = false,
        whitelistingAdmin: String = "",
        whitelistTime: String = ""
    ) {
        val db = DatabaseManager.getDatabase()
        val data = db.getPlayerByName(playerID)
        data.isWhitelisted = isWhitelisted
        data.whitelistingAdmin = whitelistingAdmin
        data.whitelistTime = whitelistTime
        db.updatePlayer(playerID, data)
        db.save()
    }

    fun kickPlayer(player: Player, kickReason: String, kickTime: String, kickingAdmin: String) {
        kick(player, kickReason, kickTime, kickingAdmin)
    }

    fun banOnlinePlayer(
        player: Player,
        banReason: String = "",
        banDuration: String = "",
        banTime: String = "",
        banningAdmin: String = ""
    ) {
        onlineBan(player, banReason, banDuration, banTime, banningAdmin)
    }

    fun banOfflinePlayer(
        playerName: String = "",
        banReason: String = "",
        banDuration: String = "",
        banTime: String = "",
        banningAdmin: String = ""
    ) {
        offlineBan(playerName, banReason, banDuration, banTime, banningAdmin)
    }

    fun unbanPlayer(
        playerID: String
    ) {
        unban(playerID)
    }

    fun whitelistPlayer(
        playerID: String,
        isWhitelisted: Boolean = false,
        whitelistTime: String = "",
        whitelistingAdmin: String = ""
    ) {
        whitelist(
            playerID, isWhitelisted, whitelistTime, whitelistingAdmin
        )
    }
}