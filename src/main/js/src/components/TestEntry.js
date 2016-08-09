/* @flow */

import React, {Component} from 'react';
import {ListItem} from 'material-ui/List';

import ActionInfo from 'material-ui/svg-icons/action/info';

import type {Test} from '../types/Test';

class TestEntry extends Component<any, Test, void> {
    render() {
        const {name, description, url, interval} = this.props;
        return <ListItem primaryText={<span>{name}, {url}</span>} rightIcon={<ActionInfo />} />
    }
}

export default TestEntry;