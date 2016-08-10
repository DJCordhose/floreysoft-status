import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';

import injectTapEventPlugin from 'react-tap-event-plugin';
// Needed for onTouchTap
// http://stackoverflow.com/a/34015469/988941
injectTapEventPlugin();

// https://github.com/webpack/expose-loader
import 'expose?init!./api-init';

import getMuiTheme from 'material-ui/styles/getMuiTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import {deepOrange500} from 'material-ui/styles/colors';
const muiTheme = getMuiTheme({
    palette: {
        accent1Color: deepOrange500,
    },
});

import App from './components/App';
// import App from './components/TableExampleComplex';
ReactDOM.render(
    <MuiThemeProvider muiTheme={muiTheme}>
        <App />
    </MuiThemeProvider>
    , document.getElementById('root'));
