/* @flow */

import React, {Component} from 'react';
import RaisedButton from 'material-ui/RaisedButton';
import TextField from 'material-ui/TextField';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';

const styles = {
    container: {
        textAlign: 'center',
        paddingTop: 200,
    },
};

type Props = any;

type State = {
    open: boolean;
    text: string;
};

class Overview extends Component<any, Props, State> {
    state: State;

    constructor(props: Props) {
        super(props);
        this.state = {
            open: false,
            text: ""
        };
    }

    render() {
        const {open} = this.state;

        const standardActions = (
            <FlatButton
                label="Ok"
                primary={true}
                onTouchTap={() => this.setState({open: false})}
            />
        );

        return (
            <div style={styles.container}>
                <Dialog
                    open={open}
                    title="Sekrete"
                    actions={standardActions}
                    onRequestClose={() => this.setState({open: false})}
                >
                    <TextField
                        hintText="Fucking text"
                        floatingLabelText="Enter the fucking text"
                        onChange={(e) => this.setState({text: e.target.value})}
                        onKeyDown={(e: KeyboardEvent)=> e.keyCode === 13 && this.setState({open: false})}
                    />
                </Dialog>
                <h1>Material-UI</h1>
                <h2>example project</h2>
                <RaisedButton
                    label="Add URL"
                    secondary={true}
                    onTouchTap={() => this.setState({open: true})}
                />
            </div>
        );
    }
}

export default Overview;