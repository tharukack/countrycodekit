# Third-party notices

CountryCodeKit bundles PNG renditions of SVG flag artwork from `lipis/flag-icons` under the MIT
license. Phone parsing and validation derive from Google libphonenumber and a
Kotlin Multiplatform port under Apache License 2.0. Complete upstream notices
are stored alongside their corresponding assets and port sources.

## Phone engine provenance

- Kotlin port baseline: `luca992/libphonenumber-kotlin` commit
  `a2f7c845ed050bd704839513054c2c747e330429`.
- Google source and metadata: libphonenumber `v9.0.36`, commit
  `eba87f5b1f76960b6f704588370d5bd708065214`.
- Both upstream codebases use Apache License 2.0.

The checked-in phone engine and metadata are compiled directly into the CountryCodeKit module;
consumers do not resolve a separate phone-number artifact.
