import React from "react";
import "./App.scss";
import Content from "./components/layout/Content/Content";
import Footer from "./components/layout/Footer/Footer";
import Header from "./components/layout/Header/Header";

function App() {
  return (
    <>
      <div className="App">
        <Header />
        <Content />
        <Footer />
      </div>
    </>
  );
}

export default App;
