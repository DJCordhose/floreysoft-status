/* @flow */

import React, {Component} from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import TextField from 'material-ui/TextField';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import type {Test} from '../types/Test';

type Props = {
    open: boolean;
    test: Test;
    onSaved(test: Test): void;
};

class TestDialog extends Component<any, Props, Test> {
    state: Test;

    constructor(props: Props) {
        super(props);
        this.state = props.test;
    }

    render() {
        const {open, onSaved} = this.props;
        const test = this.state;

        const standardActions = (
            <FlatButton
                label="Ok"
                primary={true}
                onTouchTap={() => onSaved(test)}
            />
        );

        return <Dialog
            open={open}
            title="Sekrete"
            actions={standardActions}
            onRequestClose={() => onSaved(test)}
        >
            <TextField
                hintText="Fucking text"
                floatingLabelText="Enter the fucking text"
                onChange={(e) => this.setState({name: e.target.value})}
                onKeyDown={(e: KeyboardEvent)=> e.keyCode === 13 && onSaved(test)}
            />
        </Dialog>;
    }
}

export default TestDialog;