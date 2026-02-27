import { createRoot } from "react-dom/client";
import "./styles/index.css";
import App from "./app.tsx";
import { StrictMode } from "react";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
