/* @flow */
/* global gapi */

import type {Test} from '../types/Test';

declare var gapi;

export function load(): Promise<Array<Test>> {
    const promise = new Promise(resolve => {
        gapi.client.status.greetings.listTests().execute(
            function (resp) {
                if (!resp.code) {
                    resp.items = resp.items || [];
                    resolve(resp.items);
                }
            });
    });
    return promise;
}
