Для удобства проверки работы можно использовать docker-compose:  
    1. После клонирования репозитория, в корне пропишите ```mvn clean package``` и ```mvn clean install```  
    2. Пропишите ```docker-compose up -d```  
Если gateway продолжает не видеть сервисы, попробуйте остановить его контейнер и перезапустить.  
Если вы хотите тестить с локальной машины, запускайте все c dev конфигурацией (надеюсь, везде ее поправил) 
В корне имеется папка Postman, в котором лежит json для тестов 
