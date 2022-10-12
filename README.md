# 2022 졸업프로젝트 - 혼밥 메이트

> 다대다 웹 화상채팅 서비스 https://github.com/qkrtjdrbs/bobmate

### 구현 동기

    최근 코로나 사태로 인해 비대면 서비스의 수요가 증가하면서 구현해보고 싶다는 생각이 들어서 만들게 되었습니다.

### 구현 목표

    별도의 프로그램을 설치하지 않고 웹상에서 다양한 방식(화상, 텍스트, 음성, 화면 공유)의 커뮤니케이션을 할 수 있는 서비스를 구현하는 것.

### 기본 동작

    서비스를 이용하기 위해서는 로그인이 필요합니다.

    소셜 계정으로 편리하게 로그인할 수 있습니다.

    특정 채팅 세션에 접속한 사용자들은 여러 방식으로 자유롭게 소통할 수 있습니다.

---

# 사용한 기술 스택

```
개인 프로젝트라서 모든 부분을 제가 개발했습니다.

크게 Thymeleaf + Spring Boot + Openvidu의 조합으로 개발했습니다.
```

> 사용한 기술스택은 다음과 같습니다.

- Thymeleaf (Frontend)
- Spring Boot (Backend)
- Openvidu (Media Server)
  ```
  5명 이상의 사용자들이 원활하게 통신하려면 미디어 서버의 존재가 필수적 입니다.
  Openvidu는 미디어 서버를 개발자에게 제공하며, WebRTC기술을 편하게 사용할 수 있는 모듈을 제공하는 플랫폼입니다.
  ```
- Docker
  ```
  미디어 서버를 Docker image로 제공하기 때문에 사용했습니다.
  ```
- Spring Security (Social Login)
- H2 Database (DB)
- Spring Data JPA (ORM)

---

# 프로젝트 구조도

> 배포 전 local 환경에서의 구조입니다.

![구조도(졸업)](https://user-images.githubusercontent.com/68425462/195347253-f0e6b1f6-de88-43d8-96c0-1032de6907ae.png)

---

# 패키지 구조

![패키지구조](https://user-images.githubusercontent.com/68425462/195347435-6e119a8b-cf33-41c6-ade5-4b7f8d860c36.png)

- config

  ```
  웹 소켓 통신을 위한 WebSocketConfig
  소셜 로그인을 위한 SecurityConfig
  ```

- mvc

  - controller

    ```
    홈 화면 관련 요청 HomeController
    초대받은 유저 처리 InviteController
    로그인 관련 요청 LoginController
    세션 초대 메시지 처리 InviteMessageHandler
    세션 생성, 참여 요청 처리 SessionController
    ```

  - repository

    > 영속성 context에 접근할 수 있는 Repository 계층

  - service

    > 비즈니스 로직을 수행해주는 Service 계층

- dto

  > 인자 전달을 편리하게 하기 위한 dto

- entity

  ```
  가입된 유저 entity User
  ```

---

# 1. 홈 화면

## 1-1. 비 로그인

> 로그인하지 않았을 때 홈화면 입니다.

![비로그인홈](https://user-images.githubusercontent.com/68425462/195347609-233a664b-49fd-4363-a2e7-188c2405a9b5.png)

- 로그인하지 않으면 채팅 세션에 참여할 수 없습니다.

- '로그인'이나 '이동'버튼을 클릭해서 로그인 창으로 갈 수 있습니다.

## 1-2. 구글 계정 로그인

> 구글 계정으로 로그인한 홈 화면입니다.

![홈](https://user-images.githubusercontent.com/68425462/195347688-4ac9fc77-8280-4053-81ae-aacf46f0209d.png)

- Navigation Bar에 로그인 계정 이름을 띄워줍니다.

- 채팅 세션 참여 form을 보여줍니다.

- 최초에는 임의로 별명과 방 제목을 생성해서 input 태그에 띄워줍니다.

- 별명과 방 제목을 입력후 'Join!' 버튼을 눌렀을 때

  - 해당 방 제목을 가진 세션이 있는 경우 그 세션에 참여합니다.
  - 해당 방 제목을 가진 세션이 없을 경우 새로 세션을 만들고 그 세션에 참여합니다.

- '로그아웃' 버튼을 클릭하면 현재 계정에서 로그아웃됩니다.

---

# 2. 로그인

> 사이트 내에서 만든 계정 또는 소셜 계정으로 로그인이 가능합니다.

![로그인](https://user-images.githubusercontent.com/68425462/195347745-4f65e7af-c71c-4f74-b505-31ddd15ce172.png)

---

# 3. 채팅 세션

![채팅세션](https://user-images.githubusercontent.com/68425462/195347802-e154eb0b-fd91-4f19-83b3-2fd59b85930a.png)

- '테스트'라는 제목의 채팅방입니다.

- 현재 '테스터1', '테스터2' 2명의 유저가 접속중 입니다.

- video 버튼을 통해 내 비디오를 on / off 합니다.

- audio 버튼을 통해 내 오디오를 on / off 합니다.

- 우측의 채팅창으로 텍스트 채팅을 할 수 있습니다.

- 하단의 썸네일을 누르면 그 유저의 화면을 크게 띄울 수 있습니다.

---

# 4. 채팅 세션 초대

## 4-1. 초대 하기

> 초대하는 유저의 입장입니다.

![초대하기](https://user-images.githubusercontent.com/68425462/195347952-bfd208f3-ec68-48ee-a5e9-cf9b72049290.PNG)

- '테스터2'라는 유저가 초대를 보낼 때 상황입니다.

- '초대하기' 버튼을 누르면 Modal창을 띄웁니다.

- 초대하고자 하는 유저의 닉네임을 기입하고 '초대'버튼을 누르면 해당 유저에게 초대 메시지가 전송됩니다.

## 4-2. 초대 받기

> 초대받는 유저의 입장입니다.

![초대받기1](https://user-images.githubusercontent.com/68425462/195348025-ff4eab30-73d8-41dd-863a-097272404561.png)

- 초대메시지가 올 경우 우측 하단에 메시지를 띄워줍니다.

- 해당 링크를 클릭하면 해당 채팅 세션에 참여할 수 있는 form으로 이동합니다.

![초대받기2](https://user-images.githubusercontent.com/68425462/195348099-a55ac0ae-217e-40b9-9682-89a4b4923677.png)

- 방 제목 input 태그의 value를 바꾸면 안되기 때문에 readonly 처리를 해주었습니다.

---

# 5. 화면 공유

> 자신의 컴퓨터 화면을 공유할 수 있습니다.

![화면공유](https://user-images.githubusercontent.com/68425462/195348168-b44169ba-0139-4e27-911a-82e710b1d9c8.PNG)

- 전체화면, 창, 크롬탭 중 원하는 방식으로 공유할 수 있습니다.

![화면공유2](https://user-images.githubusercontent.com/68425462/195348251-5c5a87fa-3540-4076-82ed-2f0f138fecc2.png)

- 인텔리제이 창을 공유하면 썸네일 목록에 공유 화면이 생성됩니다.
- 해당 화면을 클릭하면 크게 볼 수 있습니다.
