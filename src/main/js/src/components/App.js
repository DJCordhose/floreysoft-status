/* @flow */

import React, {Component} from 'react';

import getMuiTheme from 'material-ui/styles/getMuiTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import {deepOrange500} from 'material-ui/styles/colors';
import Overview from './Overview';
import AppBar from './AppBar';
import TestDialog from './TestDialog';

import {List} from 'immutable';

// https://flowtype.org/blog/2015/02/18/Import-Types.html
import type {Test} from '../types/Test';

const muiTheme = getMuiTheme({
    palette: {
        accent1Color: deepOrange500,
    },
});

const styles = {
    container: {
        // textAlign: 'center',
        // paddingTop: 200,
    },
};

type State = {
    tests: List<Test>;
    dialogOpen: boolean;
    currentTestId: number;
}

class App extends Component<any, any, State> {
    state: State;

    constructor(props: any) {
        super(props);
        this.state = {
            tests: List.of({
                id: 1,
                name: 'Test1',
                interval: 5,
                description: 'Noch ein Test',
                url: 'url1'
            }, {
                id: 2,
                name: 'Test2',
                interval: 5,
                description: 'Noch ein Test 2',
                url: 'url2'
            }),
            dialogOpen: false,
            currentTestId: 1
        }
    }

    openTest(id: number) {

    }

    findTestForId(id: number): ?Test {
        const test = this.state.tests.find(test => test.id === id);
        return test;
    }

    saveTest(test: Test) {
        const storedTest = this.findTestForId(test.id);
    }

    render() {
        const {tests, dialogOpen, currentTestId} = this.state;
        const currentTest = this.findTestForId(currentTestId);
        return <MuiThemeProvider muiTheme={muiTheme}>
            <div style={styles.container}>
                <AppBar />
                <Overview tests={tests}/>
                <TestDialog
                    open={dialogOpen}
                    test={currentTest}
                    onSaved={(test: Test) => this.saveTest(test)}
                />
            </div>
        </MuiThemeProvider>
    }
}

export default App;
