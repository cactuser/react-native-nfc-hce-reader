export function supportNFC(): { support: boolean; enabled: boolean };
export function startListenNFCStatus(): (
  callback: (status: object) => void
) => void;
export function stopListenNFCStatus(): (
  callback: (status: object) => void
) => void;
export function startListenDataReceived(): (
  callback: (data: object) => void
) => void;
export function stopListenDataReceived(): (
  callback: (data: object) => void
) => void;
