/* @flow */

import React, {Component} from 'react';

import Overview from './Overview';
import AppBar from './AppBar';
import TestDialog from './TestDialog';

import {List} from 'immutable';

import {loadTests} from '../actions/actions';

// https://flowtype.org/blog/2015/02/18/Import-Types.html
import type {Test} from '../types/Test';
import type {Selection} from '../types/ui';

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
            url: 'url2',
            disabled: true
        };
        this.state = {
            tests: List.of(test1, test2),
            dialogOpen: false,
            currentTest: test1,
        }
    }

    componentDidMount() {
        loadTests().then((statusList: Array<Test>) => {
            this.setState({
                tests: List(statusList) // eslint-disable-line new-cap
            });

        })
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
                tests: this.state.tests.delete(key).push(test).sort((t1: Test, t2: Test) => t1.id - t2.id)
            });
        }
    }

    closeDialog() {
        this.setState({
            dialogOpen: false
        });
    }

    executeAction(action: string) {
        const {tests} = this.state;
        if (action === 'edit') {
            const firstSelectedTest = tests.find(test => test.selected);
            if (firstSelectedTest) {
                this.openTest(firstSelectedTest.id);
            }
        } else {
            console.error(`Should execute ${action}`);
        }
    }

    setSelection(selection: Selection) {
        const {tests} = this.state;
        tests.forEach((test: Test) => {
            test.selected = false;
            return true;
        });
        if (selection instanceof Array) {
            selection.forEach((index) => {
                tests.get(index).selected = true;
            })
        } else if (selection === "all") {
            tests.forEach((test: Test) => {
                test.selected = true;
                return true;
            });
        }
        this.setState({
            tests
        });
    }

    render() {
        const {tests, dialogOpen, currentTest} = this.state;
        return <div style={styles.container}>
            <AppBar
                onAction={(action: string) => this.executeAction(action)}
            />
            <Overview
                tests={tests}
                onSelect={(selection: Selection) => this.setSelection(selection)}
                onSave={(test: Test) => this.saveTest(test)}
            />
            <TestDialog
                open={dialogOpen}
                test={currentTest}
                onSaved={(test: Test) => {
                        this.saveTest(test);
                        this.closeDialog();
                    }}
                onCanceled={() => this.closeDialog()}
            />
        </div>;
    }
}

export default App;
