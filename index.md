![EGradle](https://github.com/de-jcup/egradle/blob/master/egradle-plugin-main/html/images/egradle-banner_128x128.png?raw=true)

<a href="http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=3071167" class="drag" title="Drag to your running Eclipse workspace to install EGradle"><img class="img-responsive" src="https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.png" alt="Drag to your running Eclipse workspace to install EGradle" /></a>
# About
EGradle is a lightweight gradle integration for eclipse

## Why EGradle?
There are other gradle integrations for Eclipse (Gradle IDE, Buildship, ...). So why just another one?
EGradle does it in another way than other integrations:
- Console world and Eclipse world are same.
No special imports necessary, no additional classpath behaviours, eclipse natures etc. Just plain gradle usage. Your are always able to call gradle with "cleanEclipse eclipse" and import the generated eclipse project descriptons
- EGradle ist more or less a simple UI wrapper for gradle with some extra functions.
- It is a bridge between eclipse and console but the main logic is still inside gradle
- No trying to parse/analyze gradle scripts. If you want to know which task you can execute please call "tasks" by quick launch
- Reusing existing views, concepts and integrations - e.g. Junit test results are shown in Junit View
- Uses KISS pattern (keep it simple stupid) in conjuction with being fast and comfortable

## What are the features of EGradle?
Please look into [Features overview](https://github.com/de-jcup/egradle/wiki/Features#overview-of-egradle-features).
More information can be found inside [Wiki](https://github.com/de-jcup/egradle/wiki).

## License
EGradle is under Apache 2.0 license (http://www.apache.org/licenses/LICENSE-2.0)
