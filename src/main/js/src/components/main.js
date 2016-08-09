/* @flow */

import React from 'react';

import getMuiTheme from 'material-ui/styles/getMuiTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import {deepOrange500} from 'material-ui/styles/colors';
import Overview from './Overview';

const muiTheme = getMuiTheme({
    palette: {
        accent1Color: deepOrange500,
    },
});

const App = () => (
    <MuiThemeProvider muiTheme={muiTheme}>
        <Overview />
    </MuiThemeProvider>
);

export default App;
