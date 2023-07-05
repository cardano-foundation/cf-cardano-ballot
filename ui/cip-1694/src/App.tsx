import React from "react";
import { BrowserRouter as Router } from "react-router-dom";
import "./App.scss";
import Content from "./components/layout/Content/Content";
import Footer from "./components/layout/Footer/Footer";
import Header from "./components/layout/Header/Header";
import { Toaster } from "react-hot-toast";
import AccountBalanceWalletIcon from "@mui/icons-material/AccountBalanceWallet";

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
              className: "",
              icon: (
                <AccountBalanceWalletIcon
                  style={{ marginRight: "8px" }}
                  height={22}
                  width={22}
                />
              ),
              style: {
                borderRadius: "10px",
                background: "#030321",
                color: "#fff",
              },
            }}
          />
        </Router>
      </div>
    </>
  );
}

export default App;
