# Privacy Policy for Pixel Meter

**Effective Date:** 2026-08-26

Pixel Meter ("we," "our," or "us") is committed to protecting your privacy. This policy explains how the app processes information and uses Android permissions.

## 1. Information Collection and Processing

**Pixel Meter does not collect, store, sell or transmit personal data, device identifiers, network traffic contents, browsing history or app usage data.**

The app reads local byte counters exposed by Android for active physical network interfaces. These counters are used only to calculate the current upload and download speed on your device. Pixel Meter does not inspect packet contents, DNS requests, websites or communications.

Settings are stored locally with Android Preferences DataStore.

## 2. Permissions

Pixel Meter may declare or request the following permissions:

- **`ACCESS_NETWORK_STATE`**: Identifies active network transports and physical interface names, and excludes VPN transports from speed calculation.
- **`INTERNET`**: Supports links opened by the user, including the Cloudflare speed test. Pixel Meter does not use it for analytics or background reporting.
- **`FOREGROUND_SERVICE`**: Runs the user-enabled real-time monitoring service.
- **`FOREGROUND_SERVICE_SPECIAL_USE`**: Declares the network-monitor Foreground Service subtype used on Android 14 and later.
- **`FOREGROUND_SERVICE_DATA_SYNC`**: Provides the compatible Foreground Service type used on earlier supported Android versions.
- **`POST_NOTIFICATIONS`**: Required on Android 13 and later to show the monitoring notification.
- **`POST_PROMOTED_NOTIFICATIONS`**: Supports the optional Android 16+ Live Update presentation when enabled by the user and supported by the system.
- **`SYSTEM_ALERT_WINDOW`**: Requested only when the user enables the floating window, allowing network speed to be displayed over other apps.
- **`RECEIVE_BOOT_COMPLETED`**: Starts monitoring after device boot only when the user has explicitly enabled automatic start.
- **`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`**: Opens the system battery optimization exemption request when the user selects that setting. The user controls whether the exemption is granted.

Android requires an ongoing notification while the Foreground Service is running. If dynamic notification speed is disabled, a minimal service notification remains visible.

## 3. Third-Party Pages

Pixel Meter does not include third-party analytics, advertising SDKs or tracking libraries.

User actions may open external pages with Chrome Custom Tabs or another browser, including:

- `speed.cloudflare.com`
- GitHub
- Pixel Tailor
- Telegram
- `dontkillmyapp.com`

Those pages are governed by their own privacy policies and the privacy settings of the selected browser. Pixel Meter does not receive browsing data from them.

## 4. Data Sharing and Retention

Pixel Meter does not operate a server for application data and does not share user data with third parties. Local settings remain on the device until the app data is cleared or the app is uninstalled.

## 5. Children’s Privacy

Pixel Meter is not directed to children under 13 and does not knowingly collect personal information from children.

## 6. Policy Changes

This policy may be updated when app functionality, permissions or legal requirements change. The latest version will be published with the project and relevant distribution pages.

## 7. Contact

Questions or suggestions can be submitted through the [Pixel Meter GitHub repository](https://github.com/Pixel-Tailor-CN/PixelMeter/issues).
