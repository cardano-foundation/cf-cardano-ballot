declare global {
  interface Window {
    // Support for CIP30
    cardano: {
      [key: string]: any;
    };
    // Injected env vars on runtime
    _env_?: {
      [key: string]: string;
    };
  }
}

export {};
