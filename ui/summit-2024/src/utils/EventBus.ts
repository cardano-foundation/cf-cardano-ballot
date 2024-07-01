import { EventEmitter } from "events";

enum EventName {
  ShowToast = "showToast",
  OpenConnectWalletModal = "openConnectWalletModal",
  CloseConnectWalletModal = "closeConnectWalletModal",
  OpenVerifyWalletModal = "openVerifyWalletModal",
}

class EventBus {
  eventEmitter: EventEmitter;

  constructor() {
    this.eventEmitter = new EventEmitter();
  }

  subscribe(eventName: string, listener: (...args: any[]) => void) {
    this.eventEmitter.on(eventName, listener);
  }

  unsubscribe(eventName: string, listener: (...args: any[]) => void) {
    this.eventEmitter.off(eventName, listener);
  }

  publish(eventName: string, ...args: any[]) {
    this.eventEmitter.emit(eventName, ...args);
  }
}

export const eventBus = new EventBus();

export { EventName };
