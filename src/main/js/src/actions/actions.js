/* @flow */
/* global gapi */

import type {Test} from '../types/Test';
import type {Report} from '../types/Report';

declare var gapi;

const CLIENT_ID = '113969358901-6l9q3h2n3biumcimcld1g65vlunsei7b.apps.googleusercontent.com';
const SCOPES = 'https://www.googleapis.com/auth/userinfo.email';

export let signedIn = false;

import history from '../app-history';

export function navigateToRoot() {
    const root = '/';
    history.push(root);
}

export function loadTests(): Promise<Array<Test>> {
    const promise = new Promise(resolve => {
        gapi.client.status.tests().execute(resp => {
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
        gapi.client.status.test.save(test).execute(resp => {
            if (!resp.code) {
                resolve(resp);
            }
        });
    });
    return promise;
}

export function addTest(test: Test): Promise<Test> {
    const promise = new Promise(resolve => {
        gapi.client.status.test.add(test).execute(resp => {
            if (!resp.code) {
                resolve(resp);
            }
        });
    });
    return promise;
}

export function deleteTest(test: Test): Promise<Test> {
    const promise = new Promise(resolve => {
        gapi.client.status.test.delete({
            id: test.id
        }).execute(resp => {
            if (!resp.code) {
                resolve(resp);
            }
        });
    });
    return promise;
}

export function loadReports(): Promise<Array<Report>> {
    const promise = new Promise(resolve => {
        gapi.client.status.reports().execute(resp => {
            if (!resp.code) {
                resp.items = resp.items || [];
                resolve(resp.items);
            }
        });
    });
    return promise;
}

// https://developers.google.com/api-client-library/javascript/reference/referencedocs#auth-deprecated
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