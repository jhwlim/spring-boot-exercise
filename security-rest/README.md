## 스프링 시큐리티를 이용하여 REST API 구현하기

### API

- [로그인](#1-로그인)
- [Hello](#2-hello)
- [Access Token 갱신하기](#3-access-token-갱신하기)
- [로그아웃](#4-로그아웃)

### 1. 로그인
로그인을 성공하면 새로운 Refresh Token을 생성하여 DB에 저장한다.

#### Request
URL
```http request
POST /login
```
    
Parameter

| 이름       |설명|
|----------|---|
| username |계정이름|
| password |비밀번호|


#### Response

| 이름           | 설명                          |
|--------------|-----------------------------|
| token_type   | 토큰 타입(`Bearer`)             |
| access_token | 권한이 필요한 요청을 보낼 때 사용하는 토큰    |
|refresh_token| access_token을 갱신할 때 사용하는 토큰 
|

예시
```json
{
    "token_type": "Bearer",
    "access_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTY0NzMzMTkyMH0.mS9lnJrQ1DfxkVl2qWoeodEj4AIvO9tSgNz4EAjj8JxQf3MSvtNWFnNfjUmD6pauCKgEe5wSSW8UqwTBrDJZaA",
    "refresh_token": "5dcd3004-32c7-4b70-8235-48be1b8de75a"
}
```

#### Error

- 로그인 실패 : 401

### 2. Hello

로그인한 사용자만 요청이 가능하며, `Hello` 를 보낸다.

#### Request
```http request
GET /hello
Authorization: ${token_type} ${access_token}
```

#### Response
```
Hello
```

#### Error

- Authorization 헤더가 없는 경우 : 403
- Authorization 헤더가 유효하지 않은 경우 : 401

### 3. Access Token 갱신하기

로그인 성공시 발급받은 Refresh Token을 이용하여 Access Token을 갱신한다.

#### Request
URL

```http request
POST /token
```

Parameter

|이름|설명|
|---|---|
|access_token|만료된 access_token|
|refresh_token|로그인 성공할 때 받은 토큰|

#### Response

로그인 응답과 동일

#### Error

- Request Parameter 유효성 검사 실패 : 400
- 만료된 Access Token을 디코딩하는 과정에서 문제가 발생하는 경우 : 401 
- Request Parameter의 Refresh Token이 DB에 저장된 토큰과 일치하지 않는 경우 : 401

### 4. 로그아웃

로그인한 사용자만 요청이 가능하며, DB에 저장되어 있는 Refresh Token을 삭제한다.

#### Request
URL

```http request
POST /logout
Authorization: ${token_type} ${access_token}
```

#### Error

- Authorization 헤더가 없는 경우 : 403
- Authorization 헤더가 유효하지 않은 경우 : 401

#### Response

없음

### 구현 과정 및 느낀 점

#### 로그인

- 스프링 시큐리티의 `UsernamePasswordAuthenticationFilter`를 상속받아 직접 `attemptAuthentication()` 메서드를 구현함.
- 그런데 로그인에 대한 성공과 실패에 대한 응답을 REST API로 구현해야 하는데, `HttpServletResponse`를 이용하여 응답을 작성해야 했기 때문에 불편한 부분이 있었음.
- 그래서 스프링 시큐리티의 필터를 이용하여 로그인을 처리하는 것보다는 컨트롤러에서 구현하는 것이 편리할 것으로 생각됨.

#### Hello

- 권한이 있는 사용자만 접근하는지를 테스트하기 위한 API

#### Access Token 갱신하기

- Access Token은 Authorization 헤더에 토큰으로 보내는 방법을 생각해봤지만, `BasicAuthenticationFilter`가 만료된 토큰에 대해서 deny 하기 때문에 body에 토큰 정보를 실어서 보내도록 구현함. 
- 갱신한다(reissue)에 fit한 HttpMethod에 대해 고민함. 토큰을 가져온다는 관점에서 `GET`이 적절해보이나, `GET`으로 토큰 정보를 실어보내면 URL 길이 제한이나, 보안적인 측면에서 부적절하다고 판단하여 `POST`를 선택함. 👉 생각해보기! 
 
#### 로그아웃

- 로그인과 마찬가지로 처음에는 시큐리티의 handler와 successHandler를 이용하여 구현하려고 했으나, 아래의 이유로 컨트롤러에 구현함.
  1. `BasicAuthenticationFilter`(권한을 확인하는 필터)를 거치지 않기 때문에 `BasicAuthenticationFilter`에서 구현한 내용을 또 다시 작성해야함. (중복 발생)
  2. 로그아웃 실패를 처리하는 handler가 별도로 없음. (Exception Handling이 제한됨.)
- 로그인한(권한이 있는) 사용자만 로그아웃 요청을 가능하도록 함. 👉 생각해보기!

#### 테스트

- 처음에는 컨트롤러 단위에서 테스트를 작성하였지만(`@SpringBootTest`, `@AutoConfigureMockMvc`), `AuthJwtProviderTest`에서는 Mockito를 적용하여 테스트를 작성함.

### Next Step

- 관리자 기능
- redis 적용
