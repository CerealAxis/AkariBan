package com.github.cpjinan.plugin.akariban.internal.manager

import com.github.cpjinan.plugin.akarilib.utils.TimeUtil.formatToLocalDateTime
import com.github.cpjinan.plugin.akarilib.utils.TimeUtil.formatToString
import com.github.cpjinan.plugin.akarilib.utils.TimeUtil.parseTime

object TimeManager {
    fun getUnbanTime(banTime: String, banDuration: String): String {
        return banTime.formatToLocalDateTime(FormatManager.getTimeFormat())!!
            .plusSeconds(
                banDuration.parseTime(
                    second = ConfigManager.config.getString("options.time-format.duration.second")!!,
                    minute = ConfigManager.config.getString("options.time-format.duration.minute")!!,
                    hour = ConfigManager.config.getString("options.time-format.duration.hour")!!,
                    day = ConfigManager.config.getString("options.time-format.duration.day")!!,
                    week = ConfigManager.config.getString("options.time-format.duration.week")!!,
                    month = ConfigManager.config.getString("options.time-format.duration.month")!!,
                    year = ConfigManager.config.getString("options.time-format.duration.year")!!
                )
            ).formatToString(FormatManager.getTimeFormat())
    }
}