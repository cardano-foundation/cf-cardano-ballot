import Modal from "../common/Modal/Modal";
import { useLocalStorage } from "../../common/hooks/useLocalStorage";
import { CB_COOKIES } from "../../common/constants/local";
import { Box, Button, Typography } from "@mui/material";

export enum CookiesStatus {
  ACCEPT = "ACCEPT",
  REJECT = "REJECT",
}

const Cookies = () => {
  const [cookies, setCookies] = useLocalStorage(CB_COOKIES, undefined);

  const handleAccept = () => {
    setCookies(CookiesStatus.ACCEPT);
  };

  const handleReject = () => {
    setCookies(true);
  };

  const handlePrivacyPolicy = () => {
    // TODO: open privacy and policy page
  };

  return (
    <>
      <Modal
        id="connect-wallet-modal"
        isOpen={!cookies}
        name="connect-wallet-modal"
        title="Cookie Policy"
        width={"620px"}
        onClose={() => setCookies(CookiesStatus.REJECT)}
        onBack={() => setCookies(CookiesStatus.REJECT)}
        disableBackdropClick
      >
        <Box
          sx={{
            padding: 2,
            borderRadius: 1,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            gap: 2,
          }}
        >
          <Typography
            sx={{
              color: "var(--neutralLight, #D2D2D9)",
              fontSize: "16px",
              fontWeight: 500,
              lineHeight: "24px",
              textAlign: "center",
            }}
          >
            We use cookies on this website to help improve your overall
            experience. By clicking accept you agree to our privacy policy.
          </Typography>
          <Box sx={{ display: "flex", gap: 1 }}>
            <Button
              onClick={handlePrivacyPolicy}
              sx={{
                display: "flex",
                width: "180px",
                padding: "16px 24px",
                justifyContent: "center",
                alignItems: "center",
                borderRadius: "12px",
                border: "1px solid var(--orange, #EE9766)",
                color: "var(--orange, #EE9766)",
                fontSize: "16px",
                fontWeight: 500,
                lineHeight: "24px",
                textTransform: "none",
              }}
            >
              Privacy Policy
            </Button>
            <Button
              onClick={handleReject}
              sx={{
                display: "flex",
                width: "180px",
                padding: "16px 24px",
                justifyContent: "center",
                alignItems: "center",
                borderRadius: "12px",
                border: "1px solid var(--orange, #EE9766)",
                color: "var(--orange, #EE9766)",
                fontSize: "16px",
                fontWeight: 500,
                lineHeight: "24px",
                textTransform: "none",
              }}
            >
              Reject
            </Button>
            <Button
              onClick={() => handleAccept()}
              sx={{
                display: "flex",
                width: "180px",
                padding: "16px 24px",
                justifyContent: "center",
                alignItems: "center",
                borderRadius: "12px",
                background:
                  "linear-gradient(70deg, #0C7BC5 -105.24%, #40407D -53.72%, #EE9766 -0.86%, #EE9766 103.82%)",
                color: "var(--neutralDarkest, #121212)",
                fontSize: "16px",
                fontWeight: 500,
                lineHeight: "24px",
                textTransform: "none",
              }}
            >
              Accept
            </Button>
          </Box>
        </Box>
      </Modal>
    </>
  );
};

export { Cookies };
