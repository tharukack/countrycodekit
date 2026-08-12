#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 /path/to/flag-icons"
  exit 64
fi

source_dir="$1"
project_dir="$(cd "$(dirname "$0")/.." && pwd)"
resource_dir="$project_dir/src/commonMain/composeResources/drawable"
generated_dir="$project_dir/src/commonMain/kotlin/io/github/tharukack/countrycodekit/internal"
license_dir="$project_dir/third_party/flag-icons"
svg_stage="$project_dir/build/flag-sync/svg"
png_stage="$project_dir/build/flag-sync/png"

test -f "$source_dir/country.json"
test -f "$source_dir/LICENSE"
mkdir -p "$resource_dir" "$generated_dir" "$license_dir" "$svg_stage" "$png_stage"

find "$svg_stage" -maxdepth 1 -name 'flag_*.svg' -delete
find "$png_stage" -maxdepth 1 -name 'flag_*.png' -delete
while IFS= read -r code; do
  normalized="${code//-/_}"
  cp "$source_dir/flags/4x3/$code.svg" "$svg_stage/flag_$normalized.svg"
done < <(find "$source_dir/flags/4x3" -maxdepth 1 -name '*.svg' -exec basename {} .svg \; | LC_ALL=C sort)

"$project_dir/gradlew" -p "$project_dir/tools/flag-rasterizer" run \
  --args="$svg_stage $png_stage" \
  --quiet

find "$resource_dir" -maxdepth 1 \( -name 'flag_*.svg' -o -name 'flag_*.png' \) -delete
cp "$png_stage"/flag_*.png "$resource_dir/"
cp "$source_dir/LICENSE" "$license_dir/LICENSE"

{
  echo '@file:Suppress("SpellCheckingInspection")'
  echo
  echo 'package io.github.tharukack.countrycodekit.internal'
  echo
  echo 'import io.github.tharukack.countrycodekit.generated.resources.*'
  echo 'import org.jetbrains.compose.resources.DrawableResource'
  echo
  echo 'internal fun flagResourceFor(isoCode: String): DrawableResource? = when (isoCode.uppercase()) {'
  while IFS= read -r code; do
    normalized="${code//-/_}"
    if [[ ${#code} -eq 2 ]]; then
      echo "    \"${code^^}\" -> Res.drawable.flag_$normalized"
    fi
  done < <(find "$source_dir/flags/4x3" -maxdepth 1 -name '*.svg' -exec basename {} .svg \; | LC_ALL=C sort)
  echo '    "AC" -> Res.drawable.flag_sh_ac'
  echo '    "TA" -> Res.drawable.flag_sh_ta'
  echo '    else -> null'
  echo '}'
} > "$generated_dir/FlagResources.generated.kt"

{
  echo '@file:Suppress("SpellCheckingInspection")'
  echo
  echo 'package io.github.tharukack.countrycodekit.internal'
  echo
  echo 'internal val countryNames: Map<String, String> = mapOf('
  jq -r '.[] | select(.code | length == 2) | "    \"\(.code | ascii_upcase)\" to \(.name | @json),"' "$source_dir/country.json" | LC_ALL=C sort
  echo '    "AC" to "Ascension Island",'
  echo '    "TA" to "Tristan da Cunha",'
  echo ')'
} > "$generated_dir/CountryNames.generated.kt"

echo "Synced flag-icons from $source_dir"
