# Seosong - Sistema de Playlist de Música

**Slogan:** Sua trilha. Seu momento.  
**Versão:** 2.0  
**Tipo de Sistema:** Plataforma Web para Gerenciamento e Reprodução de Músicas

---

## 1. Visão Geral

O **Seosong Integrado V2** é um sistema de gerenciamento de músicas que permite aos usuários pesquisar músicas, criar playlists, favoritar músicas e gerenciar seu perfil. O objetivo do projeto é oferecer uma plataforma simples, organizada e intuitiva para armazenamento e organização de músicas.

---

## 2. Objetivos

- Organizar músicas em um banco de dados.
- Permitir pesquisas rápidas por músicas, artistas e gêneros.
- Gerenciar usuários com autenticação.
- Criar playlists personalizadas.
- Favoritar músicas.
- Cadastrar artistas e músicas.

---

## 3. Funcionalidades do Sistema

### 3.1 Cadastro de Usuários
Permite criar contas para acesso ao sistema.

- Cadastro
- Login
- Alteração de dados
- Atualização de foto
- Recuperação de senha (futura implementação)

### 3.2 Buscador de Músicas
Permite localizar músicas através de filtros simples.

**Filtros disponíveis:**
- Nome da música
- Nome do artista
- Gênero
- Álbum
- Ano

### 3.3 Favoritos
Cada usuário pode adicionar músicas aos seus favoritos.

- Adicionar favorito
- Remover favorito
- Listar favoritos

### 3.4 Playlists
Permite criar listas personalizadas de músicas.

- Criar playlist
- Editar playlist
- Excluir playlist
- Adicionar músicas
- Remover músicas

### 3.5 Cadastro de Músicas
Permite cadastrar novas músicas contendo:

- Nome
- Artista
- Álbum (somente o nome, como texto)
- Gênero
- Ano
- Duração
- Foto

### 3.6 Cadastro de Artistas
Permite registrar artistas ou bandas.

- Nome
- Descrição
- Data de formação
- Foto

---

## 4. Requisitos Funcionais (RF)

| Código | Descrição |
| :--- | :--- |
| **RF01** | O sistema deve permitir cadastro de usuários. |
| **RF02** | O sistema deve permitir login. |
| **RF03** | O sistema deve permitir editar perfil. |
| **RF04** | O sistema deve permitir cadastrar músicas. |
| **RF05** | O sistema deve permitir cadastrar artistas. |
| **RF06** | *(Removido - Álbum não é mais uma entidade)* |
| **RF07** | O sistema deve permitir pesquisar músicas. |
| **RF08** | O sistema deve permitir criar playlists. |
| **RF09** | O sistema deve permitir adicionar músicas às playlists. |
| **RF10** | O sistema deve permitir remover músicas das playlists. |
| **RF11** | O sistema deve permitir favoritar músicas. |
| **RF12** | O sistema deve listar os favoritos do usuário. |

---

## 5. Requisitos Não Funcionais (RNF)

- Interface responsiva
- Banco de dados relacional
- Sistema seguro com criptografia de senhas
- Upload de imagens
- Boa performance nas pesquisas
- Compatibilidade com dispositivos móveis

---

## 6. Entidades do Sistema

### Usuário
Representa uma pessoa cadastrada no Seosong.

- `id` (Identificador único)
- `email` (E-mail)
- `senha`
- `nome`
- `dataNascimento`
- `cep`
- `foto` (Avatar)

---

### Música
Representa uma música cadastrada no sistema.

- `id`
- `nomeMusica` (Nome da música)
- `artista` (Relacionamento com a entidade Artista)
- `album` **(Atenção: agora é apenas um texto/String com o nome do álbum)**
- `ano`
- `duracao`
- `genero`
- `foto` (Capa da música)

---

### Artista
Representa um artista solo ou uma banda.

- `id`
- `nomeArtista`
- `descricao` (Biografia)
- `dataFormacao`
- `foto`

---

### Playlist
Representa uma coleção de músicas criada por um usuário.

- `id`
- `nomePlaylist`
- `usuario` (Relacionamento com o dono da playlist)
- `foto` (Capa da playlist)

---

### Favorito
Entidade associativa que representa o relacionamento **N:N** entre **Usuário** e **Música**.

- `id`
- `usuario` (Quem favoritou)
- `musica` (O que foi favoritado)

---

## 7. Modelagem do Banco de Dados

### Tabela: `usuario`

| Campo           | Tipo         |
| --------------- | ------------ |
| id              | BIGINT (PK)  |
| email           | VARCHAR(150) |
| senha           | VARCHAR(255) |
| nome            | VARCHAR(100) |
| data_nascimento | DATE         |
| cep             | VARCHAR(10)  |
| foto            | VARCHAR(255) |

---

### Tabela: `artista`

| Campo         | Tipo         |
| ------------- | ------------ |
| id            | BIGINT (PK)  |
| nome_artista  | VARCHAR(150) |
| descricao     | TEXT         |
| data_formacao | DATE         |
| foto          | VARCHAR(255) |

---

### Tabela: `musica`

| Campo       | Tipo         |
| ----------- | ------------ |
| id          | BIGINT (PK)  |
| artista_id  | BIGINT (FK)  |
| album       | VARCHAR(150) **(Agora é apenas texto)** |
| nome_musica | VARCHAR(150) |
| genero      | VARCHAR(50)  |
| ano         | INT          |
| duracao     | TIME         |
| foto        | VARCHAR(255) |

---

### Tabela: `playlist`

| Campo         | Tipo         |
| ------------- | ------------ |
| id            | BIGINT (PK)  |
| nome_playlist | VARCHAR(100) |
| usuario_id    | BIGINT (FK)  |
| foto          | VARCHAR(255) |

---

### Tabela: `playlist_musica`
(Tabela de associação N:N entre Playlist e Música)

| Campo       | Tipo |
| ----------- | ---- |
| playlist_id | BIGINT (FK) |
| musica_id   | BIGINT (FK) |
| *(PK composta)* | |

---

### Tabela: `favorito`

| Campo      | Tipo |
| ---------- | ---- |
| id         | BIGINT (PK) |
| usuario_id | BIGINT (FK) |
| musica_id  | BIGINT (FK) |

---

## 8. Relacionamentos e Cardinalidades

- **Usuário (1) : Playlist (N)**  
  Um usuário pode criar várias playlists. Uma playlist pertence a um único usuário.

- **Artista (1) : Música (N)**  
  Um artista pode ter várias músicas. Uma música pertence a um único artista.

- **Playlist (N) : Música (N)** *(via `playlist_musica`)*  
  Uma playlist pode conter várias músicas. Uma música pode estar em várias playlists.

- **Usuário (N) : Música (N)** *(via `favorito`)*  
  Um usuário pode favoritar várias músicas. Uma música pode ser favoritada por vários usuários.

**Obs.:** A entidade **Álbum** foi removida oficialmente do escopo. Agora, o álbum é apenas um atributo descritivo da música.

---

## 9. Operações CRUD

| Entidade  | Create | Read | Update | Delete |
| --------- | :----: | :--: | :----: | :----: |
| Usuário   |   ✅   |  ✅  |   ✅   |   ✅   |
| Música    |   ✅   |  ✅  |   ✅   |   ✅   |
| Artista   |   ✅   |  ✅  |   ✅   |   ✅   |
| Playlist  |   ✅   |  ✅  |   ✅   |   ✅   |
| Favorito  |   ✅   |  ✅  |   ❌   |   ✅   |

---

## 10. Fluxo Geral do Sistema

1. O usuário realiza o cadastro.
2. Efetua login na plataforma.
3. Pesquisa músicas por nome, artista, gênero ou álbum.
4. Visualiza os detalhes das músicas e artistas.
5. Favorita suas músicas preferidas.
6. Cria playlists personalizadas.
7. Adiciona e remove músicas das playlists.
8. Gerencia seus dados pessoais e foto de perfil.

---

## 11. Tecnologias Sugeridas

### Back-end
- Java 17+
- Spring Boot
- Spring Data JPA (Hibernate)
- API REST
- Maven ou Gradle

### Front-end
- HTML5
- CSS3
- JavaScript
- Bootstrap (para responsividade)

### Banco de Dados
- PostgreSQL

### Controle de Versão
- Git
- GitHub

---

## 12. Estrutura de Pastas (Sugestão)
