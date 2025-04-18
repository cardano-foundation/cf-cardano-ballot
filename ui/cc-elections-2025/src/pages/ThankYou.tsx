import Box from "@mui/material/Box";
import {TopNav} from "../components/TopNav.tsx";
import Typography from "@mui/material/Typography";
import {Layout} from "../components/Layout/Layout.tsx";
import { Button } from "../components/atoms";
import {useNavigate} from "react-router-dom";
import {IMAGES} from "../consts";


export const ThankYou = () => {

  const navigate = useNavigate();

  const handleButtonClick = () => {
    navigate('/');
  }

  return (
    <Layout>
      <Box>
        <TopNav />
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
            <Button onClick={handleButtonClick} sx={{ marginTop: '24px', width: '100%' }}>Go to Ballot!</Button>
          </Box>
        </Box>
      </Box>
    </Layout>
  );
}
