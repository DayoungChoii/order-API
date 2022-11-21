# 주문 API 작성
Tech Stacks
- Spring Boot, Java, H2, JPA
- Git, Gradle, Intellij

## 작업 진행 순서
1. DB TABLE 설계
2. 엔티티 생성
3. 엔티티 매핑
4. 디비에 데이터 생성
5. 로직 작성
6. 테스트 로직 작성

## DB TABLE 설계
![스크린샷 2022-11-21 오후 12 01 00](https://user-images.githubusercontent.com/38481737/202982698-f094f02c-46b7-4df1-8609-f3781043d91f.png)
- 주문과 상품과의 관계가 N:N의 관계여서 order_product 테이블을 추가하여 N:1의 관계로 변경
- 상품과 창고와의 관계가 N:N의 관계여서 inventory 테이블을 추가하여 N:1의 관계로 변경

## N+1 해결을 위한 전략
1. 자주 사용되는 @xToOne 객체는 fetch join 처리
2. @xToMany 컬렉션 객체는 default_batch_fetch_size를 설정하여 사용할 때 한 번에 가져옴

## 구현한 API
- 주문 접수 처리: /api/orders/{id}/accept
- 주문 완료 처리: /api/orders/{id}/complete
- 단일 주문 조회: /api/orders/{id}
- 주문 목록 조회: /api/orders

## API 응답
- 입력, 응답은 엔티티를 직접 사용하지 않고 ~Request, ~Response, ~Dto 객체를 사용하여 처리
- 최종 응답은 다음과 같은 형식으로 이루어짐
```json
 "data": "",
 "success": "",
 "error": ""
```
- 응답 데이터는 data에 세팅, 성공 혹은 실패 여부는 success, 오류가 있을 경우 error에 세팅

## API 예제
### 주문 접수 처리
- 수량, 가격 맞지 않을 경우 예외 처리
- 주문한 수량만큼 inventory 감소(일단은 inventory 리스트에서 읽어오는 순서대로 재고 감소)  
http://localhost:8080/api/orders/1/accept (POST)  
RequestBody
```json
{
    "customerId": "5",
    "orderAcceptDtos": [
        {
            "productId": "9",
            "orderPrice": "200000",
            "orderCount": "2"
        },
        {
            "productId": "10",
            "orderPrice": "1000000",
            "orderCount": "2"
        },
        {
            "productId": "11",
            "orderPrice": "50000",
            "orderCount": "2"
        }
    ]
}
```
응답
```json
{
    "data": {
        "orderId": 1,
        "orderStatus": "ACCEPT",
        "orderDate": "2022-11-21T15:38:16.290373",
        "totalPrice": 2500000
    },
    "success": true,
    "error": null
}
```

### 주문 완료 처리
http://localhost:8080/api/orders/1/complete (Patch)  
응답
```json
{
    "data": null,
    "success": true,
    "error": null
}
```

### 단일 주문 조회
http://localhost:8080/api/orders/1 (Get)  
응답
```json
{
    "data": {
        "orderId": 1,
        "orderStatus": "COMPLETE",
        "orderDate": "2022-11-21T15:38:16.290373",
        "totalPrice": 2500000,
        "orderProductDtos": [
            {
                "productName": "keyboard",
                "price": 200000,
                "count": 2
            },
            {
                "productName": "monitor",
                "price": 1000000,
                "count": 2
            },
            {
                "productName": "mouse",
                "price": 50000,
                "count": 2
            }
        ]
    },
    "success": true,
    "error": null
}
```

### 주문 목록 조회
http://localhost:8080/api/orders (Get)  
RequestBody
```json
{
    "offset" : 0,
    "limit" : 10
}
```
응답
```json
{
    "data": [
        {
            "orderId": 1,
            "totalPrice": 2500000,
            "orderStatus": "COMPLETE",
            "orderDate": "2022-11-21T15:38:16.290373"
        },
        {
            "orderId": 2,
            "totalPrice": 2500000,
            "orderStatus": "ACCEPT",
            "orderDate": "2022-11-21T16:06:18.614621"
        }
    ],
    "success": true,
    "error": null
}
```




