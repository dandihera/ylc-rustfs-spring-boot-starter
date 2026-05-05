# 저장소 가이드라인

## 프로젝트 구조 및 모듈 구성

이 저장소는 RustFS 객체 스토리지를 위한 Gradle 기반 Java 17 Spring Boot Starter입니다. 주요 소스 코드는 `src/main/java/com/cqcloud/platform/` 아래에 있습니다.

- `config/`: RustFS 속성, 템플릿, Knife4j 설정
- `controller/`, `service/`, `service/impl/`: 파일 작업을 위한 HTTP 및 비즈니스 로직
- `mapper/` 및 `src/main/resources/mapper/`: MyBatis-Flex 매퍼 인터페이스와 XML SQL 매핑
- `entity/`, `dto/`, `vo/`: 영속성 모델, 요청 DTO, 응답/뷰 객체
- `utils/` 및 `exception/`: 공통 결과 래퍼와 도메인 예외 타입

현재 `src/test` 트리는 없습니다. 동작을 변경할 때는 동일한 패키지 구조로 `src/test/java` 아래에 테스트를 추가하세요.

## 빌드, 테스트, 개발 명령

- `./gradlew compileJava`: 소스를 컴파일하고 어노테이션 처리를 검증합니다.
- `./gradlew test`: 테스트가 있으면 실행합니다.
- `./gradlew format`: `build.gradle`에 설정된 Spring Java Format 규칙을 적용합니다.
- `./gradlew build`: Starter JAR, sources JAR, Javadocs를 빌드하고 검사를 실행합니다.
- `./gradlew publish`: Central 인증 정보와 GPG 서명이 설정된 경우 서명된 아티팩트를 게시합니다.

이 프로젝트는 독립 실행 애플리케이션이 아니라 Starter 라이브러리입니다. 런타임 검증은 보통 샘플 Spring Boot 앱에서 이 라이브러리를 사용해 수행합니다.

## 코딩 스타일 및 네이밍 규칙

Java 17과 UTF-8을 사용합니다. 포맷터가 관리하는 탭을 포함해 Spring Java Format을 따르세요. 패키지는 `com.cqcloud.platform` 아래에 유지합니다. 클래스는 `PascalCase`, 메서드와 필드는 `camelCase`를 사용하고, `SysFileSelDto`, `SysFileVo` 같은 기존 접미사 패턴을 따릅니다.

MyBatis XML statement ID는 매퍼 메서드 이름과 일치시킵니다.

## 테스트 가이드라인

새 테스트에는 JUnit 5와 Spring Boot 테스트 관례를 사용합니다. 테스트 클래스는 대상 클래스 이름을 기준으로 작성하세요. 예: `RustfsTemplateTest`. 스토리지 동작은 테스트가 외부 서비스 요구사항을 명시하지 않는 한 S3/RustFS 클라이언트를 목 처리합니다.

변경 제출 전 `./gradlew test`를 실행하세요. 의존성, 패키징, 게시 관련 변경이라면 `./gradlew build` 또는 `./gradlew publish`도 실행합니다.

## 커밋 및 Pull Request 가이드라인

최근 이력은 `refactor(storage): ...`, `chore(dependencies): ...`, `feat(config): ...` 같은 Conventional Commit 스타일을 사용합니다. `type(scope): summary` 형식을 선호하며, 요약은 변경 내용에 맞게 간결한 한국어, 영어 또는 중국어로 작성하세요.

Pull Request에는 목적, 영향받는 모듈, 실행한 검증 명령, 설정 또는 마이그레이션 참고사항을 포함하세요. 관련 이슈가 있으면 연결합니다. API 또는 설정이 바뀌면 `README.md`도 업데이트하세요.

## 보안 및 설정 팁

실제 RustFS 엔드포인트, 액세스 키, 시크릿 키, 버킷 이름, 서명 자료를 커밋하지 마세요. 예시는 명확히 가짜 값으로 유지합니다. Central 게시 및 GPG 설정은 로컬 또는 CI 비밀값으로 취급하세요.
