import { CssBaseline, ThemeProvider } from "@mui/material";
import { useRoutes } from "react-router-dom";
import { useSelector } from "src/store/Store";
import { ThemeSettings } from "./theme/Theme.tsx";
import RTL from "./layouts/full/shared/customizer/RTL.tsx";
import ScrollToTop from "./components/shared/ScrollToTop.tsx";
import Router from "./routes/Router.tsx";
import { AppState } from "./store/Store.tsx";

function App() {
  const routing = useRoutes(Router);
  const theme = ThemeSettings();
  const customizer = useSelector((state: AppState) => state.customizer);

  return (
    <ThemeProvider theme={theme}>
      <RTL direction={customizer.activeDir}>
        <CssBaseline />
        <ScrollToTop>{routing}</ScrollToTop>
      </RTL>
    </ThemeProvider>
  );
}

export default App;
