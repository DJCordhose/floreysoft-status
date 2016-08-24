/* @flow */

import React, {Component} from 'react';
import {List} from 'immutable';
import type {Report} from '../types/Report';
import {Table, TableBody, TableHeader, TableHeaderColumn, TableRow, TableRowColumn} from 'material-ui/Table';

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
                            <TableHeaderColumn colSpan="5" tooltip="Current Reports" style={{textAlign: 'center'}}>
                                Reports
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip="Select the test"></TableHeaderColumn>
                            <TableHeaderColumn tooltip="The Name">Name</TableHeaderColumn>
                            <TableHeaderColumn tooltip="The Description">Description</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        displayRowCheckbox={false}
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}
                    >
                        {reports.map(report => {
                            const {id, name, description} = report;

                            return <TableRow key={id}>
                                <TableRowColumn></TableRowColumn>
                                <TableRowColumn>{name}</TableRowColumn>
                                <TableRowColumn>{description}</TableRowColumn>
                            </TableRow>;
                        })}
                    </TableBody>
                </Table>
            </div>
        );
    }
}

export default UserOverview;