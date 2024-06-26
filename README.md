# react-native-actions-shortcuts

iOS [Home screen Quick Actions](https://developer.apple.com/design/human-interface-guidelines/ios/system-capabilities/home-screen-actions/) & Android [App Shortcuts](https://developer.android.com/guide/topics/ui/shortcuts/creating-shortcuts) for react-native

## Installation

```bash
npm install react-native-actions-shortcuts
```

## Setup

### iOS

If you are using cocoapods - you may need to run `pod install` (from `ios` directory).

On iOS, Quick Actions are handled by the
[`application:performActionForShortcutItem:completionHandler`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1622935-application?language=objc)
method of your app's [`UIApplicationDelegate`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate) (i.e. `AppDelegate.m`),
so, you will therefore need to add the following code in your
project's [`AppDelegate.m`](./example/ios/ShortcutsExample/AppDelegate.m)) file.

```objective-c
- (void)application:(UIApplication *)application performActionForShortcutItem:(UIApplicationShortcutItem *)shortcutItem completionHandler:(void (^)(BOOL))completionHandler {
    [RNShortcuts performActionForShortcutItem:shortcutItem completionHandler:completionHandler];
}
```

### Android

Android doesn't require any additional setup.

## Usage

### Example project

See the [Example project](./example/README.md) for a working demo.

### Documentation

#### Imports

```js
// ...
import { NativeEventEmitter } from "react-native";
import Shortcuts from "react-native-actions-shortcuts";

// if using typescript, can also use the 'ShortcutItem' type
import Shortcuts, { ShortcutItem } from "react-native-actions-shortcuts";

// ...
```

#### Initial shortcut / action

Get the initial shortcut that the app was launched with. On iOS this will be returned just once, subsequent calls will return `null`.

```js
const shortcutItem = await Shortcuts.getInitialShortcut();
```

#### Listen for shortcut / action invocations

Listen to shortcut / action invocations while app is running.

On iOS the listener is also called for the initial
invocation, unless it was already received via `Shortcuts.getInitialShortcut()`.

```js
const ShortcutsEmitter = new NativeEventEmitter(Shortcuts);

// 1. define the listener
function handleShortcut(item) {
    const {type, data} = item;
    // your handling logic
};

// 2. add the listener in a `useEffect` hook or `componentDidMount`
ShortcutsEmitter.addListener("onShortcutItemPressed", handleShortcut);

// 3. remove the listener in a `useEffect` hook or `componentWillUnmount`
ShortcutsEmitter.removeListener("onShortcutItemPressed", handleShortcut);
```

#### Set shortcuts

To set shortcuts (will replace existing dynamic actions / shortcuts)

```js
const shortcutItem = {
    type: "my.awesome.action",
    title: "Do awesome things",
    shortTitle: "Do it",
    subtitle: "iOS only",
    iconName: "ic_awesome",
    symbolName: "house.fill", // SF Symbol Name (iOS only)
    data: {
        "foo": "bar",
    },
};

Shortcuts.setShortcuts([shortcutItem]);

// you can also `await` to get the current dynamic shortcuts / actions
const shortcutItems = await Shortcuts.setShortcuts([shortcutItem]);
```

#### Clear shortcuts

Clears all dynamic shortcuts.

```js
Shortcuts.clearShortcuts();
```

#### Get shortcuts

Get the current shortcuts. Some information may be lost, such as iconName, data,
etc.

```js
const shortcutItems = await Shortcuts.getShortcuts();
```

## Icons

To display icons with your shortcuts / actions you will need to add them to your
project. Once added use the name of your iOS asset or Android drawable as the
value for `iconName` above. You can also use SF Symbol Name like "house.fill"
or `globe.europe.africa` for `symbolName` above (iOS only). If `symbolName` is
filled, `iconName` is not taken into account.

### iOS - Asset catalog

Add new assets to your [Asset catalog](https://developer.apple.com/library/archive/documentation/ToolsLanguages/Conceptual/Xcode_Overview/AddingImages.html) by importing either `png` (scalar) or
`pdf` (vector) files.

Refer
[Custom Icons : Home Screen Quick Action Icon
Size](https://developer.apple.com/design/human-interface-guidelines/home-screen-quick-actions)
to learn about the dimensions and design specifications.

### Android - drawable

Add [drawable resources](https://developer.android.com/studio/write/resource-manager) to you Android project. In Android studio, choose:

- for vector icons (SVG / PDF): __File > New > Vector Asset__

- for scalar icons (PNG): __File > New > Image Asset__

Refer
[App Shortcuts: Icon design
guidelines](https://commondatastorage.googleapis.com/androiddevelopers/shareables/design/app-shortcuts-design-guidelines.pdf)
to learn about the dimensions and design specifications.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

## Attribution

Icons made by [Gregor Cresnar](https://www.flaticon.com/authors/gregor-cresnar)
from [www.flaticon.com](https://www.flaticon.com/)