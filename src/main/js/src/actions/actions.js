/* @flow */
/* global gapi */

import type {Test} from '../types/Test';
import type {Report} from '../types/Report';

declare var gapi;

const CLIENT_ID = '933874018426-irk32kd7hstnhbpb3rbe8kahc8mn236h.apps.googleusercontent.com';
const SCOPES = 'https://www.googleapis.com/auth/userinfo.email';

export let signedIn = false;

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

export function signIn(mode: boolean = true): Promise<void> {
    const promise = new Promise((resolve, reject) => {
        gapi.auth.authorize({
            client_id: CLIENT_ID,
            scope: SCOPES,
            immediate: mode
        }, () => {
            gapi.client.oauth2.userinfo.get().execute(resp => {
                if (!resp.code) {
                    signedIn = true;
                    resolve();
                } else {
                    signedIn = false;
                    reject();
                }
            });
        });
    });
    return promise;
}
