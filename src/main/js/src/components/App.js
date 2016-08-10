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
    currentTest: Test;
}

class App extends Component<any, any, State> {
    state: State;

    constructor(props: any) {
        super(props);
        const test1 = {
            id: 1,
            name: 'Test1',
            interval: 5,
            description: 'Noch ein Test',
            url: 'url1'
        };
        const test2 = {
            id: 2,
            name: 'Test2',
            interval: 5,
            description: 'Noch ein Test 2',
            url: 'url2'
        };
        this.state = {
            tests: List.of(test1, test2),
            dialogOpen: false,
            currentTest: test1
        }
    }

    openTest(id: number) {
        const testEntry = this.findTestEntryForId(id);
        if (testEntry) {
            const test = testEntry[1];
            const testCopy = Object.assign({}, test);
            this.setState({
                dialogOpen: true,
                currentTest: testCopy
            });
        }
    }

    findTestEntryForId(id: number): ?[number, Test] {
        // https://facebook.github.io/immutable-js/docs/#/List/findEntry
        return this.state.tests.findEntry(test => test.id === id);
    }

    saveTest(test: Test) {
        const storedTestEntry = this.findTestEntryForId(test.id);
        if (storedTestEntry) {
            const key = storedTestEntry[0];
            this.setState({
                tests: this.state.tests.delete(key).push(test)
            });
        }
        this.closeDialog();
    }

    closeDialog() {
        this.setState({
            dialogOpen: false
        });
    }

    render() {
        const {tests, dialogOpen, currentTest} = this.state;
        return <MuiThemeProvider muiTheme={muiTheme}>
            <div style={styles.container}>
                <AppBar />
                <Overview tests={tests} onOpen={(test: Test) => this.openTest(test.id)}/>
                <TestDialog
                    open={dialogOpen}
                    test={currentTest}
                    onSaved={(test: Test) => this.saveTest(test)}
                    onCanceled={() => this.closeDialog()}
                />
            </div>
        </MuiThemeProvider>
    }
}

export default App;
