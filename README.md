# Quick Start

Before start, you need to:

1, Create a elasticsearch domain on aws, get vpc endpoint uri

2, Create a IAM user, generate access key and access secret

3, Set those values in application.yml

```
elastic-helper:
  aws:
    key: key
    secret: secret
  domain:
    serviceName: es
    region: region
    endPoint: endPoint
    type: type
```
