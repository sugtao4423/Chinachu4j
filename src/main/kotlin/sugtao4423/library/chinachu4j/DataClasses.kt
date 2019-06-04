package sugtao4423.library.chinachu4j

import java.io.Serializable

data class Channel(
        val n: Int,
        val type: String,
        val channel: String,
        val name: String,
        val id: String,
        val sid: Int
) : Serializable

data class Program(
        val id: String,
        val category: String,
        val title: String,
        val subTitle: String,
        val fullTitle: String,
        val detail: String,
        val episode: Int,
        val start: Long,
        val end: Long,
        val seconds: Int,
        val flags: Array<String>,
        val channel: Channel
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Program

        if (id != other.id) return false
        if (category != other.category) return false
        if (title != other.title) return false
        if (subTitle != other.subTitle) return false
        if (fullTitle != other.fullTitle) return false
        if (detail != other.detail) return false
        if (episode != other.episode) return false
        if (start != other.start) return false
        if (end != other.end) return false
        if (seconds != other.seconds) return false
        if (!flags.contentEquals(other.flags)) return false
        if (channel != other.channel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + subTitle.hashCode()
        result = 31 * result + fullTitle.hashCode()
        result = 31 * result + detail.hashCode()
        result = 31 * result + episode
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + seconds
        result = 31 * result + flags.contentHashCode()
        result = 31 * result + channel.hashCode()
        return result
    }
}

data class Recorded(
        val program: Program,
        val isManualReserved: Boolean,
        val isConflict: Boolean,
        val recordedFormat: String,
        val isSigTerm: Boolean,
        val tuner: Tuner,
        val recorded: String,
        val command: String
) : Serializable

data class Reserve(
        val program: Program,
        val isManualReserved: Boolean,
        val isConflict: Boolean,
        val recordedFormat: String,
        val isSkip: Boolean
) : Serializable

data class Rule(
        val types: Array<String>,
        val categories: Array<String>,
        val channels: Array<String>,
        val ignoreChannels: Array<String>,
        val reserveFlags: Array<String>,
        val ignoreFlags: Array<String>,
        val start: Int,
        val end: Int,
        val min: Int,
        val max: Int,
        val reserveTitles: Array<String>,
        val ignoreTitles: Array<String>,
        val reserveDescriptions: Array<String>,
        val ignoreDescriptions: Array<String>,
        val recordedFormat: String,
        val isDisabled: Boolean
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rule

        if (!types.contentEquals(other.types)) return false
        if (!categories.contentEquals(other.categories)) return false
        if (!channels.contentEquals(other.channels)) return false
        if (!ignoreChannels.contentEquals(other.ignoreChannels)) return false
        if (!reserveFlags.contentEquals(other.reserveFlags)) return false
        if (!ignoreFlags.contentEquals(other.ignoreFlags)) return false
        if (start != other.start) return false
        if (end != other.end) return false
        if (min != other.min) return false
        if (max != other.max) return false
        if (!reserveTitles.contentEquals(other.reserveTitles)) return false
        if (!ignoreTitles.contentEquals(other.ignoreTitles)) return false
        if (!reserveDescriptions.contentEquals(other.reserveDescriptions)) return false
        if (!ignoreDescriptions.contentEquals(other.ignoreDescriptions)) return false
        if (recordedFormat != other.recordedFormat) return false
        if (isDisabled != other.isDisabled) return false

        return true
    }

    override fun hashCode(): Int {
        var result = types.contentHashCode()
        result = 31 * result + categories.contentHashCode()
        result = 31 * result + channels.contentHashCode()
        result = 31 * result + ignoreChannels.contentHashCode()
        result = 31 * result + reserveFlags.contentHashCode()
        result = 31 * result + ignoreFlags.contentHashCode()
        result = 31 * result + start
        result = 31 * result + end
        result = 31 * result + min
        result = 31 * result + max
        result = 31 * result + reserveTitles.contentHashCode()
        result = 31 * result + ignoreTitles.contentHashCode()
        result = 31 * result + reserveDescriptions.contentHashCode()
        result = 31 * result + ignoreDescriptions.contentHashCode()
        result = 31 * result + recordedFormat.hashCode()
        result = 31 * result + isDisabled.hashCode()
        return result
    }
}

data class Tuner(
        val name: String,
        val isScrambling: Boolean,
        val types: Array<String>,
        val command: String,
        val n: Int
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tuner

        if (name != other.name) return false
        if (isScrambling != other.isScrambling) return false
        if (!types.contentEquals(other.types)) return false
        if (command != other.command) return false
        if (n != other.n) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + isScrambling.hashCode()
        result = 31 * result + types.contentHashCode()
        result = 31 * result + command.hashCode()
        result = 31 * result + n
        return result
    }
}