/* @flow */

import React from 'react';

import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import Overview from './Overview';

const App = () => (
    <MuiThemeProvider>
        <Overview />
    </MuiThemeProvider>
);

export default App;
