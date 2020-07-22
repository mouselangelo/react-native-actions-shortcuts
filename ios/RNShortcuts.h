#import <React/RCTBridgeModule.h>

@interface RNShortcuts : NSObject <RCTBridgeModule>
+(void) performActionForShortcutItem:(UIApplicationShortcutItem *) shortcutItem completionHandler:(void (^)(BOOL succeeded)) completionHandler;
@end
