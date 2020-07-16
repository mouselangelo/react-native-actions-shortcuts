import { NativeModules } from 'react-native';

type ShortcutsType = {
  multiply(a: number, b: number): Promise<number>;
};

const { Shortcuts } = NativeModules;

export default Shortcuts as ShortcutsType;
