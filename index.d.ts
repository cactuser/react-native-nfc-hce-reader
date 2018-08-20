export function supportNFC(): { support: boolean; enabled: boolean };
export function listenNFCStatus(): (
  callback: (enabled: boolean) => void
) => void;
export function listenDataReceived(): (
  callback: (data: string) => void
) => void;
