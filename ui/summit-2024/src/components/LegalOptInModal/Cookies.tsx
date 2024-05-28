import Modal from "../common/Modal/Modal";
import { useLocalStorage } from "../../common/hooks/useLocalStorage";
import { CB_COOKIES } from "../../common/constants/local";
import { Box, Button, Typography } from "@mui/material";
import theme from "../../common/styles/theme";

export enum CookiesStatus {
  ACCEPT = "ACCEPT",
  REJECT = "REJECT",
}

const Cookies = ({ position }) => {
  const [cookies, setCookies] = useLocalStorage(CB_COOKIES, undefined);

  const handleAccept = () => {
    setCookies(CookiesStatus.ACCEPT);
  };

  const handleReject = () => {
    setCookies(true);
  };

  const handlePrivacyPolicy = () => {
    // TODO: Open privacy and policy page
  };

  return (
    <>
      <Modal
        id="connect-wallet-modal"
        isOpen={!cookies}
        name="connect-wallet-modal"
        title="Cookie Policy"
        width="auto"
        onClose={() => setCookies(CookiesStatus.REJECT)}
        onBack={() => setCookies(CookiesStatus.REJECT)}
        disableBackdropClick
        position={position}
        sx={{
          width: {
            tablet: "95vw",
          },
          maxWidth: "620px",
          position: "absolute",
          bottom: 0,
          left: "50%",
          transform: "translateX(-50%)",
        }}
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
              color: theme.palette.text.neutralLight,
              fontSize: "16px",
              fontWeight: 500,
              lineHeight: "24px",
              textAlign: "left",
            }}
          >
            We use cookies on this website to help improve your overall
            experience. By clicking accept you agree to our privacy policy.
          </Typography>
          <Box sx={{ display: "flex" }}>
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
                marginRight: "12px",
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
                marginRight: "12px",
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
