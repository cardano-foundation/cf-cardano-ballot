import React from 'react';
import { Footer } from './components/common/Footer/Footer';
import { BrowserRouter } from 'react-router-dom';
import './App.scss';
import BackgroundPolygon1 from './common/resources/images/polygon1.svg';
import { Box, Container } from '@mui/material';
import Header from './components/common/Header/Header';
import { PageRouter } from './routes';

function App() {
  return (
    <>
      <BrowserRouter>
        <img
          src={BackgroundPolygon1}
          alt="Background Shape"
          className="background-shape-1"
        />
        <div className="App">
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
