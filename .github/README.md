## COCOMU Back-End

### 💻 MVP

> MVP 개발 기간은 아래와 같습니다.

* 25.02.24 ~ 25.03.17

> MVP 개발 기간 동안 작업한 핵심 기능은 아래와 같습니다.

* CSR OAuth(Kakao, GitHub, Google) Login API
* Study CRUD API - 동적 쿼리 조회
* Coding Space CRUD API - 동적 쿼리 조회
* Code Execution/Submission API
* STOMP 기반 SSE 알림 서비스 구현
* Docker Sandbox 기반 Code Executor 구현
* Rabbit MQ 기반 API 서버와 Code Executor 간 통신 시스템 구축
* API 도메인 별 단위 테스트 작성

> MVP 기간 아래와 같이 DevOps 환경을 적용했습니다.

* S3 Storage를 통한 File Manage
* Front Web & Contents File CDN 적용
* Three Tier Subnet 구조를 통한 Infra 보안 적용
* ACM + Route 53을 활용한 SSL 적용
* Multiple AZ를 활용한 고가용성 인프라 적용
* Load Balancer를 활용한 도메인 기반 라우팅 적용
* Jenkins를 활용한 CI/CD 파이프라인 구축 및 자동화 적용
* JaCoCo를 통한 테스트 커버리지 측정 및 시각화 적용
* Discord 웹훅을 활용한 빌드 및 배포 결과 자동 알림 적용

--- 

### Tech Stack
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![RABBIT-MQ](https://img.shields.io/badge/-RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-0088cc?style=for-the-badge&logo=graphql)
![WebSocket](https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socket.io)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazonwebservices&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white)

### DevOps 아키텍쳐

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
  <img src="https://github.com/user-attachments/assets/bf26a14b-d392-4523-bafe-6ba675d21805" width="49%" />
  <img src="https://github.com/user-attachments/assets/abc28949-6a0d-4c4f-97b2-018611b66767" width="49%" />
</div>

### DevOps 기술 문서

- 다중 AZ를 채택한 이유가 무엇일까요? - [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#%EB%8B%A4%EC%A4%91-az%EB%A5%BC-%EC%B1%84%ED%83%9D%ED%95%9C-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%BC%EA%B9%8C%EC%9A%94)
- 3Tier Subnet을 채택한 이유가 무엇인가요? - [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#3tier-subnet%EC%9D%84-%EC%B1%84%ED%83%9D%ED%95%9C-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94)
- S3 Storage + CDN을 선택한 이유가 무엇인가요? - [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#s3-storage--cdn%EC%9D%84-%EC%84%A0%ED%83%9D%ED%95%9C-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94)
- HTTPS를 적용한 이유가 무엇인가요? - [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#https%EB%A5%BC-%EC%A0%81%EC%9A%A9%ED%95%9C-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94)
- 왜 CI를 도입하게 되었나요? - [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#%EC%99%9C-ci%EB%A5%BC-%EB%8F%84%EC%9E%85%ED%95%98%EA%B2%8C-%EB%90%98%EC%97%88%EB%82%98%EC%9A%94)
- 왜 CD를 도입하게 되었나요? - [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#%EC%99%9C-cd%EB%A5%BC-%EB%8F%84%EC%9E%85%ED%95%98%EA%B2%8C-%EB%90%98%EC%97%88%EB%82%98%EC%9A%94)
- Jenkins를 선택한 이유는 무엇인가요? - [링크 클릭](https://velog.io/@jihwankim128/COCOMU-Infra-Architecture#jenkins%EB%A5%BC-%EC%84%A0%ED%83%9D%ED%95%9C-%EC%9D%B4%EC%9C%A0%EB%8A%94-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94)



### Docs

[협업 강령](CONTRIBUTING.md)
[작업 내역](../docs/CHANGE_LOG.md)
