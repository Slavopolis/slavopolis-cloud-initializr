// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React, { Suspense } from 'react';
import { Provider } from 'react-redux';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';

import App from './App.tsx';
import { store } from './store/Store.tsx';
import Spinner from './views/spinner/Spinner.tsx';
import './utils/i18n.ts';
import './_mockApis';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <Provider store={store}>
    <Suspense fallback={<Spinner />}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </Suspense>
  </Provider>,
)
