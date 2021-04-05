package cz.vsb.cs.democar.webapp

data class Config(
    val webappPort: Int,
    val carApiAddress: String,
    val carApiPort: Int,
    val carApiScheme: String
) {

    companion object {
        val default
            get() = Config(7778, "127.0.0.1", 7478, "http")
    }

}
