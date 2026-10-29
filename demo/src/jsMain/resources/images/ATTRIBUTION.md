# Image assets

Every file in this folder is an unmodified copy of an asset from the
[Vaadin documentation repository](https://github.com/vaadin/docs), tag `25.3.4`
(commit `0c5a87b91c12c2a05f113c9cefac1dd117a16033`). They are used by the component demos so that
the demo shows the same pictures as the official examples.

The Vaadin documentation repository is licensed under
[Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)](https://creativecommons.org/licenses/by-nd/4.0/).
These files stay under that license and are **not** covered by this repository's own license.
Attribution: © Vaadin Ltd., source as listed below. The images are redistributed verbatim — the
portraits were only decoded from the base64 payloads of `peopleImages.json`, without re-encoding or
any other change to the image data.

| File(s) | Source in `vaadin/docs` | Used by |
| --- | --- | --- |
| `vaadin-logo-dark.png` | `src/main/resources/images/vaadin-logo-dark.png` | Button page, "Buttons with images" |
| `company-logo.png` | `src/main/resources/images/company-logo.png` | Avatar page, "Image" |
| `lapland.avif` | `src/main/resources/images/lapland.avif` | Card page (media, cover media, stretch media, combined variants, playground) |
| `reindeer.jpg` | `src/main/resources/images/reindeer.jpg` | Scroller page, "Scrolling in both directions"; Message List page, image attachment |
| `people/person-01.jpg` … `person-20.jpg` | base64 entries of `src/main/resources/data/peopleImages.json` | Avatar, Grid, List Box, Combo Box, Message List and Virtual List person pictures |

The Vaadin and Lumo names and logos are trademarks of Vaadin Ltd.
