#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 /path/to/google/libphonenumber"
  exit 64
fi

source_dir="$1"
project_dir="$(cd "$(dirname "$0")/.." && pwd)"
google_java_dir="$source_dir/java/libphonenumber/src/com/google/i18n/phonenumbers"
data_source="$google_java_dir/data"
data_target="$project_dir/src/commonMain/composeResources/files"
map_target="$project_dir/src/commonMain/kotlin/io/michaelrocks/libphonenumber/kotlin/CountryCodeToRegionCodeMap.kt"
notice_dir="$project_dir/third_party/google-libphonenumber"

test -f "$google_java_dir/CountryCodeToRegionCodeMap.java"
test -d "$data_source"
test -f "$source_dir/LICENSE"

mkdir -p "$data_target" "$(dirname "$map_target")" "$notice_dir"
find "$data_target" -maxdepth 1 -type f -delete
cp "$data_source"/* "$data_target"/
cp "$source_dir/LICENSE" "$notice_dir/LICENSE"

{
  echo '/*'
  echo ' * Generated from Google libphonenumber CountryCodeToRegionCodeMap.java.'
  echo ' * The source version is recorded in the repository UPSTREAMS.properties.'
  echo ' */'
  echo 'package io.michaelrocks.libphonenumber.kotlin'
  echo
  echo 'import kotlin.jvm.JvmStatic'
  echo
  echo 'object CountryCodeToRegionCodeMap {'
  echo '    @JvmStatic'
  echo '    val countryCodeToRegionCodeMap: Map<Int, List<String>> = mapOf('

  regions=""
  while IFS= read -r line; do
    if [[ "$line" == *'listWithRegionCode = new ArrayList'* ]]; then
      regions=""
    elif [[ "$line" == *'listWithRegionCode.add("'* ]]; then
      region="$(printf '%s' "$line" | sed -E 's/.*add\("([^"]+)"\).*/\1/')"
      if [[ -n "$regions" ]]; then regions="$regions, "; fi
      regions="${regions}\"${region}\""
    elif [[ "$line" == *'countryCodeToRegionCodeMap.put('* ]]; then
      calling_code="$(printf '%s' "$line" | sed -E 's/.*put\(([0-9]+),.*/\1/')"
      echo "        $calling_code to listOf($regions),"
    fi
  done < "$google_java_dir/CountryCodeToRegionCodeMap.java"

  echo '    )'
  echo '}'
} > "$map_target"

echo "Synced Google libphonenumber from $source_dir"
