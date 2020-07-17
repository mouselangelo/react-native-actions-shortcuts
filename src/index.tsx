import { NativeModules } from 'react-native';

type ShortcutsType = {
  multiply(a: number, b: number): Promise<number>;
};

const { RNShortcuts } = NativeModules;

export default RNShortcuts as ShortcutsType;
