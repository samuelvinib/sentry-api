# Avaliação Java Back-end

## Descrição

Este projeto tem como objetivo demonstrar conhecimento em desenvolvimento backend utilizando Java, com a implementação de um sistema de gerenciamento de documentos. O sistema permite a criação, recuperação, atualização e remoção de documentos, armazenando arquivos no sistema de arquivos e registrando metadados no banco de dados.

## Tecnologias Utilizadas

- **Framework**: Spring Boot
- **Gerenciador de Dependências**: Maven
- **Arquitetura**: API Restful
- **Banco de Dados**: Postgresql
- **Segurança**: Spring Security com API Key
- **Log**: log4j
- **Documentação**: Swagger / Postman

---

## Instalação do Projeto

> **Requisitos:** Para rodar o projeto é necessário ter o docker instalado em sua máquina.

### Passo 1 - Clonar o Repositório

```bash
  git clone git@github.com:samuelvinib/sentry-api.git
  cd sentry-api
```

### Passo 2 -  Configurar e iniciar os containers

```bash
docker compose up -d --build
```

A API estará disponível em:

```bash
  http://localhost:8080
```

---

## Funcionalidades

### 1. Criar Documento
**Endpoint:** `POST /documentos`
- **Recebe:** Nome do documento e arquivo binário.
- **Ação:** Salva os metadados no banco e armazena o arquivo no sistema de arquivos.
- **Retorno:** Status e ID do documento.

### 2. Retornar Documento
**Endpoint:** `GET /documentos/{id}`
- **Recebe:** ID do documento.
- **Retorno:** Status e o arquivo armazenado.

### 3. Atualizar Documento
**Endpoint:** `PUT /documentos/{id}`
- **Recebe:** ID do documento e novo arquivo binário.
- **Retorno:** Status da operação e os dados atualizados.

### 4. Apagar Documento
**Endpoint:** `DELETE /documentos/{id}`
- **Recebe:** ID do documento.
- **Retorno:** Status da remoção.

---

## Recursos Adicionais

- **Segurança:** A API está protegida por API Key via Spring Security.
- **Logging:** Utiliza log4j para registro de eventos e erros.
- **Tarefa Agendada:** Uma rotina diária que registra no log o número total de arquivos e o tamanho total armazenado em bytes.
- **Documentação:** A API está documentada via Swagger e Postman.

---

## Documentação da API

Após iniciar a aplicação, a documentação da API pode ser acessada pelo Swagger em:

```bash
  http://localhost:8080/swagger-ui/index.html
```

Caso utilize Postman, importe o arquivo `postman_collection.json` localizado no repositório.

---

Este projeto foi desenvolvido para fins de avaliação, explorando as melhores práticas de desenvolvimento backend com Java e Spring Boot.

