/* @flow */

import React, {Component} from 'react';
import {List} from 'immutable';
import type {Test} from '../types/Test';
import TestEntry from './TestEntry';
import {List as MaterialList, ListItem} from 'material-ui/List';

type Props = {
    tests: List<Test>;
    onOpen(test: Test): void;
};

class Overview extends Component<any, Props, void> {
    render() {
        const {tests, onOpen} = this.props;
        return (
            <MaterialList>
                {tests.map(test => <TestEntry key={test.id} onOpen={onOpen} test={test}/>)}
            </MaterialList>
        );
    }
}

export default Overview;