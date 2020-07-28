# react-native-shortcuts

iOS Home screen Quick Actions &amp; Android App Shortcuts for react-native

## Installation

This package is not yet published to the npm public repository, so currently it
has to be installed directly using the GitHub URL:

```bash
npm install git+https://github.com/mouselangelo/react-native-shortcuts.git
```

## Setup

### iOS

If you are using cocoapods - you may need to run `pod install` (from `ios` directory).

On iOS, Quick Actions are handled by your app's `UIApplicationDelegate`
(`AppDelegate.m`) - you will therefore need to add the following code in your
project's `` file.

```objective-c
- (void)application:(UIApplication *)application performActionForShortcutItem:(UIApplicationShortcutItem *)shortcutItem completionHandler:(void (^)(BOOL))completionHandler {
    [RNShortcuts performActionForShortcutItem:shortcutItem completionHandler:completionHandler];
}
```

### Android

Android doesn't require any additional setup.

## Usage

Imports

```js
// ...
import { NativeEventEmitter } from "react-native";
import Shortcuts from "react-native-shortcuts";

// if using typescript, can also use the 'ShortcutItem' type
import Shortcuts, { ShortcutItem } from "react-native-shortcuts";

// ...
```

Get the initial shortcut that the app was launched with.
On iOS this will be returned just once, subsequent calls will return `null`.

```js
const shortcutItem = await Shortcuts.getInitialShortcut();
```

Listen to action invocations while app is running.

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

To set shortcuts (will replace existing dynamic actions / shortcuts)

```js
const shortcutItem = {
    type: "my.awesome.action",
    title: "Do awesome things",
    shortTitle: "Do it",
    subtitle: "iOS only",
    iconName: "ic_awesome",
    data: {
        "foo": "bar",
    },
};

Shortcuts.setShortcuts([shortcutItem]);

// you can also `await` to get the current dynamic shortcuts / actions
const shortcutItems = await Shortcuts.setShortcuts([shortcutItem]);
```

To clear shortcuts

```js
Shortcuts.clearShortcuts();
```

Get the current shortcuts.

Some information may be lost, such as iconName, data

```js
const shortcutItems = await Shortcuts.getShortcuts();
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

## Attribution

Icons made by [Gregor Cresnar](https://www.flaticon.com/authors/gregor-cresnar)
from [www.flaticon.com](https://www.flaticon.com/)