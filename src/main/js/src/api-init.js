/* global gapi */
import main from './main';

const apiRoot = '//' + window.location.host + '/_ah/api';
// let appLoaded = false;
// let oauthLoaded = false;

function initialize(apiRoot) {
    const callbackApp = () => {
        // appLoaded = true;
        console.log("App API initialized");
        main();
    };
    const callbackOauth = () => {
        // oauthLoaded = true;
        console.log("Oauth initialized");
    };

    gapi.client.load('status', 'v1', callbackApp, apiRoot);
    gapi.client.load('oauth2', 'v2', callbackOauth);
}

export function init() {
    console.log("Initializing APIs");
    initialize(apiRoot);
}