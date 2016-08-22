/* global gapi */

export function load() {
    const promise = new Promise(resolve => {
        gapi.client.status.greetings.listGreeting().execute(
            function (resp) {
                if (!resp.code) {
                    resp.items = resp.items || [];
                    resolve(resp.items);
                    // resp.items.forEach((item) => {
                    //     console.log(item)
                    // });
                }
            });
    });
    return promise;
}
