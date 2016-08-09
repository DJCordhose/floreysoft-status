import React from 'react';
import ReactDOM from 'react-dom';
import Main from './components/main';
import './index.css';

// https://github.com/webpack/expose-loader
// import 'expose?jQuery!jquery';
// import 'expose?Tether!tether';
import 'expose?init!./api-init';

// import 'bootstrap/dist/js/bootstrap';
// import 'bootstrap/dist/css/bootstrap.css';

ReactDOM.render(
  <Main />,
    document.getElementById('root')

// $('#root')[0]
);
