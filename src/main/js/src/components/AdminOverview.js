/* @flow */

import React, {Component} from 'react';
import {List} from 'immutable';
import type {Test} from '../types/Test';
import {Table, TableBody, TableHeader, TableHeaderColumn, TableRow, TableRowColumn} from 'material-ui/Table';
import Toggle from 'material-ui/Toggle';
import type {Selection} from '../types/ui';

const styles = {
    table: {
        height: '300px'
    },
    propToggleHeader: {
        margin: '20px auto 10px',
    },
};

type Props = {
    tests: List<Test>;
    onSave(test: Test): void;
    onSelect(selection: Selection): void;
};

class AdminOverview extends Component<any, Props, void> {
    fuckTheNextSelection: boolean;
    render() {
        const {tests, onSave, onSelect} = this.props;
        return (
            <div>
                <Table
                    height={styles.table.height}
                    fixedHeader={true}
                    fixedFooter={true}
                    selectable={true}
                    multiSelectable={true}
                    onRowSelection={(e) => {
                        if (this.fuckTheNextSelection) {
                            this.fuckTheNextSelection = false;
                        } else {
                            onSelect(e);
                        }
                    }}
                    onCellClick={(rowNumber, columnId) => {
                        if (columnId === 5) {
                            this.fuckTheNextSelection = true;
                        }
                    }}
                >
                    <TableHeader
                        displaySelectAll={false}
                        adjustForCheckbox={true}
                        enableSelectAll={false}
                    >
                        <TableRow>
                            <TableHeaderColumn colSpan="5" tooltip="All configured Tests" style={{textAlign: 'center'}}>
                                Tests
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip="Select the test"></TableHeaderColumn>
                            <TableHeaderColumn tooltip="The Name">Name</TableHeaderColumn>
                            <TableHeaderColumn tooltip="The Description">Description</TableHeaderColumn>
                            <TableHeaderColumn tooltip="The Description">URL</TableHeaderColumn>
                            <TableHeaderColumn tooltip="The Interval">Interval</TableHeaderColumn>
                            <TableHeaderColumn tooltip="Is this Test active?">Suspended</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        displayRowCheckbox={true}
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}
                    >
                        {tests.map(test => {
                            const {id, name, description, url, interval, disabled, selected} = test;

                            return <TableRow key={id} selected={selected}>
                                <TableRowColumn></TableRowColumn>
                                <TableRowColumn>{name}</TableRowColumn>
                                <TableRowColumn>{description}</TableRowColumn>
                                <TableRowColumn>{url}</TableRowColumn>
                                <TableRowColumn>{interval}</TableRowColumn>
                                <TableRowColumn><Toggle
                                    name="disabled"
                                    onToggle={(e) => {
                                        test.disabled = !disabled;
                                        onSave(test);
                                    }}
                                    toggled={disabled || false}
                                /></TableRowColumn>
                            </TableRow>;
                        })}
                    </TableBody>
                </Table>
            </div>
        );
    }
}

export default AdminOverview;