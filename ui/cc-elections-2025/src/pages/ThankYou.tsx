import { useNavigate } from "react-router-dom";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import {TopNav} from "../components/TopNav.tsx";
import {Layout} from "../components/Layout/Layout.tsx";

import { IMAGES } from "@consts";
import { Button } from "@atoms";
import { Footer } from "@organisms";


export const ThankYou = () => {

  const navigate = useNavigate();

  const handleButtonClick = () => {
    navigate('/');
  }

  return (
    <Box sx={{ backgroundColor: '#f2f4f8', minHeight: '100vh' }}>
      <TopNav title="Apply as a candidate" navigateBack={false} />
      <Layout>
        <Box>
          <Box sx={{ display: 'flex', gap: '24px', marginTop: '56px', justifyContent: 'center' }}>
            <Box sx={{
              display: 'flex',
              flexDirection: 'column',
              gap: '16px',
              width: '520px',
              borderRadius: '16px',
              padding: '32px 40px',
              alignItems: 'center',
              backgroundColor: 'white',
            }}>
              <Typography variant="h1">Thank You!</Typography>
              <img src={IMAGES.thankyou} alt="thank you" />
              <Typography variant="body1" sx={{ color: '#506288' }}>Your subbmision has been received!</Typography>
              <Button
                variant="text"
                onClick={handleButtonClick}
              >
                Back to main page
              </Button>
            </Box>
          </Box>
        </Box>
      </Layout>
      <Footer />
    </Box>
  );
}
