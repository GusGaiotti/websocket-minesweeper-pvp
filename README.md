# Multiplayer Minesweeper

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)

Projeto de Campo Minado Multiplayer desenvolvido com arquitetura híbrida (REST + WebSocket) para partidas em tempo real. Este projeto é uma **POC (Proof of Concept)** focada no estudo e implementação de **WebSockets** utilizando o protocolo **STOMP**.

<img width="360" height="360" alt="image" src="https://github.com/user-attachments/assets/70379544-fd77-4f21-aca0-94264d8245af" />
<img width="410" height="410" alt="image" src="https://github.com/user-attachments/assets/f84f3826-105b-4fe6-9536-6ba5f20b0225" />


## Sobre o Projeto

O sistema permite que dois jogadores compitam em uma mesma partida simultaneamente. O backend atua como o motor do jogo, garantindo que a lógica e a posição das minas sejam validadas antes de qualquer atualização visual no cliente.

### Funcionalidades Principais
* **Partidas Multiplayer:** Sincronização em tempo real via WebSockets (STOMP).
* **Segurança Híbrida:** Autenticação JWT para rotas REST e interceptadores para validação de canal WebSocket.
* **Lógica no Servidor:** Processamento de movimentos e algoritmos de revelação de células (Flood Fill) protegidos contra trapaças.
* **Consistência:** Controle de concorrência em memória.
    * *Cenário Real:* Em larga escala, utilizaria-se de outras estratégias para sincronizar o estado entre múltiplas instâncias.
---

##  Tecnologias

* **Backend:** Java 21, Spring Boot 3.4, Spring Security, WebSocket.
* **Frontend:** Next.js, React, TypeScript, Tailwind CSS, StompJS. (Em desenvolvimento)
* **Banco de Dados:** PostgreSQL.

---

## Como Executar

### 1. Banco de Dados
O projeto já conta com o arquivo `docker-compose.yml`. Para subir o PostgreSQL, utilize:

```bash
docker-compose up -d
```

### 2. Backend (API)
Certifique-se de ter o **JDK 21** e **Maven** instalados.
* O arquivo `src/main/resources/application.yaml` já contém as configurações padrão de conexão e segurança para desenvolvimento.
* Caso queira alterar a chave de criptografia, ajuste a propriedade `jwt.secret`.

```bash
./mvnw spring-boot:run
```

A aplicação iniciará em `http://localhost:8080`.

## Roadmap

- [x] Backend: Motor de Jogo.
- [x] Backend: Segurança JWT e Handshake WebSocket.
- [x] Infra: Dockerização do banco de dados.
- [ ] Frontend: Finalizar o desenvolvimento do Frontend.

---

**Gustavo Gaiotti**
