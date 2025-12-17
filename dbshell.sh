#!/bin/bash
# Django의 dbshell과 동일한 역할

echo "Connecting to MySQL database..."
docker exec -it diary-mysql mysql -u diary_user -pdiary_password diary_db
