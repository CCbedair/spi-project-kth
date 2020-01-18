Group manager is responsible for managing the group key for the clients and updating the data collector server with the current group public key to verify clients. 

## API endpoints:

### Request
 
```https
GET /keys
```
This endpoint is meant to be only accessed by the DC. It will provide the Group Public Key required for all
data authentication.

### Response

__200 OK__
```javascript
{
  "timestamp" : <long>,
  "gpk" : <Crypto Group Public Key>,
  "x509": <string>,
   "messageSignature": <string>
}
```

The `timestamp` attribute contains a unix timestamp of the sent request, and it is used in logging.

The `gpk` attribute describes the group public key, and it is implemented in the crypto package.

The `x509` attribute contains the GM server's x509 certificate and it is obtained from the pki.

The `messageSignature` is a signature of body components using SHA256withECDSA in base64 encoding.


__500 Internal Server Error__
```javascript
{
  "message" : <string>
}
```

The `message` can take any of the following values, and represents errors that should not take place on the server
under normal circumstances:
- {"message": "Bad Key Spec/Algo"}
- {"message": "Invalid Key/Signature"}
- {"message": "Failed to read file!"}

### Request

```https
POST /register
```

This endpoint is meant to be used by Clients. This endpoint provides a trusted client with the group public key and a
group secret key.

```javascript
{
  "timestamp" : <long>,
  "x509": <string>
}
```
The `timestamp` attribute contains a unix timestamp of the sent request, and it is used in logging.

The `x509` attribute contains the Client's x509 certificate and it is obtained from the pki. This certificate is used to
determine if the client is trusted and whether the client has previously registered.

### Response

__200 OK__
```javascript
{
  "timestamp" : <long>,
  "gpk": <Crypto Group Public Key>,
   "gsk": <Crypto Group Secret Key>,
  "x509": <string>
}
```

The `timestamp` attribute contains a unix timestamp of the sent request, and it is used in logging.

The `gpk` attribute describes the group public key, and it is implemented in the crypto package.

The `gsk` attribute describes the group secret key, and it is implemented in the crypto package.

The `x509` attribute contains the GM server's x509 certificate and it is obtained from the pki.

__403 Unauthorized__
```javascript
{
  "message" : "Client already registered"
}
```

__404 Unauthorized__
```javascript
{
  "message" : "Client Limit Reached. Resource No longer available."
}
```

__500 Internal Server Error__
```javascript
{
  "message" : <string>
}
```

The `message` can take any of the following values, and represents errors that should not take place on the server
under normal circumstances:
- {"message": "Bad Key Spec/Algo"}
- {"message": "Invalid Key/Signature"}
- {"message": "Failed to read file!"}


## How to run it:
From the spi-project directory, run the following:
```
docker build -f group-manager-server/Dockerfile -t gm-server-image .
docker run -e N_CLIENT=100 -p 9999:9999 --rm --name gm-server-container gm-server-image
```

In the example above, the `-e N_CLIENT=100` is the number of clients in the group.

## Lombok plugin:
The code uses very handy @Getter and @Setter annotation from Lombok to avoid the huge and useless lines of code. To make @Getter and @Setter annotations work, you need to:
1. Enable annotation in Compiler settings
2. Install the plugin at Intellij Idea.