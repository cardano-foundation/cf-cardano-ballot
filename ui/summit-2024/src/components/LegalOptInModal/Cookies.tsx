import Modal from "../common/Modal/Modal";
import { useLocalStorage } from "../../common/hooks/useLocalStorage";
import { CB_COOKIES } from "../../common/constants/local";
import { Box, Typography } from "@mui/material";
import theme from "../../common/styles/theme";
import { CustomButton } from "../common/CustomButton/CustomButton";

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
        closeIcon={false}
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
          component="div"
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
              textAlign: {
                xs: "left",
                sm: "center",
              },
            }}
          >
            We use cookies on this website to help improve your overall
            experience. By clicking accept you agree to our privacy policy.
          </Typography>
          <Box
            component="div"
            sx={{
              display: "flex",
              flexDirection: { xs: "column", sm: "row" },
              width: "100%",
              alignItems: "center",
              gap: 2,
              marginTop: "24px",
            }}
          >
            <CustomButton
              onClick={handlePrivacyPolicy}
              fullWidth={true}
              colorVariant="secondary"
            >
              Privacy Policy
            </CustomButton>
            <CustomButton
              onClick={handleReject}
              fullWidth={true}
              colorVariant="secondary"
            >
              Reject
            </CustomButton>
            <CustomButton
              onClick={() => handleAccept()}
              fullWidth={true}
              colorVariant="primary"
            >
              Accept
            </CustomButton>
          </Box>
        </Box>
      </Modal>
    </>
  );
};

export { Cookies };
