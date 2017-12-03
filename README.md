# location-service
Simple stateless service exposing REST API for Google Geolocation API integration.

You can use our Docker image:
```
docker run -p 8080:8080 -e API_KEY={GOOGLE_API_KEY} codeheroes/location-service:0.5
```

Sample request:
```
curl -X POST \
  http://localhost:8080/location?Krakow+Kazimierza+Wielkiego \
  -H 'content-type: application/json'  
 ```
