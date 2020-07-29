import * as React from 'react';
import { useEffect, useState, useCallback } from 'react';
import {
  Button,
  FlatList,
  NativeEventEmitter,
  StyleSheet,
  Text,
  View,
  Platform,
} from 'react-native';
import Shortcuts, { ShortcutItem } from 'react-native-actions-shortcuts';

export default function App() {
  const [initialShortcut, setInitialShortcut] = useState<ShortcutItem | null>();

  const [lastPressedShortcut, setLastPressedShortcut] = useState<
    ShortcutItem | undefined
  >();

  const [shortcutItems, setShortcutItems] = useState<
    ShortcutItem[] | undefined
  >();

  const ShortcutsEmitter = new NativeEventEmitter(Shortcuts);

  useEffect(() => {
    const getInitialShortcut = async () => {
      const shortcutItem = await Shortcuts.getInitialShortcut();
      setInitialShortcut(shortcutItem);
    };
    getInitialShortcut();
  }, [setInitialShortcut]);

  useEffect(() => {
    const listener = (item: ShortcutItem) => {
      setLastPressedShortcut(item);
    };

    ShortcutsEmitter.addListener('onShortcutItemPressed', listener);

    return () => {
      ShortcutsEmitter.removeListener('onShortcutItemPressed', listener);
    };
  }, [ShortcutsEmitter]);

  const setShortcuts = useCallback(async () => {
    const shortcuts = await Shortcuts.setShortcuts([
      {
        type: 'song',
        title:
          Platform.OS === 'android' ? 'Play "Imagine by John Lennon"' : 'Play',
        shortTitle: 'Play "Imagine"',
        subtitle: 'Imagine by John Lennon',
        iconName: 'ic_music',
        data: {
          id: '1234',
        },
      },
    ]);

    setShortcutItems(shortcuts);
  }, [setShortcutItems]);

  const getShortcuts = useCallback(async () => {
    const shortcuts = await Shortcuts.getShortcuts();
    setShortcutItems(shortcuts);
  }, [setShortcutItems]);

  const clear = useCallback(async () => {
    Shortcuts.clearShortcuts();
    setShortcutItems(undefined);
    setLastPressedShortcut(undefined);
  }, [setShortcutItems, setLastPressedShortcut]);

  return (
    <View style={styles.container}>
      {initialShortcut && (
        <React.Fragment>
          <Text style={styles.caption}>Initial shortcut item: </Text>
          <Text style={styles.info}>
            {initialShortcut?.type} : {initialShortcut?.title},{' '}
            {initialShortcut.data?.id}
          </Text>
        </React.Fragment>
      )}

      {lastPressedShortcut && (
        <React.Fragment>
          <Text style={styles.caption}>Last pressed shortcut item: </Text>
          <Text style={styles.info}>
            {lastPressedShortcut?.type}: {lastPressedShortcut?.title} ,{' '}
            {lastPressedShortcut.data?.id}
          </Text>
        </React.Fragment>
      )}

      <Text style={styles.caption}>Current shortcuts: </Text>
      {shortcutItems?.length ? (
        <FlatList
          style={styles.list}
          data={shortcutItems}
          keyExtractor={(_, index) => `${index}`}
          renderItem={({ item }) => (
            <Text style={styles.info}>
              {item.type} : {item.title}
            </Text>
          )}
        />
      ) : (
        <Text style={styles.info}>None</Text>
      )}

      <View style={styles.actions}>
        <Button title="Set shortcuts" onPress={() => setShortcuts()} />
        <Button title="Get shortcuts" onPress={() => getShortcuts()} />
        <Button title="Clear" onPress={() => clear()} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: 44,
    margin: 16,
  },
  caption: {
    marginVertical: 8,
  },
  info: {
    width: '100%',
    backgroundColor: '#f5f5f5',
    alignItems: 'center',
    padding: 8,
  },
  list: {
    flex: 1,
    width: '100%',
  },
  actions: {
    position: 'absolute',
    bottom: 0,
    flexDirection: 'row',
    width: '100%',
    justifyContent: 'space-between',
  },
});
