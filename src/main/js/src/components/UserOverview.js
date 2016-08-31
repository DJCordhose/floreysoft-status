/* @flow */

import React, {Component} from 'react';
import {List} from 'immutable';
import type {Report} from '../types/Report';
import {Table, TableBody, TableHeader, TableHeaderColumn, TableRow, TableRowColumn} from 'material-ui/Table';
import FontIcon from 'material-ui/FontIcon';
import {yellow500, red500, green500} from 'material-ui/styles/colors';

const styles = {
    table: {
        height: '300px'
    },
    propToggleHeader: {
        margin: '20px auto 10px',
    },
};

type Props = {
    reports: List<Report>;
};

class UserOverview extends Component<any, Props, void> {
    render() {
        const {reports} = this.props;
        return (
            <div>
                <Table
                    height={styles.table.height}
                    fixedHeader={true}
                    fixedFooter={true}
                    selectable={false}
                    multiSelectable={false}
                >
                    <TableHeader
                        displaySelectAll={false}
                        adjustForCheckbox={false}
                        enableSelectAll={false}
                    >
                        <TableRow>
                            <TableHeaderColumn colSpan="4" tooltip="Current Reports" style={{textAlign: 'center'}}>
                                Test-Reports
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip="The Name">Name</TableHeaderColumn>
                            <TableHeaderColumn tooltip="What is the test about?">Description</TableHeaderColumn>
                            <TableHeaderColumn
                                tooltip="When was the test last executed?">Last Checked</TableHeaderColumn>
                            <TableHeaderColumn tooltip="What was the result of that test?">Status</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        displayRowCheckbox={false}
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}
                    >
                        {reports.map(report => {
                            const {id, name, description, timestamp, status} = report;
                            let statusSymbol;
                            if (status === 'OK') {
                                statusSymbol = <FontIcon className="material-icons"
                                                         color={green500}>check_circle</FontIcon>;
                            } else if (status === 'FAIL') {
                                statusSymbol = <FontIcon className="material-icons"
                                                         color={red500}>error</FontIcon>;
                            } else {
                                statusSymbol = <FontIcon className="material-icons"
                                                         color={yellow500}>warning</FontIcon>;
                            }
                            const lastExecuted = new Date(Number.parseInt(timestamp, 10)).toString();

                            return <TableRow key={id}>
                                <TableRowColumn>{name}</TableRowColumn>
                                <TableRowColumn>{description}</TableRowColumn>
                                <TableRowColumn>{lastExecuted}</TableRowColumn>
                                <TableRowColumn>{statusSymbol}</TableRowColumn>
                            </TableRow>;
                        })}
                    </TableBody>
                </Table>
            </div>
        );
    }
}

export default UserOverview;