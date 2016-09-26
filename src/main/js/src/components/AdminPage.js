/* @flow */

import React, {Component} from 'react';

import Overview from './AdminOverview';
import AppBar from './AppBar';
import TestDialog from './TestDialog';

import {List} from 'immutable';

import {loadTests, saveTest, addTest, deleteTest, navigateToRoot, signIn} from '../actions/actions';

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
    currentTest: ?Test;
}

class AdminPage extends Component<any, any, State> {
    state: State;

    constructor(props: any) {
        super(props);
        const test1 = {
            id: '1',
            name: 'Test1',
            interval: 5,
            description: 'Noch ein Test',
            url: 'url1'
        };
        const test2 = {
            id: '2',
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
        this.loadTests();
    }

    loadTests() {
        loadTests().then((tests: Array<Test>) => {
            this.setState({
                tests: List(tests).sort( // eslint-disable-line new-cap
                    (t1: Test, t2: Test) =>
                        t1.id === t2.id ? 0 :
                            (t1.id > t2.id ? 1 : -1))
            });

        })
    }

    openTestById(id: string) {
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

    openNewTest() {
        const test: Test = {
            id: '-1',
            name: '',
            interval: -1,
            description: '',
            url: ''
        };
        this.setState({
            dialogOpen: true,
            currentTest: test
        });
    }

    findTestEntryForId(id: string): ?[number, Test] {
        // https://facebook.github.io/immutable-js/docs/#/List/findEntry
        return this.state.tests.findEntry(test => test.id === id);
    }

    saveTest(test: Test) {
        if (test.id !== -1) {
            saveTest(test).then(() => this.loadTests());
        } else {
            addTest(test).then(() => this.loadTests());
        }
    }

    deleteTest(test: Test) {
        deleteTest(test).then(() => this.loadTests());
    }

    closeDialog() {
        this.setState({
            dialogOpen: false
        });
    }

    executeAction(action: string) {
        const {tests} = this.state;
        if (action === 'login') {
            signIn(false).then(() => console.log('logged in')).catch(()=>console.warn('login failed'))
        } else if (action === 'edit') {
            const firstSelectedTest = tests.find(test => test.selected);
            if (firstSelectedTest) {
                this.openTestById(firstSelectedTest.id);
            }
        } else if (action === 'add') {
            this.openNewTest();
        } else if (action === 'delete') {
            const firstSelectedTest = tests.find(test => test.selected);
            if (firstSelectedTest) {
                this.deleteTest(firstSelectedTest);
            }
        } else if (action === 'refresh') {
            this.loadTests();
        } else if (action === 'to report') {
            navigateToRoot();
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

export default AdminPage;
