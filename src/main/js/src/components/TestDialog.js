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
    onCanceled(): void;
};

class TestDialog extends Component<any, Props, Test> {
    state: Test;

    constructor(props: Props) {
        super(props);
        this.state = props.test;
    }

    componentWillReceiveProps(props: Props) {
        this.state = props.test;
    }

    render() {
        const {open, onSaved, onCanceled} = this.props;
        const test = this.state;
        const {id, name, description, url, interval} = test;

        const standardActions = <div>
            <FlatButton
                label="Ok"
                primary={true}
                onTouchTap={() => onSaved(test)}
            />
            <FlatButton
                label="Cancel"
                primary={true}
                onTouchTap={onCanceled}
            />
        </div>;

        return <Dialog
            open={open}
            title="Sekrete"
            actions={standardActions}
            onRequestClose={onCanceled}
        >
            <TextField
                hintText="Enter Name"
                floatingLabelText="Name"
                onChange={(e) => this.setState({name: e.target.value})}
                onKeyDown={(e: KeyboardEvent)=> e.keyCode === 13 && onSaved(test)}
                value={name}
            />
            <TextField
                hintText="Enter URL"
                floatingLabelText="URL"
                onChange={(e) => this.setState({url: e.target.value})}
                onKeyDown={(e: KeyboardEvent)=> e.keyCode === 13 && onSaved(test)}
                value={url}
            />
        </Dialog>;
    }
}

export default TestDialog;