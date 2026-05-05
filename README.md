## 독립형 객체 스토리지 Spring Boot Starter

RustFS 객체 스토리지를 Spring Boot 애플리케이션에서 사용할 수 있도록 제공하는 Starter입니다.

참고 글: https://blog.csdn.net/weimeilayer/article/details/149197572?spm=1011.2415.3001.5331

## 의존성 추가

Gradle:

```groovy
implementation 'cloud.cqcloud.platform:ylc-rustfs-spring-boot-starter:1.0.5'
```

Maven:

```xml
<dependency>
    <groupId>cloud.cqcloud.platform</groupId>
    <artifactId>ylc-rustfs-spring-boot-starter</artifactId>
    <version>1.0.5</version>
</dependency>
```

## 빌드

```bash
./gradlew build
```

Spring Java Format을 적용하려면 다음 명령을 실행합니다.

```bash
./gradlew format
```

## x86 이미지

```bash
docker pull registry.cn-hangzhou.aliyuncs.com/qiluo-images/rustfs:latest
```

## ARM 아키텍처 이미지

```bash
docker pull registry.cn-hangzhou.aliyuncs.com/qiluo-images/linux_arm64_rustfs:latest
```

## 컨테이너 실행

```bash
docker run -d \                         # 백그라운드 실행
  --name rustfs \                       # 컨테이너 이름
  --restart=always \                    # 자동 재시작
  --privileged=true \                   # 권한 모드
  -p 10087:9000 \                       # API 포트
  -p 10088:9001 \                       # 웹 콘솔 포트
  -e RUSTFS_ACCESS_KEY=rustfsadmin \    # 액세스 키
  -e RUSTFS_SECRET_KEY=rustfsadmin \    # 시크릿 키
  -v /data/rustfs/data:/data \          # 데이터 영속화
  registry.cn-hangzhou.aliyuncs.com/qiluo-images/rustfs:latest
```

## 배포 스크립트

```bash
#!/bin/bash
# deploy-rustfs-production.sh

set -e  # 오류 발생 시 종료

echo "=== RustFS 운영 환경 배포 ==="

# 설정값
CONTAINER_NAME="rustfs"
API_PORT="10087"
CONSOLE_PORT="10088"
DATA_DIR="/data/rustfs"
IMAGE="registry.cn-hangzhou.aliyuncs.com/qiluo-images/rustfs:latest"

# 1. 강력한 비밀번호 생성
echo "1. 보안 키 생성 중..."
ACCESS_KEY=$(openssl rand -base64 32 | tr -dc 'a-zA-Z0-9' | head -c 20)
SECRET_KEY=$(openssl rand -base64 32 | tr -dc 'a-zA-Z0-9' | head -c 40)

# 키를 파일에 안전하게 저장
KEY_FILE="$DATA_DIR/.rustfs-keys"
mkdir -p $DATA_DIR
echo "RUSTFS_ACCESS_KEY=$ACCESS_KEY" > $KEY_FILE
echo "RUSTFS_SECRET_KEY=$SECRET_KEY" >> $KEY_FILE
chmod 600 $KEY_FILE

echo "키가 저장되었습니다: $KEY_FILE"
echo "액세스 키: $ACCESS_KEY"
echo "시크릿 키: $SECRET_KEY"

# 2. 기존 컨테이너 정리
echo "2. 기존 컨테이너 정리 중..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

# 3. 데이터 디렉터리 권한 설정
echo "3. 데이터 디렉터리 권한 설정 중..."
sudo mkdir -p $DATA_DIR
sudo chown -R 1000:1000 $DATA_DIR
sudo chmod -R 777 $DATA_DIR

# 4. 이미지 가져오기
echo "4. 이미지 다운로드 중..."
docker pull $IMAGE

# 5. 컨테이너 배포
echo "5. 컨테이너 배포 중..."
docker run -d \
  --name $CONTAINER_NAME \
  --restart=always \
  --privileged=true \
  -p $API_PORT:9000 \
  -p $CONSOLE_PORT:9001 \
  -v $DATA_DIR:/data \
  -e RUSTFS_ACCESS_KEY=$ACCESS_KEY \
  -e RUSTFS_SECRET_KEY=$SECRET_KEY \
  $IMAGE

# 6. 대기 후 검증
echo "6. 배포 검증 중..."
sleep 5

# 컨테이너 상태 확인
if docker ps | grep -q $CONTAINER_NAME; then
    echo "컨테이너가 정상 실행 중입니다"

    # 로그 확인
    LOG_OUTPUT=$(docker logs --tail 10 $CONTAINER_NAME)
    echo "컨테이너 로그:"
    echo "$LOG_OUTPUT"

    # 연결 테스트
    if curl -s http://localhost:$API_PORT > /dev/null 2>&1; then
        echo "API 서비스를 사용할 수 있습니다"
    else
        echo "API 서비스에 접근할 수 없습니다. 방화벽 설정을 확인하세요"
    fi
else
    echo "컨테이너 시작에 실패했습니다"
    docker logs $CONTAINER_NAME
    exit 1
fi

echo ""
echo "=== 배포 완료 ==="
echo "서비스 정보:"
echo "  API 엔드포인트: http://$(hostname -I | awk '{print $1}'):$API_PORT"
echo "                  http://localhost:$API_PORT"
echo "  웹 콘솔: http://$(hostname -I | awk '{print $1}'):$CONSOLE_PORT/rustfs/console/index.html"
echo "           http://localhost:$CONSOLE_PORT/rustfs/console/index.html"
echo "  Access Key: $ACCESS_KEY"
echo "  Secret Key: $SECRET_KEY"
echo ""
echo "관리 명령:"
echo "  상태 확인: docker ps | grep $CONTAINER_NAME"
echo "  로그 확인: docker logs -f $CONTAINER_NAME"
echo "  컨테이너 접속: docker exec -it $CONTAINER_NAME sh"
echo "  서비스 중지: docker stop $CONTAINER_NAME"
echo "  서비스 재시작: docker restart $CONTAINER_NAME"
echo "  서비스 제거: docker stop $CONTAINER_NAME && docker rm $CONTAINER_NAME"
echo ""
echo "키 백업:"
echo "  키 파일: $KEY_FILE"
echo "  키를 안전하게 보관하세요."
```

## PostgreSQL 테이블 생성

```sql
CREATE TABLE "public"."sys_file" (
"id" "pg_catalog"."varchar" COLLATE "pg_catalog"."default" NOT NULL,
"name" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"group_id" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"file_type" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"suffix" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"size" "pg_catalog"."int4",
"preview_url" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"storage_type" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"storage_url" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"bucket_name" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"object_name" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"visit_count" "pg_catalog"."int4",
"sort" "pg_catalog"."int4",
"remarks" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"gmt_create" "pg_catalog"."timestamp",
"gmt_modified" "pg_catalog"."timestamp",
"create_by" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"update_by" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"del_flag" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
"tenant_id" "pg_catalog"."int4",
"original" "pg_catalog"."varchar" COLLATE "pg_catalog"."default",
CONSTRAINT "sys_file_pkey" PRIMARY KEY ("id")
)
;

COMMENT ON COLUMN "public"."sys_file"."id" IS '기본 키';
COMMENT ON COLUMN "public"."sys_file"."name" IS '원본 파일명';
COMMENT ON COLUMN "public"."sys_file"."group_id" IS '그룹 번호, 다중 파일에 대응';
COMMENT ON COLUMN "public"."sys_file"."file_type" IS '파일 유형';
COMMENT ON COLUMN "public"."sys_file"."suffix" IS '파일 확장자';
COMMENT ON COLUMN "public"."sys_file"."size" IS '파일 크기, 단위는 바이트';
COMMENT ON COLUMN "public"."sys_file"."preview_url" IS '미리보기 주소';
COMMENT ON COLUMN "public"."sys_file"."storage_type" IS '스토리지 유형';
COMMENT ON COLUMN "public"."sys_file"."storage_url" IS '스토리지 주소';
COMMENT ON COLUMN "public"."sys_file"."bucket_name" IS '버킷 이름';
COMMENT ON COLUMN "public"."sys_file"."object_name" IS '버킷 내 파일명';
COMMENT ON COLUMN "public"."sys_file"."visit_count" IS '방문 횟수';
COMMENT ON COLUMN "public"."sys_file"."sort" IS '정렬값';
COMMENT ON COLUMN "public"."sys_file"."remarks" IS '비고';
COMMENT ON COLUMN "public"."sys_file"."gmt_create" IS '생성 시간';
COMMENT ON COLUMN "public"."sys_file"."gmt_modified" IS '수정 시간';
COMMENT ON COLUMN "public"."sys_file"."create_by" IS '생성자 ID';
COMMENT ON COLUMN "public"."sys_file"."update_by" IS '수정자 ID';
COMMENT ON COLUMN "public"."sys_file"."del_flag" IS '논리 삭제 플래그(0: 미삭제, null: 삭제)';
COMMENT ON COLUMN "public"."sys_file"."tenant_id" IS '소속 테넌트';
COMMENT ON COLUMN "public"."sys_file"."original" IS '원본 파일명';
COMMENT ON TABLE "public"."sys_file" IS '시스템 기본 정보 - 파일 관리 정보 테이블';
```

## YAML 설정 파일

암호화된 설정 예시:

```yaml
rustfs:
  endpoint: ENC(2ibwJTJtC9aSCwI+REN4up/bkWiPjWYei0XXqXv9dsD80cEkQ3BBbQ==)
  access-key: ENC(UbfMrajSAkV2JMRqVJdZTxwmQotPjhp9RZBjJ6ocd/4=)
  secret-key: ENC(ErfPLkmb/e6Bkq+4Yv9L/BnWkVmTtsMFnY03v0GgK9+LIbfZTcd0d2+6J8Pm5HJt)
  bucket-name: ENC(KWMCzT4HsuQ3owNp6xQs53qekQFGlFfmW8YLiz6g0ns=)
  public-bucket-name: ENC(NHW2QW2iwf2YEWtiC95nf3gK4UDvwobBUQAB6nHfaPw=)
  preview-domain: ENC(F1qPbzbrpnpftyLw1TENQ9aMzNVGX269TQLXtqiEKqYBZ7XUx1aQPQ==)
```

1.0.5 버전 설정 예시:

```yaml
rustfs:
  endpoint: http://192.168.1.100:10087
  access-key: minioadmin
  secret-key: minioadmin123
  bucket-name: app-private
  public-bucket-name: app-public
  preview-domain: https://static.example.com
```

## 설정 구조 설명

### RustFS 연결 설정

```yaml
rustfs:
  endpoint:       # RustFS 서버 주소
  access-key:     # 액세스 키
  secret-key:     # 시크릿 키
```

### 버킷 설정

```yaml
bucket-name:             # 기본 버킷, 일반적으로 비공개 파일용
public-bucket-name:      # 공개 접근 버킷, 일반적으로 공개 파일용
preview-domain:          # 파일 미리보기 도메인, CDN 주소일 수 있음
```

### 설정 항목 상세 설명

| 설정 항목 | 설명 | 예시 값 |
| --- | --- | --- |
| `endpoint` | RustFS 서비스 주소 | `http://rustfs.example.com:10087` |
| `access-key` | 액세스 키 ID | `AKIAIOSFODNN7EXAMPLE` |
| `secret-key` | 시크릿 액세스 키 | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY` |
| `bucket-name` | 기본 버킷 이름 | `private-bucket` |
| `public-bucket-name` | 공개 버킷 이름 | `public-bucket` |
| `preview-domain` | 파일 접근 도메인 | `https://cdn.example.com` |
