package cz.vsb.cs.democar.webapp.conf

data class Config(
    val webappPort: Int,
    val carApiAddress: String,
    val carApiPort: Int,
    val carApiScheme: String,
) {

    companion object {

        private val env
            get() = System.getenv()

        private fun getInt(name: String, default: Int) = env[name].let {
            when (val integral = it?.toIntOrNull()) {
                null -> default
                else -> integral
            }
        }

        private fun getString(name: String, default: String) = env[name].let {
            when {
                it.isNullOrBlank() -> default
                else -> it
            }
        }

        private object Keys {
            const val webAppPort = "WEBAPP_PORT"
            const val carApiAddress = "CAR_API_ADDRESS"
            const val carApiPort = "CAR_API_PORT"
            const val carApiScheme = "CAR_API_SCHEME"
        }

        private object Defaults {
            const val webappPort = 7778
            const val carApiAddress = "127.0.0.1"
            const val carApiPort = 7478
            const val carApiScheme = "http"
        }

        val default
            get() = Config(
                webappPort    = Defaults.webappPort,
                carApiAddress = Defaults.carApiAddress,
                carApiPort    = Defaults.carApiPort,
                carApiScheme  = Defaults.carApiScheme,
            )

        val fromEnv
            get() = Config(
                carApiScheme  = getString(Keys.carApiScheme,  Defaults.carApiScheme ),
                carApiAddress = getString(Keys.carApiAddress, Defaults.carApiAddress),
                carApiPort    = getInt   (Keys.carApiPort,    Defaults.carApiPort   ),
                webappPort    = getInt   (Keys.webAppPort,    Defaults.webappPort   ),
            )
    }
}
