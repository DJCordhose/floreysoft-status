/* @flow */

import React, {Component} from 'react';

import AppBar from 'material-ui/AppBar';
import IconButton from 'material-ui/IconButton';
import FontIcon from 'material-ui/FontIcon';
import NavigationClose from 'material-ui/svg-icons/navigation/close';

type Props = {
    onAction(action: string): void;
};

// https://design.google.com/icons
// http://www.material-ui.com/#/components/icon-button
// http://www.material-ui.com/#/components/font-icon
class MyAppBar extends Component<void, Props, void> {
    render() {
        const {onAction} = this.props;
        const menue = <div>
            <IconButton
                tooltip="Edit"
                onClick={() => onAction('edit')}
            >
                <FontIcon className="material-icons">create</FontIcon>
            </IconButton>
            <IconButton
                tooltip="Add"
                onClick={() => onAction('add')}
            >
                <FontIcon className="material-icons">add_circle_outline</FontIcon>
            </IconButton>
            <IconButton
                tooltip="Delete"
                onClick={() => onAction('delete')}
            >
                <FontIcon className="material-icons">delete</FontIcon>
            </IconButton>
            <IconButton
                tooltip="Refresh"
                onClick={() => onAction('refresh')}
            >
                <FontIcon className="material-icons">cached</FontIcon>
            </IconButton>
            <IconButton
                tooltip="To Report"
                onClick={() => onAction('to report')}
            >
                <FontIcon className="material-icons">description</FontIcon>
            </IconButton>
        </div>;

        return <AppBar
            title="Test Administration"
            iconElementLeft={<IconButton><NavigationClose /></IconButton>}
            iconElementRight={menue}
        />
    }
}

export default MyAppBar;