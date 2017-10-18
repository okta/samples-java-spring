var path = require("path");

/**
 * POST /oauth2/v1/token
 *
 * accept: application/json
 * content-type: application/x-www-form-urlencoded
 * cache-control: no-cache, no-store
 * pragma: no-cache
 * user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:48.0) Gecko/20100101 Firefox/48.0
 * host: rain.okta1.com:1802
 * connection: keep-alive
 * content-length: 192
 * accept-language: en-US
 * accept-encoding: gzip
 * cookie: 
 */

module.exports = function (req, res) {
  res.statusCode = 200;

  res.setHeader("server", "Apache-Coyote/1.1");
  res.setHeader("x-okta-request-id", "reqpjjlcCKmTIeXlLHkvUBi8A");
  res.setHeader("p3p", "CP=\"HONK\"");
  res.setHeader("set-cookie", ["sid=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/","JSESSIONID=F93F028A8E51266CB092C831B253046C; Path=/"]);
  res.setHeader("x-rate-limit-limit", "10000");
  res.setHeader("x-rate-limit-remaining", "9995");
  res.setHeader("x-rate-limit-reset", "1508203657");
  res.setHeader("cache-control", "no-cache, no-store");
  res.setHeader("pragma", "no-cache");
  res.setHeader("expires", "0");
  res.setHeader("content-type", "application/json;charset=UTF-8");
  res.setHeader("transfer-encoding", "chunked");
  res.setHeader("date", "Tue, 17 Oct 2017 01:26:46 GMT");

  res.setHeader("x-yakbak-tape", path.basename(__filename, ".js"));

  res.write(new Buffer("eyJhY2Nlc3NfdG9rZW4iOiJleUpoYkdjaU9pSlNVekkxTmlJc0ltdHBaQ0k2SW5ZemMzSnljREpGYjI1SlRWVnBiSFV4WDBwRVFtbE1aM0ZsVFdGcFpVMDJNa1JIVjFwVVNsZFRZbWNpZlEuZXlKMlpYSWlPakVzSW1wMGFTSTZJa0ZVTGs1aWRFaFRaMEV4TjAxcVExazNiMWxIUzBsWmJGZzBMVXhVTTJoYWFIcExWVWd0ZEdnNWVITnJSMDBpTENKcGMzTWlPaUpvZEhSd09pOHZjbUZwYmk1dmEzUmhNUzVqYjIwNk1UZ3dNaUlzSW1GMVpDSTZJbWgwZEhBNkx5OXlZV2x1TG05cmRHRXhMbU52YlRveE9EQXlJaXdpYzNWaUlqb2laMlZ2Y21kbFFHRmpiV1V1WTI5dElpd2lhV0YwSWpveE5UQTRNakF6TmpBM0xDSmxlSEFpT2pFMU1EZ3lNRGN5TURjc0ltTnBaQ0k2SWpCdllXOTFNM2RCUTAwNE4xRnBTWFZpTUdjeklpd2lkV2xrSWpvaU1EQjFiM2N5WmtoTVNsWlFXWGMxU1hnd1p6TWlMQ0p6WTNBaU9sc2laVzFoYVd3aUxDSndjbTltYVd4bElpd2liM0JsYm1sa0lsMTkua0lyaGppT0dPZnRvTnZDLWd1QmZnazg1U3NIZXlBNkhFRzRpRE5IVDFNSzZsSnpOcmU1cmtPZXBLZXZyeHRNVFpXX3dQWGVQb0ZlanZOQ2JIazltYUhWSkZOYkFHeFVfQnc1WkRMR0cxbzEzZmZkcGJqa1czNWhTVExoSnJCQVByVkRvZXVwV3kxaEZjSjhBV1pfRHh0bUlzTEpUbF9ic2Y1ODVOMFFpcVcxM3p0OHctRXdsRTBNNExWZ05CQlFrY1ZBLUFCYVlCYjBvQ3pWSmxJZlZaWWE0elVpaUswX1hnQmk0Q2VqS3RwMnJJVV9GN2VmMTl4MUtaRDN1dGZzcE03Y3M2Z0FqRmNSOVNSS3JzcFZnT2ctaXF4ZXU1NmlVTGxySGNHWXNwdWVHdEhzTFVDR0UyTnVicjJMN1FvZ2VMVVJSa3g1Q0Rfc083ZVlXazgyZlJBIiwidG9rZW5fdHlwZSI6IkJlYXJlciIsImV4cGlyZXNfaW4iOjM2MDAsInNjb3BlIjoiZW1haWwgcHJvZmlsZSBvcGVuaWQiLCJpZF90b2tlbiI6ImV5SmhiR2NpT2lKU1V6STFOaUlzSW10cFpDSTZJbXR2UTBwTlFXcG5ZWGsyVEZoMkxVcGtRV1pmZURWYWNVTm5kV2hvY21oR1ZHdHpkbHAyUTBaeFV6Z2lmUS5leUp6ZFdJaU9pSXdNSFZ2ZHpKbVNFeEtWbEJaZHpWSmVEQm5NeUlzSW01aGJXVWlPaUpIWlc5eVoyVWdWMkZ6YUdsdVozUnZiaUlzSW1WdFlXbHNJam9pWjJWdmNtZGxRR0ZqYldVdVkyOXRJaXdpZG1WeUlqb3hMQ0pwYzNNaU9pSm9kSFJ3T2k4dmNtRnBiaTV2YTNSaE1TNWpiMjA2TVRnd01pSXNJbUYxWkNJNklqQnZZVzkxTTNkQlEwMDROMUZwU1hWaU1HY3pJaXdpYVdGMElqb3hOVEE0TWpBek5qQTNMQ0psZUhBaU9qRTFNRGd5TURjeU1EY3NJbXAwYVNJNklrbEVMa1ZyWHpoSFFsWkdYMGd0UjFSamFYRmZiVWh5TVdWV1NFMXNSRzFRWkRaNFNsWmlUVWxIWXpWSlVGa2lMQ0poYlhJaU9sc2ljSGRrSWwwc0ltbGtjQ0k2SWpBd2IyOW1NV1pEUlhWSk1YUlBlalJqTUdjeklpd2ljSEpsWm1WeWNtVmtYM1Z6WlhKdVlXMWxJam9pWjJWdmNtZGxRR0ZqYldVdVkyOXRJaXdpWVhWMGFGOTBhVzFsSWpveE5UQTRNakF6TmpBMUxDSmhkRjlvWVhOb0lqb2lNazVhTTBZeFVUQmFNMWRSTFdvMFIyNW9XRkU0VVNKOS5GalRCTU03OHZ4U1NFcW5jTnNfSTBvdUd0ejRLTGV3c3d6MDI0eHVza1pmNEdhN2I1dXpjbkpQaWxORjlQNl9RcGdqTGEwNDBXNUFSdnhPb3V1YUFEVVlSU1liY0JKalR4Y3RfRkdCRWp2UkVuVk1LWkFwZ1VWT2xVZlRUczZiZEN2bDZ4Vjl1ZXVZeHNlc2V6NGItSlZrejdHOXg5TkczVDRJQ3p2Y3pxbVdwSmpsTEdBSUtWVmhfYWhHMmJQRkw0X2xVUUJxOXNfZWNPQy1sTmpTei1WR2pIV1UzR1ZVZnRGYWtxVE1FTmNVRkdUOHBpZXZ1Y0VlTzJVX1R5TTU3bzYtX21RQnI1NkRidmJfTXhRdUpMdjhzenFaNjNfNzM2MGdaR3hIbnlhMVBPOWNYc1lpZGVYdS14YkpVVXNwWFJ3TDVIZER1S2piRlBVZzllN0ZDY3cifQ==", "base64"));
  res.end();

  return __filename;
};
