// okta widget configuration
window.oktaSignIn = new OktaSignIn( {
  baseUrl: 'https://dev-123456.oktapreview.com',
  clientId: 'YourClientId',
  redirectUri: window.location.href,
  authParams: {
    issuer: 'default',
    responseType: ['id_token', 'token'],
    scopes: ["openid", "profile", "email"]
  }
});

// the baseUrl of the resource server
window.resourceServerBaseUrl = "http://localhost:8000";
