import React from 'react';
import { Footer } from './components/common/Footer/Footer';
import { BrowserRouter } from 'react-router-dom';
import './App.scss';
import BackgroundPolygon1 from './common/resources/images/polygon1.svg';
import { Box, Container, useMediaQuery, useTheme } from '@mui/material';
import Header from './components/common/Header/Header';
import { PageRouter } from './routes';

function App() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  return (
    <>
      <BrowserRouter>
        <img
          src={BackgroundPolygon1}
          alt="Background Shape"
          className="background-shape-1"
        />
        <div
          className="App"
          style={{ padding: isMobile ? '0px 0px' : '10px 52px' }}
        >
          <Header />
          <div className="main-content">
            <Container
              maxWidth="xl"
              className="big-container"
            >
              <Box my={2}>
                <PageRouter />
              </Box>
            </Container>
          </div>

          <Footer />
        </div>
      </BrowserRouter>
    </>
  );
}

export default App;
