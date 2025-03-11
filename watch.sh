#!/bin/bash

# Variável para armazenar o PID do processo Java
JAVA_PID=0

run_spring_boot() {
  echo "$(date) - Iniciando a aplicação Spring Boot..."
  if [ $JAVA_PID -ne 0 ]; then
    echo "$(date) - Matando o processo Java antigo (PID: $JAVA_PID)..."
    kill $JAVA_PID
  fi

  nohup java -jar /app/target/api-0.0.1-SNAPSHOT.jar &
  JAVA_PID=$!

  echo "$(date) - Aplicação Spring Boot iniciada (PID: $JAVA_PID)."
}

# Verificar se o .jar existe, se não, compilar o projeto
if [ ! -f /app/target/api-0.0.1-SNAPSHOT.jar ]; then
  echo "$(date) - Arquivo .jar não encontrado, gerando o arquivo .jar..."
  ./mvnw clean install -DskipTests
  if [ $? -ne 0 ]; then
    echo "$(date) - Erro ao compilar o projeto."
    exit 1
  fi
fi

# Rodar o Spring Boot na primeira execução
run_spring_boot

# Monitorar alterações no código fonte e nos arquivos do Maven (pom.xml, etc.)
while true; do
  # Monitorando arquivos .java e pom.xml
  inotifywait -r -e modify,create,delete --exclude '.*\.swp|.*\.git' ./src ./pom.xml

  # Quando houver uma mudança, rodar a compilação
  echo "$(date) - Alterações detectadas, recompilando o projeto..."
  ./mvnw clean install -DskipTests
  if [ $? -ne 0 ]; then
    echo "$(date) - Erro ao compilar o projeto."
    continue
  fi

  # Aguarda alguns segundos para garantir que o arquivo .jar esteja pronto
  sleep 5

  # Rodar o Spring Boot novamente
  run_spring_boot
done