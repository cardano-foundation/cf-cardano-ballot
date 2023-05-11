import React from 'react';
import logo from './commons/resources/images/logo.svg';
import './App.scss';

function App() {
  return (
    <>
      <div className="App">
        <header className="App-header"></header>
        <img
          src={logo}
          className="App-logo"
          alt="logo"
        />
        <h1>Cardano Voting App</h1>
      </div>
    </>
  );
}

export default App;
