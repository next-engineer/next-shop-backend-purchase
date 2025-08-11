#!/bin/bash

PORT=8082

PID=$(lsof -t -i :$PORT)

if [ -n "$PID" ]; then
  echo "포트 $PORT 를 사용 중인 프로세스 PID: $PID 종료 중..."
  kill -9 $PID
  echo "프로세스 종료 완료."
else
  echo "포트 $PORT 는 사용 중이지 않습니다."
fi

echo "서버 실행 중..."
./gradlew bootRun

