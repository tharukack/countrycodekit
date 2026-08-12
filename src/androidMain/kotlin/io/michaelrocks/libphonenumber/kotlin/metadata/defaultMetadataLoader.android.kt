package io.michaelrocks.libphonenumber.kotlin.metadata

import io.michaelrocks.libphonenumber.kotlin.MetadataLoader
import io.michaelrocks.libphonenumber.kotlin.applicationContext
import io.michaelrocks.libphonenumber.kotlin.io.InputStream
import io.michaelrocks.libphonenumber.kotlin.io.JavaInputStream
import io.michaelrocks.libphonenumber.kotlin.metadata.source.AssetsMetadataLoader
import java.io.File

actual fun defaultMetadataLoader(): MetadataLoader {
    val assets = applicationContext?.assets
    if (assets == null) {
        return ProjectResourceMetadataLoader()
    }

    return AssetsMetadataLoader(assets)
}

/** Reads source resources when common tests run as local Android JVM tests. */
private class ProjectResourceMetadataLoader : MetadataLoader {
    override fun loadMetadata(phoneMetadataResource: String): InputStream? {
        val metadataFile = File(
            "src/commonMain/composeResources/files/$phoneMetadataResource",
        )
        return metadataFile
            .takeIf(File::isFile)
            ?.inputStream()
            ?.let(::JavaInputStream)
    }
}
