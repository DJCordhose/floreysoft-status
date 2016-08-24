/* @flow */

import React, {Component} from 'react';

import {List} from 'immutable';

import {loadReports} from '../actions/actions';

import type {Report} from '../types/Report';

import Overview from './UserOverview';

const styles = {
    container: {
        // textAlign: 'center',
        // paddingTop: 200,
    },
};


type State = {
    reports: List<Report>;
}

class OverviewPage extends Component<any, any, State> {
    state: State;

    constructor(props: any) {
        super(props);
        this.state = {
            reports: List(),  // eslint-disable-line new-cap
        }
    }

    componentDidMount() {
        this.loadReports();
    }

    loadReports() {
        loadReports().then((reports: Array<Report>) => {
            this.setState({
                reports: List(reports).sort((r1: Report, r2: Report) => r1.id - r2.id) // eslint-disable-line new-cap
            });

        })
    }


    render() {
        const {reports} = this.state;
        return <div style={styles.container}>
            <Overview
                reports={reports}
            />
        </div>;
    }
}

export default OverviewPage;
