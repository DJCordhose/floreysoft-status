/* @flow */

import React from 'react';
import ReactDOM from 'react-dom';

import getMuiTheme from 'material-ui/styles/getMuiTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import {deepOrange500} from 'material-ui/styles/colors';
const muiTheme = getMuiTheme({
    palette: {
        accent1Color: deepOrange500,
    },
});

import AdminPage from './components/AdminPage';

export default function() {
    ReactDOM.render(
        <MuiThemeProvider muiTheme={muiTheme}>
            <AdminPage />
        </MuiThemeProvider>
        , document.getElementById('root'));
}