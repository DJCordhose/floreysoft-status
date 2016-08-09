import React from 'react';
import AppBar from 'material-ui/AppBar';
import FlatButton from 'material-ui/FlatButton';
import IconButton from 'material-ui/IconButton';
import IconMenu from 'material-ui/IconMenu';
import MenuItem from 'material-ui/MenuItem';
import MoreVertIcon from 'material-ui/svg-icons/navigation/more-vert';
import NavigationClose from 'material-ui/svg-icons/navigation/close';

const AppBarExampleIconMenu = () => {
    const menue = <div>
        <FlatButton label="Save" />
        <FlatButton label="Delete" />
        <IconMenu
            iconButtonElement={<IconButton><MoreVertIcon /></IconButton>}
            targetOrigin={{horizontal: 'right', vertical: 'top'}}
            anchorOrigin={{horizontal: 'right', vertical: 'top'}}
        >
            <MenuItem primaryText="Refresh"/>
            <MenuItem primaryText="Sign out"/>
        </IconMenu>
    </div>;

    return <AppBar
        title="Test Administration"
        iconElementLeft={<IconButton><NavigationClose /></IconButton>}
        iconElementRight={menue}
    />};

export default AppBarExampleIconMenu;