import { NativeModules, EventSubscriptionVendor } from 'react-native';

export interface ShortcutItem {
  /**
   * Unique string used to identify the type of the action
   */
  type: string;

  /**
   * On Android - it's recommended to keep this under 25 characters. If there
   * isn't enough space to display this, fallsback to `shortTitle`
   */
  title: string;

  /**
   * Android only, max 10 characters recommended. This is displayed instead of
   * `title` when there is not enough space to display the title.
   */
  shortTitle?: string;

  /**
   * iOS only, ignored on Android
   */
  subtitle?: string; // only used on iOS

  /**
   * The name of the iOS Asset or Android drawable
   */
  iconName?: string;

  /**
   * The name of the iOS SF Symbol Name
   */
  symbolName?: string;

  /**
   * Custom payload for the action
   */
  data?: any;
}

interface ShortcutsType extends EventSubscriptionVendor {
  /**
   * Set the shortcut items.
   * @returns a promise with the items that were set
   */
  setShortcuts(items: ShortcutItem[]): Promise<ShortcutItem[]>;

  /**
   * @returns a promise with the items that were set
   */
  getShortcuts(): Promise<ShortcutItem[]>;

  /**
   * Removes all the shortcut items
   */
  clearShortcuts(): void;

  /**
   * Gets the initial shortcut the app was launched with
   */
  getInitialShortcut(): Promise<ShortcutItem | null>;
}

const { RNShortcuts } = NativeModules;

export default RNShortcuts as ShortcutsType;
