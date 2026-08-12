/*
 * Copyright (C) 2014 The Libphonenumber Authors
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
package io.michaelrocks.libphonenumber.kotlin.internal

import io.michaelrocks.libphonenumber.kotlin.Phonemetadata.PhoneNumberDesc
import kotlin.jvm.JvmStatic

/**
 * Implementation of the matcher API using the regular expressions in the PhoneNumberDesc
 * proto message to match numbers.
 */
class RegexBasedMatcher private constructor() : MatcherApi {
    private val regexCache = RegexCache(100)

    // @Override
    override fun matchNationalNumber(
        number: CharSequence, numberDesc: PhoneNumberDesc, allowPrefixMatch: Boolean
    ): Boolean {
        val nationalNumberPattern = numberDesc.nationalNumberPattern
        // We don't want to consider it a prefix match when matching non-empty input against an empty
        // pattern.
        return if (nationalNumberPattern.isEmpty()) {
            false
        } else match(number, regexCache.getRegexForPattern(nationalNumberPattern), allowPrefixMatch);
    }

    companion object {
        @JvmStatic
        fun create(): MatcherApi {
            return RegexBasedMatcher()
        }

        private fun match(number: CharSequence, regex: Regex, allowPrefixMatch: Boolean): Boolean {
            // Kotlin/Native's matchesAt can disagree with matchEntire for some nested metadata
            // patterns (for example Curaçao's current fixed-line expression). Exact validation is
            // the common path, so evaluate it directly. A prefix match mirrors Java's lookingAt().
            if (regex.matchEntire(number) != null) return true
            if (!allowPrefixMatch) return false
            return regex.find(number, 0)?.range?.first == 0
        }
    }
}
