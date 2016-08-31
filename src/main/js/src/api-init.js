/* global gapi */
import main from './main';

const apiRoot = '//' + window.location.host + '/_ah/api';
let appLoaded = false;
let oauthLoaded = false;

function checkFullInitAndStartMain() {
    if (appLoaded && oauthLoaded) {
        main();
    }
}

function initialize(apiRoot) {
    const callbackApp = () => {
        appLoaded = true;
        // console.log("App API initialized");
        checkFullInitAndStartMain();
    };
    const callbackOauth = () => {
        oauthLoaded = true;
        // console.log("Oauth initialized");
        checkFullInitAndStartMain();
    };

    gapi.client.load('status', 'v1', callbackApp, apiRoot);
    gapi.client.load('oauth2', 'v2', callbackOauth);
}

export function init() {
    // console.log("Initializing APIs");
    initialize(apiRoot);
}