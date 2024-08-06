import { useState, useEffect } from "react";
import Typography from "@mui/material/Typography";

type ErrorMessageProps = {
  show?: boolean;
  message: string;
};
const ErrorMessage = (props: ErrorMessageProps) => {
  const { show, message } = props;
  const [showError, setShowError] = useState(show);

  useEffect(() => {
    if (show) {
      setShowError(true);

      const timer = setTimeout(() => {
        setShowError(false);
      }, 3000);

      return () => clearTimeout(timer);
    } else {
      setShowError(false);
    }
  }, [show]);

  return (
    <>
      {showError && (
        <Typography
          sx={{
            color: "var(--error, #FF878C)",
            fontSize: "12px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "20px",
          }}
        >
          {" "}
          {message}
        </Typography>
      )}
    </>
  );
};

export { ErrorMessage };
