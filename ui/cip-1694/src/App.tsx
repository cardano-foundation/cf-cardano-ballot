import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Content from './components/layout/Content/Content';
import Footer from './components/layout/Footer/Footer';
import Header from './components/layout/Header/Header';
import './App.scss';

function App() {
  return (
    <>
      <div className="App">
        <Router>
          <Header />
          <Content />
          <Footer />
          <Toaster
            toastOptions={{
              // TODO: let's deside on the approach we use: either the inline styles or className?
              className: '',
              style: {
                borderRadius: '10px',
                background: '#030321',
                color: '#fff',
              },
            }}
          />
        </Router>
      </div>
    </>
  );
}

export default App;
