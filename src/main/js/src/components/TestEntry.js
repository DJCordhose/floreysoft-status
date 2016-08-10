/* @flow */

import React, {Component} from 'react';
import {ListItem} from 'material-ui/List';

import ActionInfo from 'material-ui/svg-icons/action/info';

import type {Test} from '../types/Test';

type Props = {
    test: Test;
    onOpen(test: Test): void;
};

class TestEntry extends Component<any, Props, void> {
    render() {
        const {onOpen, test} = this.props;
        const {id, name, description, url, interval} = test;
        return <ListItem
            primaryText={<span>{name}, {url}</span>}
            rightIcon={<ActionInfo />}
            onClick={() => onOpen(test)}
        />
    }
}

export default TestEntry;