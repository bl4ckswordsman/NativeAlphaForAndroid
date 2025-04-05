# <img src="graphics/logo.png" width="50px" alt=""></img> Native Alpha
![OS](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white&style=plastic)
![OS](https://img.shields.io/badge/MinVersion-8.0-red)
![SDK](https://img.shields.io/badge/SDK-35-yellowgreen)
[![GitHub release](https://img.shields.io/github/v/release/cylonid/NativeAlphaForAndroid?include_prereleases&color=blueviolet)](https://github.com/cylonid/NativeAlphaForAndroid/releases)
[![Github all releases](https://img.shields.io/github/downloads/cylonid/NativeAlphaForAndroid/total?color=blue&label=GitHub%E2%87%A9&style=plastic)](https://somsubhra.github.io/github-release-stats/?username=cylonid&repository=NativeAlphaForAndroid&page=1&per_page=20)
[![GitHub license](https://img.shields.io/github/license/cylonid/NativeAlphaForAndroid?color=orange)](https://github.com/cylonid/NativeAlphaForAndroid/blob/master/LICENSE)
![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)

> [!NOTE]
> This fork of the [original NativeAlpha](https://github.com/cylonid/NativeAlphaForAndroid) lacks paid features (available in the Play Store version — consider supporting the original developer) and AdBlock functionality (library deprecated, needed to build/test).
> Key differences from the original include:
> - Support for Android 15 edge-to-edge enforcement
> - Updated dependencies and minor code refactoring
> - Improved icon fetching (including SVG support)
> - Shortcut-specific icons shown in the recent apps view
> 
> I may upstream some changes if time permits and the original dev is interested.


## Features
  * Shows any website in a borderless full-screen window using Android System WebView.
  * Create home screen shortcuts and retrieves icons in suitable resolution.
  * Various settings (JavaScript, cookies, adblocking, location/camera/microphone access) can be set for every web app individually
  * Navigation with multi-touch gestures while browsing.
  * Opt-in adblock using an AdBlock Plus custom webview.
  * Less memory footprint and no privacy-invading app permissions in comparison to native apps
  * Dark mode for Android 10+

## Download Options
[![IzzyOnDroid Download Badge](graphics/IzzyOnDroid.png)](https://apt.izzysoft.de/fdroid/index/apk/com.cylonid.nativealpha)
[![APK Download Badge](graphics/apk_badge.png)](https://github.com/cylonid/NativeAlphaForAndroid/releases/download/v1.4.0/NativeAlpha-standard-universal-release-v1.4.0.apk)
[![Google Play Download Badge](graphics/google_play.png)](https://play.google.com/store/apps/details?id=com.cylonid.nativealpha)

## Latest Changes (v1.4.0)

* Order of Web Apps in menu can be changed
* File downloads are supported
* Native Alpha custom context menu can be deactived so that the standard browser context menu is shown
* Italian translation added
* General, technical version updates

## FAQ
<details> 
<summary><i> Q: Why would I need this app if any mobile browser can do the same? </i></summary>
A: Mobile browsers usually only are able to create shortcuts which give a native, borderless fullscreen experience if the website has a Progressive Web App (PWA) manifest. Unfortunately, most websites do not offer this feature yet. Additionally, you cannot set different settings for different websites with an usual browser.
</details>

<details> 
<summary><i> Q: Can I keep multiple log-in sessions of the same website? </i></summary>
A: Yes, this is possible using the sandbox feature of Native Alpha Plus.
</details>

<details> 
<summary><i> Q: Why isn't the sandbox feature in Native Alpha Plus enabled by default? </i></summary>
A: The sandboxing approach is recommended for specific usage rather than general usage because it can limit the performance of the application and increase the disk usage. Therefore, use it for privacy-invasive websites or websites where you want to be logged in twice, but not for any website just because you can.
</details>

<details> 
<summary><i> Q: Is this app a dedicated web browser with its own browser engine? </i></summary>
A: No. As stated, this app relies on the system built-in Android WebView in order to display the website. For privacy reasons, you can opt to use alternative webviews such as [Bromite](https://www.bromite.org/system_web_view) on rooted phones. Always make sure to use to most recent version of any WebView implementation you use!
</details>

<details>
<summary><i> Q: Why is it not possible to find an icon for a certain website? </i></summary>
A: This problem can occur due to multiple reasons. In most cases, the website does not offer a high-resolution icon. If you are a website maintainer and your website icon cannot be found, look at [RealFaviconGenerator](https://realfavicongenerator.net) for further information. If you think it should work, feel free to post the URL and I will look into it.
</details>

<details>
<summary><i> Q: In constrast to your promise, this app has a large memory footprint! </i></summary>
A: This is because Native Alpha makes use of caching in the same way your browser app does, i.e., it saves web content locally on your device. Then it can be loaded faster if you visit the same page again. You can either delete cache regularly yourself or set the "Clear cache after usage" setting in the global settings if memory footprint is a concern for you. However, then websites will take a longer time to load because everything has to be loaded from net.
</details>

<details>
<summary><i> Q: What is the minimum Android version for running Native Alpha? </i></summary>
A: Oreo (8.0). This is because older versions use a discontinued API for creating screenshots which currently is not implemented.
</details>

<details>
<summary><i> Q: I don't want to use Google Play services, is there any other way to obtain Native Alpha Plus? </i></summary>
A: You can build the app yourself, everything is open-source including the paid features.
</details>

## Used libraries/resources
* [CircularProgressBar](https://github.com/lopspower/CircularProgressBar)
* [JSoup](https://jsoup.org/)
* [AdBlock+WebView](https://github.com/adblockplus/libadblockplus-android)
* [MovableFloatingActionButton](https://stackoverflow.com/questions/46370836/android-movable-draggable-floating-action-button-fab)
* [Android About Page](https://github.com/medyo/android-about-page)
* [Android Databinding](https://developer.android.com/topic/libraries/data-binding)
* [AboutLibraries](https://github.com/mikepenz/AboutLibraries)
* [Drag & Drop n' Swipe Recyclerview](https://github.com/ernestoyaquello/DragDropSwipeRecyclerview)

For testing purposes:
* [Robolectric](https://github.com/robolectric/robolectric)
* [Espresso](https://developer.android.com/training/testing/espresso/)

A list of used open-source libraries can also be found inside the app ("About" section).

## Screenshots
<details>
<summary> Click to see screenshots </summary>
<div style="text-align: center; margin: auto;">
<img src="graphics/screenshots/mainScreen.png" alt="Main Screen" width="350"/>
<img src="graphics/screenshots/addWebApp.png" alt="Add Web App" width="350"/>
<img src="graphics/screenshots/webAppSettings.png" alt="Available Web App Settings" width="350"/>
<img src="graphics/screenshots/globalSettings.png" alt="Global Settings" width="350"/>
</div>
</details>


## License
Native Alpha is Free Software: You can use, study share and improve it at your
will. Specifically you can redistribute and/or modify it under the terms of the
[GNU General Public License](https://www.gnu.org/licenses/gpl.html) as
published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

## End User License Agreement
THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
