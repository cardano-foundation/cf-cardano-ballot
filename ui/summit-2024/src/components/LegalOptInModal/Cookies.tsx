import { useLocalStorage } from "../../common/hooks/useLocalStorage";
import { CB_COOKIES } from "../../common/constants/local";
import { Box, Typography } from "@mui/material";
import theme from "../../common/styles/theme";
import { CustomButton } from "../common/CustomButton/CustomButton";

export enum CookiesStatus {
  ACCEPT = "ACCEPT",
  REJECT = "REJECT",
}

const Cookies = () => {
  const [cookies, setCookies] = useLocalStorage(CB_COOKIES, undefined);
  const showCookies = cookies === undefined;

  const handleAccept = () => {
    setCookies(CookiesStatus.ACCEPT);
  };

  const handleReject = () => {
    setCookies(CookiesStatus.REJECT);
  };

  const handlePrivacyPolicy = () => {
    // TODO: Open privacy and policy page
  };

  if (!showCookies) return null;

  return (
    <Box
      component="div"
      sx={{
        position: "fixed",
        bottom: "20px",
        width: "100%",
        height: "232px",
        maxWidth: "600px",
        left: "50%",
        transform: "translateX(-50%)",
        backgroundColor: theme.palette.background.default,
        padding: "28px",
        boxShadow: "4px 4px 24px 0px rgba(115, 115, 128, 0.20)",
        zIndex: 1000,
        borderRadius: "20px",
      }}
    >
      <Typography
        sx={{
          color: theme.palette.text.neutralLight,
          fontSize: "32px",
          fontFamily: "Dosis",
          fontWeight: 700,
          lineHeight: "36px",
          textAlign: "left",
          marginBottom: "12px",
        }}
      >
        Cookies Policy
      </Typography>
      <Typography
        sx={{
          color: theme.palette.text.neutralLight,
          fontSize: "16px",
          fontWeight: 500,
          lineHeight: "24px",
          textAlign: "left",
          marginBottom: "12px",
        }}
      >
        We use cookies on this website to help improve your overall experience.
        By clicking accept you agree to our privacy policy.
      </Typography>
      <Box
        component="div"
        sx={{
          display: "flex",
          flexDirection: { xs: "column", sm: "row" },
          justifyContent: "center",
          alignItems: "center",
          gap: 2,
          width: "100%",
        }}
      >
        <CustomButton
          onClick={handlePrivacyPolicy}
          colorVariant="secondary"
          fullWidth={true}
        >
          Privacy Policy
        </CustomButton>
        <CustomButton
          onClick={handleReject}
          colorVariant="secondary"
          fullWidth={true}
        >
          Reject
        </CustomButton>
        <CustomButton
          onClick={handleAccept}
          colorVariant="primary"
          fullWidth={true}
        >
          Accept
        </CustomButton>
      </Box>
    </Box>
  );
};

export { Cookies };
