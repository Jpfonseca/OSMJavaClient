# ETSI OSM JAVA Wrapper

This repo contains two projects: 

1. `ETSI OSM SOL005 JAVA Wrapper`: present in the [master branch](https://github.com/Jpfonseca/OSMJavaClient/tree/master) which supports changes implemented in ETSI OSM 9 including the SOL006 Compability. It interfaces with the standardized SOL005 API

2. `ETSI OSM SOL005 NSMF Driver`: present in the [vsDriver branch](https://github.com/Jpfonseca/OSMJavaClient/tree/vsDriver) which allows for the communication with the [5GROWTH Vertical Slicer](https://github.com/5growth/5gr-vs).

## ETSI OSM SOL005 NSMF Driver
The descriptors and packages created to ne used with this tool can be found [here](https://github.com/ATNoG/osm-packages/tree/main/E2E_Interdomain%2BMTD).

The network slice templates as well as vertical service descriptors needed by the 5GROWTH Vertical Slicer can be found [here](https://github.com/5growth/5gr-pilots/tree/main/EFACEC_Vertical_Pilots/DynamicInterdomain_MTD)


## Maintanance Warning
This repo has not kept up with the changes implemented to ETSI OSM in newer releases.

<details>

<summary>Running the JAVA Wrapper</summary>
The JAVA Wrapper was idealized to be an alternative to the Official Python based ESTI OSM client. Therefore the wrapper is called a client in this repo.

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
</details>
