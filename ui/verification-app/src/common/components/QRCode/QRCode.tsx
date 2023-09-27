import React, { useEffect, useRef, useMemo } from "react";
import QRCodeStyling, { Options } from "qr-code-styling";

export interface QRCodeProps {
  /** Data to display as QR code */
  data: string;
  /** Styling options. Merged with default options */
  options?: Options;
}

const defaultOptions: Options = {
  width: 200,
  height: 200,
  cornersDotOptions: {
    type: "square",
  },
};

export const QRCode = ({ data, options }: QRCodeProps): React.ReactElement => {
  const qrCode = useMemo(() => new QRCodeStyling(defaultOptions), []);
  const ref = useRef(null);
  useEffect(() => {
    qrCode.append(ref.current);
  }, [qrCode]);

  useEffect(() => {
    qrCode.update({ ...defaultOptions, ...options, data });
  }, [data, options, qrCode]);

  return (
    <div
      data-testid="qr-code"
      style={{ overflow: "hidden", display: "flex" }}
      ref={ref}
    />
  );
};
