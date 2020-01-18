# spi-project
The course project for Communication Systems Design 2019 at KTH. The project is prepared by SPI team.

# Team members:
1. Ahmed Beder
2. Akzharkyn Duisembiyeva
3. Adika Bintang Sulaeman
4. Abhishek Mishra
5. Bruno Duarte Coscia
6. Timur Kartaev

# Logs format
All logs collected during running of the whole project have the same format:
```
YYYY-MM-DD HH:mm:SS INFO  log_type time_difference
```

The logs can be found in root folder. These logs are collected from data collector (datacollector.log), client (client.log), group manager (groupmanager.log) . 

## Log types:
1. VERIFY_GS ->DC: this is for experiment to compare the verification speed of GS vs HY;  
2. VERIFY_HY ->DC: this is for experiment to compare the verification speed of GS vs HY;
3. REGISTER -> client: time spent to register on GM
4. REGISTER_CLIENT_GM ->client: time to spent to register on GM
5. REGISTER_LTCA -> bootstrapper: time to spent to register on NSS-PKI
4. SIGN_GS ->client: this is for experiment to compare the signing speed of GS vs HY;
5. SIGN_HY ->client: this is for experiment to compare the signing speed of GS vs HY;
6. N_CLIENTS -> GM: this is an expermient related to increasing/descreasing of number of clients;
7. CHECKSCHEME_HY ->GM: to log how fast the searching of hybrid scheme works
8. UPDATE_DB -> DC: the experiment on how fast the locations are added to the database

# Data plotting
The logs are parsed into CSV file using Fluent Bit. The data plotting will be done using plotspi.py.

# F.A.Q.

1. Why not mTLS for mutual authentication? 

Because the return x509 from NSS-PKI does not allow for TLS client authentication. Example error log:
```
sun.security.validator.ValidatorException: Extended key usage does not permit use for TLS client authentication
```

See this openssl issue: https://github.com/openssl/openssl/issues/1418

2. ...
