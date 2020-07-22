import UIKit

typealias ShortcutItem = [String: Any];

protocol ShortcutsDelegate: class {
    func onShortcutItemPressed(_ item: ShortcutItem)
}

@objc
class Shortcuts: NSObject {
    @objc static let shared = Shortcuts();

    weak var delegate: ShortcutsDelegate? {
        didSet {
            processUnhandledShortcutItem()
        }
    }

    private var unhandledShortcutItem: UIApplicationShortcutItem?

    func getShortcuts(callback: (([ShortcutItem]?) -> Void)?) {
        DispatchQueue.main.async {
            let items = UIApplication.shared.shortcutItems?.map({ $0.asDictionary })
            callback?(items)
        }
    }

    func setShortcuts(_ items: [ShortcutItem]) throws -> [ShortcutItem] {
        let shortuctItems = items.compactMap({ try! UIApplicationShortcutItem.from($0) })

        DispatchQueue.main.async {
            UIApplication.shared.shortcutItems = shortuctItems
        }
        
        return shortuctItems.map({ $0.asDictionary })
    }

    func clearShortcuts() {
        DispatchQueue.main.async {
            UIApplication.shared.shortcutItems = nil
        }
    }

    private func processUnhandledShortcutItem() {
        guard
            let unhandledShortcutItem = self.unhandledShortcutItem ,
            let delegate = self.delegate
        else {
            return
        }

        delegate.onShortcutItemPressed(unhandledShortcutItem.asDictionary)
        self.unhandledShortcutItem = nil
    }

    func performAction(forShortcutItem item: UIApplicationShortcutItem) {
        guard let delegate = self.delegate else {
            unhandledShortcutItem = item
            return
        }

        delegate.onShortcutItemPressed(item.asDictionary)
    }
}

enum ShortcutsError: Error {
    case invalidShortcutItem
}


fileprivate extension UIApplicationShortcutItem {
    var asDictionary: [String: Any] {
        return [
            "type": type,
            "title": localizedTitle,
            "subtitle": localizedSubtitle as Any,
            "data": userInfo as Any
        ]
    }

    static func from(_ value: [String: Any]) throws -> UIApplicationShortcutItem? {
        guard
            let type = value["type"] as? String,
            let title = value["title"] as? String
            else {
                throw ShortcutsError.invalidShortcutItem
        }

        let subtitle = value["subtitle"] as? String
        let icon = UIApplicationShortcutIcon.from(value["iconName"] as? String)
        let userInfo = value["data"] as? [String: NSSecureCoding]

        return UIApplicationShortcutItem(
            type: type,
            localizedTitle: title,
            localizedSubtitle: subtitle,
            icon: icon,
            userInfo: userInfo
        )
    }
}

fileprivate extension UIApplicationShortcutIcon {
    static func from(_ imageName: String?) -> UIApplicationShortcutIcon? {
        guard let imageName = imageName else {
            return nil
        }
        return UIApplicationShortcutIcon(templateImageName: imageName)
    }
}
