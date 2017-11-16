var path = require("path");

/**
 * GET /login/sessionCookieRedirect?checkAccountSetupComplete=true&token=201119VBOdo3arUfcmbfq2zur3k69Nv0JfPKx_8X1Fhv7Y01LWxnNEV&redirectUrl=http://127.0.0.1:7777/oauth2/v1/authorize/redirect?okta_key=PYH_HkL0S91TROH1o1zg131XqLGFrcqxYOm7bO7eSiU
 *
 * host: rain.okta1.com:1802
 * connection: keep-alive
 * user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:48.0) Gecko/20100101 Firefox/48.0
 * accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,* / *;q=0.8
 * accept-encoding: gzip
 * accept-language: en-US
 * cookie: JSESSIONID=B9CECE602E74D112D09308E13D0FD1D5; DT=DI0Yu__qM89SdmK1pl6HkvzZg
 * cache-control: no-cache, no-store
 * pragma: no-cache
 */

module.exports = function (req, res) {
  res.statusCode = 302;

  res.setHeader("server", "Apache-Coyote/1.1");
  res.setHeader("x-okta-request-id", "reqUyxS8ZbgQIKpkbclSSI-vg");
  res.setHeader("p3p", "CP=\"HONK\"");
  res.setHeader("set-cookie", ["sid=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/","t=default; Path=/","sid=1029qSGoOs0R4iImmxYTVbY5A;Version=1;Path=/;HttpOnly","proximity_dfa8e97427d06ca00bb2ebfb846168b8=\"4Y6QSMypBh+j2CaUQ3M7UnefA/1AxfffTse5k7w/jsb8+xXbz+Ct5fYjogg3xjpzgdYw7v2T68GCXYcw38X763yBmsmPxAHw2i+l3ys/f/w0qWD9VTDwku1AsGm+X0gHdXyMfgKzNBRwXu61bnecQDbBgnmZ9uEPo1LPq6ev6yUhDMh6KaMmh0+P5H3O3t+F\"; Version=1; Max-Age=31536000; Expires=Wed, 17-Oct-2018 01:26:46 GMT; Path=/","JSESSIONID=B9CECE602E74D112D09308E13D0FD1D5; Path=/"]);
  res.setHeader("x-rate-limit-limit", "10000");
  res.setHeader("x-rate-limit-remaining", "9997");
  res.setHeader("x-rate-limit-reset", "1508203657");
  res.setHeader("x-okta-backend", "albatross");
  res.setHeader("cache-control", "no-cache, no-store");
  res.setHeader("pragma", "no-cache");
  res.setHeader("expires", "0");
  res.setHeader("x-frame-options", "SAMEORIGIN");
  res.setHeader("location", "http://127.0.0.1:7777/oauth2/v1/authorize/redirect?okta_key=PYH_HkL0S91TROH1o1zg131XqLGFrcqxYOm7bO7eSiU");
  res.setHeader("content-language", "en");
  res.setHeader("content-length", "0");
  res.setHeader("date", "Tue, 17 Oct 2017 01:26:46 GMT");

  res.setHeader("x-yakbak-tape", path.basename(__filename, ".js"));

  res.end();

  return __filename;
};
