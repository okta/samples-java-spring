var path = require("path");

/**
 * GET /oauth2/v1/userinfo
 *
 * authorization: Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6InYzc3JycDJFb25JTVVpbHUxX0pEQmlMZ3FlTWFpZU02MkRHV1pUSldTYmcifQ.eyJ2ZXIiOjEsImp0aSI6IkFULk5idEhTZ0ExN01qQ1k3b1lHS0lZbFg0LUxUM2haaHpLVUgtdGg5eHNrR00iLCJpc3MiOiJodHRwOi8vcmFpbi5va3RhMS5jb206MTgwMiIsImF1ZCI6Imh0dHA6Ly9yYWluLm9rdGExLmNvbToxODAyIiwic3ViIjoiZ2VvcmdlQGFjbWUuY29tIiwiaWF0IjoxNTA4MjAzNjA3LCJleHAiOjE1MDgyMDcyMDcsImNpZCI6IjBvYW91M3dBQ004N1FpSXViMGczIiwidWlkIjoiMDB1b3cyZkhMSlZQWXc1SXgwZzMiLCJzY3AiOlsiZW1haWwiLCJwcm9maWxlIiwib3BlbmlkIl19.kIrhjiOGOftoNvC-guBfgk85SsHeyA6HEG4iDNHT1MK6lJzNre5rkOepKevrxtMTZW_wPXePoFejvNCbHk9maHVJFNbAGxU_Bw5ZDLGG1o13ffdpbjkW35hSTLhJrBAPrVDoeupWy1hFcJ8AWZ_DxtmIsLJTl_bsf585N0QiqW13zt8w-EwlE0M4LVgNBBQkcVA-ABaYBb0oCzVJlIfVZYa4zUiiK0_XgBi4CejKtp2rIU_F7ef19x1KZD3utfspM7cs6gAjFcR9SRKrspVgOg-iqxeu56iULlrHcGYspueGtHsLUCGE2Nubr2L7QogeLURRkx5CD_sO7eYWk82fRA
 * accept: application/json
 * user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:48.0) Gecko/20100101 Firefox/48.0
 * host: rain.okta1.com:1802
 * connection: keep-alive
 * accept-language: en-US
 * accept-encoding: gzip
 * cookie: 
 * cache-control: no-cache, no-store
 * pragma: no-cache
 */

module.exports = function (req, res) {
  res.statusCode = 200;

  res.setHeader("server", "Apache-Coyote/1.1");
  res.setHeader("x-okta-request-id", "reqFvGKCNbNTIOmpzU8zkk7Iw");
  res.setHeader("p3p", "CP=\"HONK\"");
  res.setHeader("set-cookie", ["sid=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/","JSESSIONID=63C69CA8EF9C737F8CD001892F1550A4; Path=/"]);
  res.setHeader("x-rate-limit-limit", "10000");
  res.setHeader("x-rate-limit-remaining", "9994");
  res.setHeader("x-rate-limit-reset", "1508203657");
  res.setHeader("cache-control", "no-cache, no-store");
  res.setHeader("pragma", "no-cache");
  res.setHeader("expires", "0");
  res.setHeader("content-type", "application/json;charset=UTF-8");
  res.setHeader("transfer-encoding", "chunked");
  res.setHeader("date", "Tue, 17 Oct 2017 01:26:46 GMT");

  res.setHeader("x-yakbak-tape", path.basename(__filename, ".js"));

  res.write(new Buffer("eyJzdWIiOiIwMHVvdzJmSExKVlBZdzVJeDBnMyIsIm5hbWUiOiJHZW9yZ2UgV2FzaGluZ3RvbiIsImxvY2FsZSI6ImVuLVVTIiwiZW1haWwiOiJnZW9yZ2VAYWNtZS5jb20iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJnZW9yZ2VAYWNtZS5jb20iLCJnaXZlbl9uYW1lIjoiR2VvcmdlIiwiZmFtaWx5X25hbWUiOiJXYXNoaW5ndG9uIiwiem9uZWluZm8iOiJBbWVyaWNhL0xvc19BbmdlbGVzIiwidXBkYXRlZF9hdCI6MTUwODE5MTM5MywiZW1haWxfdmVyaWZpZWQiOnRydWV9", "base64"));
  res.end();

  return __filename;
};
