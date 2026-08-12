# Flag rasterizer

Build-time-only SVG-to-PNG renderer used by `scripts/sync_flag_icons.sh`.
It uses Apache Batik 1.19 (Apache-2.0) to produce deterministic 128x96,
transparent PNG resources. Batik is not included in the published CountryCodeKit
runtime artifact.
