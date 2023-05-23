package atm.bloodworkxgaming.serverstarter.config

import java.net.InetSocketAddress
import java.net.Proxy
import java.net.SocketAddress
import java.util.*

data class AdditionalFile(
    var url: String = "",
    var destination: String = ""
)

data class LocalFile(
    var from: String = "",
    var to: String = ""
)

data class ModpackConfig(
    var name: String = "",
    var description: String = ""
)

data class LaunchSettings(
    var spongefix: Boolean = false,
    var ramDisk: Boolean = false,
    var checkOffline: Boolean = false,
    var maxRam: String = "",
    var minRam: String = "",

    var startFile: String = "",
    var startCommand: List<String> = Collections.emptyList(),
    var javaArgs: List<String> = Collections.emptyList(),
    var autoRestart: Boolean = false,
    var crashLimit: Int = 0,
    var crashTimer: String = "",
    var preJavaArgs: String = "",

    var forcedJavaPath: String = "",

    ) {
    val processedForcedJavaPath: String
        get() {
            var str = forcedJavaPath
            val regex = Regex("\\\$\\{(.+)}")
            for (matchResult in regex.findAll(forcedJavaPath)) {
                val res = matchResult.groupValues.getOrNull(0) ?: continue
                val inner = matchResult.groupValues.getOrNull(1) ?: continue
                str = str.replace(res, System.getenv(inner))
            }

            return str
        }
}

data class HttpProxyConfig(
    var type: String? = null,
    var host: String? = null,
    var port: Int? = null,
) {
    fun toProxy(): Proxy? {
        if (type == null || host == null || port == null) {
            return null
        }
        val t = Proxy.Type.valueOf(type!!.uppercase())
        val address: SocketAddress? = when {
            host == null && port == null -> null
            host == null -> InetSocketAddress(port!!)
            else -> InetSocketAddress(host, port!!)
        }
        return Proxy(t, address)
    }
}

data class InstallConfig(
    var mcVersion: String = "",

    var loaderVersion: String = "",
    var installerUrl: String = "",
    var installerArguments: List<String> = Collections.emptyList(),

    var modpackUrl: String = "",
    var modpackFormat: String = "",
    var formatSpecific: Map<String, Any> = Collections.emptyMap(),

    var baseInstallPath: String = "",
    var ignoreFiles: List<String> = Collections.emptyList(),
    var additionalFiles: List<AdditionalFile> = Collections.emptyList(),
    var localFiles: List<LocalFile> = Collections.emptyList(),

    var checkFolder: Boolean = false,
    var installLoader: Boolean = false,
    var installPack: Boolean = true,

    var spongeBootstrapper: String = "",
    var connectTimeout: Long = 30,
    var readTimeout: Long = 30,

    var httpProxy: HttpProxyConfig = HttpProxyConfig(),
) {

    @Suppress("UNCHECKED_CAST")
    fun <T> getFormatSpecificSettingOrDefault(name: String, fallback: T?): T? {
        return formatSpecific.getOrDefault(name, fallback) as T?
    }
}

data class ConfigFile(
    var _specver: Int = 0,
    var modpack: ModpackConfig = ModpackConfig(),
    var install: InstallConfig = InstallConfig(),
    var launch: LaunchSettings = LaunchSettings()
)
