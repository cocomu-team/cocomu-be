## COCOMU Back-End

### 💻 MVP

> MVP 개발 기간은 아래와 같습니다.

* 25.02.24 ~ 25.03.17

--- 

> 코코무는 아래와 같은 기술 스택을 활용합니다.
> 
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![RABBIT-MQ](https://img.shields.io/badge/-RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-0088cc?style=for-the-badge&logo=graphql)
![WebSocket](https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socket.io)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazonwebservices&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white)

- 스프링 부트를 사용하는 이유가 무엇인가요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-%EA%B8%B0%EC%88%A0-%EC%8A%A4%ED%83%9D-%EC%84%A0%EC%A0%95#%EC%99%9C-spring-boot%EB%A5%BC-%EC%84%A0%ED%83%9D%ED%96%88%EB%8A%94%EA%B0%80--%ED%98%BC%EC%9E%90-%EB%B0%B1%EC%97%94%EB%93%9C%EB%A5%BC-%EB%A7%A1%EC%9C%BC%EB%A9%B0-%EC%96%BB%EC%9D%80-%EA%B2%B0%EB%A1%A0)
- Query DSL을 선택하는 이유가 무엇인가요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-%EA%B8%B0%EC%88%A0-%EC%8A%A4%ED%83%9D-%EC%84%A0%EC%A0%95#%EC%99%9C-querydsl%EC%9D%84-%EC%84%A0%ED%83%9D%ED%96%88%EB%8A%94%EA%B0%80---%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EC%97%90%EC%84%9C-%EB%8A%90%EB%82%80-%ED%95%84%EC%9A%94%EC%84%B1)
- Rabbit MQ는 왜 사용하나요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-%EA%B8%B0%EC%88%A0-%EC%8A%A4%ED%83%9D-%EC%84%A0%EC%A0%95#%EC%99%9C-rabbitmq%EB%A5%BC-%EB%8F%84%EC%9E%85%ED%96%88%EB%8A%94%EA%B0%80--private-subnet-%EA%B8%B0%EB%B0%98-%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98%EC%97%90%EC%84%9C-%EB%A9%94%EC%8B%9C%EC%A7%95-%EB%B0%A9%EC%8B%9D%EC%9D%84-%EC%84%A0%ED%83%9D%ED%95%9C-%EC%9D%B4%EC%9C%A0)
- Docker Sandbox가 무엇이고 왜 사용했나요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-%EA%B8%B0%EC%88%A0-%EC%8A%A4%ED%83%9D-%EC%84%A0%EC%A0%95#%EC%99%9C-docker-sandbox%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%96%88%EB%8A%94%EA%B0%80--%EC%95%88%EC%A0%84%ED%95%98%EA%B3%A0-%EC%9C%A0%EC%97%B0%ED%95%9C-%EC%BD%94%EB%93%9C-%EC%8B%A4%ED%96%89-%ED%99%98%EA%B2%BD%EC%9D%84-%EB%A7%8C%EB%93%A4%EA%B8%B0-%EC%9C%84%ED%95%B4)
- SSE가 아니라 STOMP로 알림을 처리하는 이유가 무엇인가요?  ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-%EA%B8%B0%EC%88%A0-%EC%8A%A4%ED%83%9D-%EC%84%A0%EC%A0%95#%EC%99%9C-stompwebsocket-%EA%B8%B0%EB%B0%98%EB%A5%BC-%EC%84%A0%ED%83%9D%ED%96%88%EB%8A%94%EA%B0%80--%EC%95%8C%EB%A6%BC%EA%B3%BC-%EC%B1%84%ED%8C%85%EC%9D%84-%EB%8F%99%EC%8B%9C%EC%97%90-%EA%B3%A0%EB%A0%A4%ED%95%9C-%EC%84%A4%EA%B3%84)

---

> MVP 개발 기간 동안 작업한 핵심 기능은 아래와 같습니다.

**핵심 기능**
* CSR OAuth(Kakao, GitHub, Google) Login API
* Study CRUD API - 동적 쿼리 조회
* Coding Space CRUD API - 동적 쿼리 조회
* Code Execution/Submission API
* STOMP 기반 SSE 알림 서비스 구현
* Docker Sandbox 기반 Code Executor 구현
* Rabbit MQ 기반 API 서버와 Code Executor 간 통신 시스템 구축
* API 도메인 별 단위 테스트 작성

**백엔드 기술 문서**

- 코드 실행기를 구현하는 방법은? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-%ED%95%B5%EC%8B%AC-%EA%B8%B0%EB%8A%A5#1-code-executor)
  - 동시성 이슈를 해결하고 병렬처리로 높은 성능의 코드 실행기
- 도메인 비즈니스 로직은 어떻게? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-%ED%95%B5%EC%8B%AC-%EA%B8%B0%EB%8A%A5#2-domain-business-logic)
  - JPA를 활용해 도메인 응집성을 높은 비즈니스 로직
- OAuth 로그인 어떻게? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-%ED%95%B5%EC%8B%AC-%EA%B8%B0%EB%8A%A5#3-oauth-20-%EB%A1%9C%EA%B7%B8%EC%9D%B8:~:text=%EA%B2%B0%EA%B3%BC%20(Result)-,3.%20OAuth%202.0%20%EB%A1%9C%EA%B7%B8%EC%9D%B8,-%EC%83%81%ED%99%A9%20(Situation))
  - 사용자 경험 개선과 높은 보안을 고려한 OAuth 로그인
- STOMP 기반 알림 서비스는 어떻게? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-%ED%95%B5%EC%8B%AC-%EA%B8%B0%EB%8A%A5#4-stomp-%EA%B8%B0%EB%B0%98-sse-%EC%95%8C%EB%A6%BC)
  - 보안을 고려한 알림 시스템

---

> MVP 기간 아래와 같이 DevOps 환경을 적용했습니다.

**DevOps 작업 목록**
* S3 Storage를 통한 File Manage
* Front Web & Contents File CDN 적용
* Three Tier Subnet 구조를 통한 Infra 보안 적용
* ACM + Route 53을 활용한 SSL 적용
* Multiple AZ를 활용한 고가용성 인프라 적용
* Load Balancer를 활용한 도메인 기반 라우팅 적용
* Jenkins를 활용한 CI/CD 파이프라인 구축 및 자동화 적용
* JaCoCo를 통한 테스트 커버리지 측정 및 시각화 적용
* Discord 웹훅을 활용한 빌드 및 배포 결과 자동 알림 적용

**DevOps 기술 문서**

- 다중 AZ를 채택한 이유가 무엇일까요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#%EB%8B%A4%EC%A4%91-az%EB%A5%BC-%EC%B1%84%ED%83%9D%ED%95%9C-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%BC%EA%B9%8C%EC%9A%94)
- 3Tier Subnet을 채택한 이유가 무엇인가요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#3tier-subnet%EC%9D%84-%EC%B1%84%ED%83%9D%ED%95%9C-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94)
- S3 Storage + CDN을 선택한 이유가 무엇인가요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#s3-storage--cdn%EC%9D%84-%EC%84%A0%ED%83%9D%ED%95%9C-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94)
- HTTPS를 적용한 이유가 무엇인가요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#https%EB%A5%BC-%EC%A0%81%EC%9A%A9%ED%95%9C-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94)
- 왜 CI를 도입하게 되었나요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#%EC%99%9C-ci%EB%A5%BC-%EB%8F%84%EC%9E%85%ED%95%98%EA%B2%8C-%EB%90%98%EC%97%88%EB%82%98%EC%9A%94)
- 왜 CD를 도입하게 되었나요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#%EC%99%9C-cd%EB%A5%BC-%EB%8F%84%EC%9E%85%ED%95%98%EA%B2%8C-%EB%90%98%EC%97%88%EB%82%98%EC%9A%94)
- Jenkins를 선택한 이유는 무엇인가요? ➡️ [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#jenkins%EB%A5%BC-%EC%84%A0%ED%83%9D%ED%95%9C-%EC%9D%B4%EC%9C%A0%EB%8A%94-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94)

---

### 코코무 기타 문서

- 코코무 백엔드의 협업 강령입니다. ➡️  [협업 강령](CONTRIBUTING.md)
