/* @flow */

// https://github.com/reactjs/react-router/blob/master/upgrade-guides/v2.0.0.md#using-custom-histories
import { useRouterHistory } from 'react-router';
import { createHashHistory } from 'history';
const history = useRouterHistory(createHashHistory)({ queryKey: false });

export default history;