/* @flow */

import React, {Component} from 'react';
import {List} from 'immutable';
import type {Test} from '../types/Test';
import TestEntry from './TestEntry';
import {List as MaterialList, ListItem} from 'material-ui/List';

type Props = {
    tests: List<Test>;
};

class Overview extends Component<any, Props, void> {
    render() {
        const {tests} = this.props;
        return (
            <MaterialList>
                {tests.map(test => <TestEntry {...test}/> )}
            </MaterialList>
        );
    }
}

export default Overview;