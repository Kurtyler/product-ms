#!/usr/bin/env bash

case "$1" in
	"dev")
        export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/productDb?autoReconnect=true'
        export SPRING_DATASOURCE_USERNAME='root'
        export SPRING_DATASOURCE_PASSWORD='pogiako12'
    ;;
esac