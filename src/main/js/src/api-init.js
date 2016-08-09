/* global gapi */

const apiRoot = '//' + window.location.host + '/_ah/api';
// let appLoaded = false;
// let oauthLoaded = false;

function initialize(apiRoot) {
    const callbackApp = () => {
        // appLoaded = true;
        console.log("App API initialized");
        // google.devrel.samples.hello.enableButtons();
        // google.devrel.samples.hello.signin(true,
        //     google.devrel.samples.hello.userAuthed);
        gapi.client.helloworld.greetings.listGreeting().execute(
            function (resp) {
                if (!resp.code) {
                    resp.items = resp.items || [];
                    resp.items.forEach((item) => {
                        console.log(item)
                    });
                }
            });
    };
    const callbackOauth = () => {
        // oauthLoaded = true;
        console.log("Oauth initialized");
    };

    gapi.client.load('helloworld', 'v1', callbackApp, apiRoot);
    gapi.client.load('oauth2', 'v2', callbackOauth);
};

export function init() {
    console.log("Initializing APIs");
    initialize(apiRoot);
}