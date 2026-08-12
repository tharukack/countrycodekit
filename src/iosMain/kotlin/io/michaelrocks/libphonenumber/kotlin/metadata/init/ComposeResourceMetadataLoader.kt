/*
 * Copyright (C) 2022 The Libphonenumber Authors
 * Copyright (C) 2022 Michael Rozumyanskiy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.michaelrocks.libphonenumber.kotlin.metadata.init

import co.touchlab.kermit.Logger
import io.github.tharukack.countrycodekit.generated.resources.Res
import io.michaelrocks.libphonenumber.kotlin.MetadataLoader
import io.michaelrocks.libphonenumber.kotlin.io.InputStream
import io.michaelrocks.libphonenumber.kotlin.io.OkioInputStream
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import org.jetbrains.compose.resources.MissingResourceException
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager

/**
 * A [MetadataLoader] implementation that reads phone number metadata files as classpath
 * resources.
 */
class ComposeResourceMetadataLoader : MetadataLoader {
    override fun loadMetadata(phoneMetadataResource: String): InputStream? {
        return try {
            val fm = NSFileManager.defaultManager()
            val currentDirectoryPath = fm.currentDirectoryPath
            // Local iOS test runs read directly from the project resources.
            val path = if (currentDirectoryPath.endsWith("/CountryCodeKit") || currentDirectoryPath.endsWith("/countrycodekit")) {
                getPathOnDisk("files/$phoneMetadataResource")
            } else {
                Res.getUri("files/$phoneMetadataResource").removePrefix("file://")
            }
            val unescapedPath = path.replace("%20", " ")
            OkioInputStream(FileSystem.SYSTEM.source(unescapedPath.toPath()).buffer())
        } catch (t: Throwable) {
            logger.v("Failed to load metadata from $phoneMetadataResource", t)
            null
        }
    }

    fun getPathInBundle(path: String): String {
        // todo: support fallback path at bundle root?
        return NSBundle.mainBundle.resourcePath + "/compose-resources/" + path
    }


    private fun getPathOnDisk(path: String): String {
        val fm = NSFileManager.defaultManager()
        val currentDirectoryPath = fm.currentDirectoryPath
        return listOf(
            "$currentDirectoryPath/src/iosMain/composeResources/$path",
            "$currentDirectoryPath/src/commonMain/composeResources/$path",
            "$currentDirectoryPath/src/commonTest/composeResources/$path"
        ).firstOrNull { p -> fm.fileExistsAtPath(p) } ?: throw MissingResourceException(path)
    }

    companion object {
        private val logger = Logger.withTag(
            ComposeResourceMetadataLoader::class.simpleName.toString()
        )
    }
}
