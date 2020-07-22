@objc(RNShortcuts)
public class RNShortcuts: RCTEventEmitter {

    let onShortcutItemPressed = "onShortcutItemPressed"

    public override func startObserving() {
        Shortcuts.shared.delegate = self
    }

    public override func stopObserving() {
        Shortcuts.shared.delegate = nil
    }

    public override func supportedEvents() -> [String]! {
        return [
            onShortcutItemPressed
        ]
    }

    public override class func requiresMainQueueSetup() -> Bool {
        return true
    }

    @objc(setShortcuts:resolve:reject:)
    public func setShortcuts(shortcutItems: [[String: Any]],
                             resolve: RCTPromiseResolveBlock,
                             reject: RCTPromiseRejectBlock) {
        do {
            let shortcutItems = try Shortcuts.shared.setShortcuts(shortcutItems)
            resolve(shortcutItems)
        } catch {
            let error = NSError(domain: "RNShortcuts", code: 1)
            reject("1", "Unable to set shortcuts", error)
        }

    }

    @objc(getShortcuts:reject:)
    public func getShortcuts(resolve: @escaping RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        Shortcuts.shared.getShortcuts { (shorcutItems) in
            resolve(shorcutItems)
        }
    }

    @objc
    public func clearShortcuts() {
        Shortcuts.shared.clearShortcuts()
    }

    @objc
    public class func performActionForShortcutItem(_ shortcutItem: UIApplicationShortcutItem,
                                                   completionHandler: (Bool) ->Void) {
        Shortcuts.shared.performAction(forShortcutItem: shortcutItem)
    }
}

extension RNShortcuts: ShortcutsDelegate {
    func onShortcutItemPressed(_ item: ShortcutItem) {
        sendEvent(withName: onShortcutItemPressed, body: item)
    }
}
