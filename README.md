# OpenSearch 기반 [보안 Data] 관리 DashBoard

## 📊 프로젝트 소개
이 프로젝트는 OpenSearch 기반의 **보안 데이터 관리**를 위한 대시보드 웹페이지 백엔드 서버입니다.  
로그인 및 회원가입, 권한 관리, 케이스 CRUD 등 다양한 기능을 제공하며, 효율적이고 안정적인 데이터 관리를 목표로 개발되었습니다.

---
## 🧑‍💻 백엔드 개발자 소개
- **박지은**: 경북대학교 컴퓨터학부(글로벌소프트웨어융합전공) 
- **한재준**: 경북대학교 컴퓨터학부(심화컴퓨팅전공) 

---

## 💡 주요 기능

- **사용자 인증 및 권한 관리**
  - jwt를 이용한 로그인 및 사용자 권한 제어
- **케이스 관리**
  - 케이스 생성, 조회, 수정, 삭제(CRUD) API 제공
  - 케이스 검색 기능 제공
- **Email 전송 서비스**
  - 비밀번호 변경 요청, 권한 삭제시 이메일 전송
- **데이터 관리**
  - 데이터 검색 및 필터링

---

## 💻 기술 스택

- **Backend Framework**: Spring Boot 3.3.4
- **Language**: Java 17
- **IDE**: IntelliJ 
- **ORM**: JPA
- **Database**: MySQL
- **Build Tool**: Gradle

---

## ⬇️ 설치 및  실행 방법

### 1. 프로젝트 클론 및  설정
GitHub 저장소를 로컬로 클론합니다.
```bash
git clone <repository-url>
cd <repository-folder>
```
### 2. 데이터 베이스 설정
MySQL을 설치하고 프로젝트에서 사용할 데이터 베이스인 caselist를 생성합니다.

### 3. .properties, .yml파일 추가
해당 프로젝트에 .properties, .yml파일을 추가합니다.

### 4. 빌드 
Gradle을 사용해 프로젝트를 빌드하고 실행합니다.
```bash
./gradlew build
```
빌드가 완료되면 build/libs 폴더에 *.jar 파일이 생성됩니다.

### 5. 백그라운드 실행
```bash
nohup -jar <build-file>.jar &
```
실행 로그는 nohup.out에서 확인할 수 있습니다.

### 6. java 프로세스 확인 및 종료
```bash
ps -e | grep java
```
실행 중인 프로세스 ID(PID)를 확인할 수 있습니다.
```bash
kill -9 <PID>
```
해당 프로세스를 종료할 수 있습니다.

### api 테스트
서버가 성공적으로 실행되면, 다음 URL로 API를 테스트할 수 있습니다.
- 기본 URL: http://localhost:8080
- Swagger 문서: http://localhost:8080/swagger-ui/index.html#







