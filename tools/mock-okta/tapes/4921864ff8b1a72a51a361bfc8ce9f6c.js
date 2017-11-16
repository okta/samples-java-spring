var path = require("path");

/**
 * GET /oauth2/v1/authorize?client_id=0oaou3wACM87QiIub0g3&redirect_uri=http://localhost:8080/login&response_type=code&scope=openid profile email&state=W0Y4WV
 *
 * host: rain.okta1.com:1802
 * connection: keep-alive
 * user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:48.0) Gecko/20100101 Firefox/48.0
 * accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,* / *;q=0.8
 * accept-encoding: gzip
 * accept-language: en-US
 * cache-control: no-cache, no-store
 * pragma: no-cache
 */

module.exports = function (req, res) {
  res.statusCode = 302;

  res.setHeader("server", "Apache-Coyote/1.1");
  res.setHeader("x-okta-request-id", "req0HF_RH1wQXOXkmalxcQScw");
  res.setHeader("p3p", "CP=\"HONK\"");
  res.setHeader("set-cookie", ["sid=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/","JSESSIONID=C245B854C7006EE672A30A43AAD4E5CF; Path=/","t=default; Path=/","DT=DI0w-VAje2vRb66PZPvGaKpug; Expires=Thu, 17-Oct-2019 01:26:37 GMT; Path=/","sid=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/","JSESSIONID=C245B854C7006EE672A30A43AAD4E5CF; Path=/"]);
  res.setHeader("x-rate-limit-limit", "10000");
  res.setHeader("x-rate-limit-remaining", "9999");
  res.setHeader("x-rate-limit-reset", "1508203657");
  res.setHeader("referrer-policy", "no-referrer");
  res.setHeader("x-okta-backend", "albatross");
  res.setHeader("cache-control", "no-cache, no-store");
  res.setHeader("pragma", "no-cache");
  res.setHeader("expires", "0");
  res.setHeader("location", "http://rain.okta1.com:1802/login/login.htm;jsessionid=C245B854C7006EE672A30A43AAD4E5CF?fromURI=%2Foauth2%2Fv1%2Fauthorize%2Fredirect%3Fokta_key%3DPYH_HkL0S91TROH1o1zg131XqLGFrcqxYOm7bO7eSiU");
  res.setHeader("content-language", "en");
  res.setHeader("content-length", "0");
  res.setHeader("date", "Tue, 17 Oct 2017 01:26:37 GMT");

  res.setHeader("x-yakbak-tape", path.basename(__filename, ".js"));

  res.end();

  return __filename;
};
