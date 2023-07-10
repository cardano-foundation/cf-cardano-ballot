import React, { BrowserRouter as Router } from 'react-router-dom';
import './App.scss';
import { Toaster } from 'react-hot-toast';
import Content from './components/common/Content/Content';
import Footer from './components/common/Footer/Footer';
import Header from './components/common/Header/Header';

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
