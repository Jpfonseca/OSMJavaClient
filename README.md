# OSMJavaClient

In order to run the client one needs to create a `pkcs12 trust store`

Obtaining OSM NBI private key:

```
wget -O privkey.pem "https://osm.etsi.org/gitweb/?p=osm/NBI.git;a=blob_plain;f=osm_nbi/http/privkey.pem;hb=refs/heads/v7.0"
```

Creaing the `pkcs12 trust store`:

```
openssl pkcs12 -export -out trust.pkcs12 -passout pass:000000 -in cert.pem -inkey privkey.pem 
```

Run the client and the tests with the following "flag"

```
-ea -Djavax.net.ssl.keyStoreType=PKCS12 -Djavax.net.ssl.trustStore=/home/planck/Desktop/5Growth/SimpleDriver/trust.pkcs12 -Djavax.net.ssl.trustStorePassword=000000 -Djdk.internal.httpclient.disableHostnameVerification
```
