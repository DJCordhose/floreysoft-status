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

import {signIn, signedIn} from './actions/actions';

// https://github.com/reactjs/react-router/blob/master/docs/Glossary.md#enterhook
function requireAuth(nextState, replace) {
    if (!signedIn) {
        const {pathname} = nextState.location;
        const root = '/';
        signIn(false)
            .then(() => {
                history.replace(pathname);
            })
            .catch(() => {
                history.replace(root)
            });

        replace(root);
    }
}

export default function () {
    ReactDOM.render(
        <Router history={history}>
            <Route path="/" component={App}>
                <IndexRoute component={OverviewPage} />
                <Route path="admin" component={AdminPage} onEnter={requireAuth}/>
            </Route>
        </Router>
        , document.getElementById('root'));
}
