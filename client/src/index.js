import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import { Theme, Button } from 'react-daisyui'

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <Theme dataTheme="emerald">
      <App/>
    </Theme>
  </React.StrictMode>
);

