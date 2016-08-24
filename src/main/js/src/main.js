/* @flow */

import React from 'react';
import ReactDOM from 'react-dom';

import {Router, Route, IndexRoute, useRouterHistory } from 'react-router';

// https://github.com/reactjs/react-router/blob/master/upgrade-guides/v2.0.0.md#using-custom-histories
import { createHashHistory } from 'history';
const history = useRouterHistory(createHashHistory)({ queryKey: false });

import App from './components/App';
import AdminPage from './components/AdminPage';
import OverviewPage from './components/OverviewPage';

export default function () {
    ReactDOM.render(
        <Router history={history}>
            <Route path="/" component={App}>
                <IndexRoute component={OverviewPage}/>
                <Route path="admin" component={AdminPage} />
            </Route>
        </Router>
        , document.getElementById('root'));
}
