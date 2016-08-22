/* @flow */
/* global gapi */

import type {Test} from '../types/Test';
import type {Report} from '../types/Report';

declare var gapi;

export function loadTests(): Promise<Array<Test>> {
    const promise = new Promise(resolve => {
        gapi.client.status.tests.listTests().execute(resp => {
            if (!resp.code) {
                resp.items = resp.items || [];
                resolve(resp.items);
            }
        });
    });
    return promise;
}

export function saveTest(test: Test): Promise<Test> {
    const promise = new Promise(resolve => {
        gapi.client.status.test.save().execute(resp => {
            if (!resp.code) {
                resolve(resp);
            }
        });
    });
    return promise;
}

export function loadReports(): Promise<Array<Report>> {
    const promise = new Promise(resolve => {
        gapi.client.status.tests.listCurrentReports().execute(resp => {
            if (!resp.code) {
                resp.items = resp.items || [];
                resolve(resp.items);
            }
        });
    });
    return promise;
}
