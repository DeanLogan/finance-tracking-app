window.onload = () => {
    const fullUrl = window.location.href;

    console.log("Extracted URL:", fullUrl);

    extractAndSend(fullUrl);
};

function parseFragment(url) {
    const fragment = url.split('#')[1];
    if (!fragment) {
        console.error("No fragment found in the URL.");
        return null;
    }
    return fragment;
}

function extractKeys(fragment) {
    const params = new URLSearchParams(fragment);
    const code = params.get('code');
    const idToken = params.get('id_token');

    if (!code || !idToken) {
        console.error("Required parameters (code or id_token) are missing in the fragment.");
        return null;
    }
    console.log("code: "+code);
    console.log("idtoken: "+idToken);
    return { code, idToken };
}

function sendPostRequest(keys) {
    fetch('http://localhost:8080/ulsterbank/oauth/callback/extractcode', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'code': keys.code,
            'id_token': keys.idToken
        },
        credentials: 'include'
    })
    .then(response => {
        if (response.ok) {
            console.log("Request sent successfully.");
            return response.json();
        } else {
            throw new Error("Failed to send request: " + response.statusText);
        }
    })
    .then(data => console.log("Server response:", data))
    .catch(error => console.error("Error:", error));
}

function extractAndSend(url) {
    const fragment = parseFragment(url);
    if (!fragment) return;

    const keys = extractKeys(fragment);
    if (!keys) return;

    sendPostRequest(keys);
}