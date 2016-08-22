/* @flow */

import React, {Component} from 'react';

import AppBar from 'material-ui/AppBar';
import FlatButton from 'material-ui/FlatButton';
import IconButton from 'material-ui/IconButton';
import IconMenu from 'material-ui/IconMenu';
import MenuItem from 'material-ui/MenuItem';
import MoreVertIcon from 'material-ui/svg-icons/navigation/more-vert';
import NavigationClose from 'material-ui/svg-icons/navigation/close';

type Props = {
    onAction(action: string): void;
};

class MyAppBar extends Component<void, Props, void> {
    render() {
        const {onAction} = this.props;
        const menue = <div>
            <FlatButton label="Edit" onClick={() => onAction('edit')}/>
            <FlatButton label="Delete" onClick={() => onAction('delete')}/>
            <IconMenu
                iconButtonElement={<IconButton><MoreVertIcon /></IconButton>}
                targetOrigin={{horizontal: 'right', vertical: 'top'}}
                anchorOrigin={{horizontal: 'right', vertical: 'top'}}
            >
                <MenuItem primaryText="Refresh" onClick={() => onAction('refresh')}/>
                <MenuItem primaryText="Sign in" onClick={() => onAction('login')}/>
                <MenuItem primaryText="Sign out" onClick={() => onAction('logout')}/>
            </IconMenu>
        </div>;

        return <AppBar
            title="Test Administration"
            iconElementLeft={<IconButton><NavigationClose /></IconButton>}
            iconElementRight={menue}
        />
    }
}

export default MyAppBar;