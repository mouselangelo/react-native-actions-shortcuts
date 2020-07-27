#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(RNShortcuts, RCTEventEmitter)

RCT_EXTERN_METHOD(
    getShortcuts:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(
    setShortcuts:(NSArray *)shortcutItems
    resolve:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)
)

RCT_EXTERN_METHOD(clearShortcuts)

RCT_EXTERN_METHOD(
    getInitialShortcut:(RCTPromiseResolveBlock)resolve
    reject:(RCTPromiseRejectBlock)reject
)

@end

