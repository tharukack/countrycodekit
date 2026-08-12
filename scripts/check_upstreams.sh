#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")/.." && pwd)"
properties="$project_dir/UPSTREAMS.properties"

read_property() {
  sed -n "s/^$1=//p" "$properties"
}

flag_version="$(read_property flagIcons.version)"
phone_version="$(read_property googleLibphonenumber.version)"
port_commit="$(read_property kotlinPort.baselineCommit)"

test -n "$flag_version"
test -n "$phone_version"
test -n "$port_commit"
grep -q "googleLibphonenumber.version=${phone_version}" "$properties"
grep -q "flag-icons ${flag_version}" "$project_dir/CHANGELOG.md"
grep -q "Google ${phone_version}" "$project_dir/CHANGELOG.md"

flag_count="$(find "$project_dir/src/commonMain/composeResources/drawable" -name 'flag_*.png' | wc -l | tr -d ' ')"
metadata_count="$(find "$project_dir/src/commonMain/composeResources/files" -type f | wc -l | tr -d ' ')"
test "$flag_count" -ge 250
test "$metadata_count" -ge 500

if find "$project_dir/src/commonMain/composeResources/drawable" -name 'flag_*.svg' | grep -q .; then
  echo "Raw SVG flags are not supported by Android; run scripts/sync_flag_icons.sh."
  exit 1
fi

if grep -R -q 'io\.github\.luca992' "$project_dir/src"; then
  echo "The maintained port still contains the old upstream resource package."
  exit 1
fi

if [[ "${1:-}" == "--remote" ]]; then
  command -v curl >/dev/null
  command -v jq >/dev/null
  latest_flag="$(curl -fsSL https://api.github.com/repos/lipis/flag-icons/releases/latest | jq -r '.tag_name | ltrimstr("v")')"
  latest_phone="$(curl -fsSL https://api.github.com/repos/google/libphonenumber/releases/latest | jq -r '.tag_name | ltrimstr("v")')"
  latest_port="$(curl -fsSL https://api.github.com/repos/luca992/libphonenumber-kotlin/commits/master | jq -r '.sha')"

  stale=0
  [[ "$latest_flag" == "$flag_version" ]] || { echo "flag-icons: pinned $flag_version, latest $latest_flag"; stale=1; }
  [[ "$latest_phone" == "$phone_version" ]] || { echo "libphonenumber: pinned $phone_version, latest $latest_phone"; stale=1; }
  [[ "$latest_port" == "$port_commit" ]] || { echo "Kotlin baseline: pinned $port_commit, latest $latest_port"; stale=1; }
  exit "$stale"
fi

echo "Upstream pins and vendored assets are internally consistent."
