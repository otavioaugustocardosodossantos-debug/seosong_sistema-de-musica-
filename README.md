# seosong_sistema-de-musica-
É um sistema de playlist de musica 

Funcionalidades do sistema:
- buscador de musicas(filtragem simples, mais podemos decidir) 
- favoritos 
- playliste 
- cadastro de usuario 
- adicionar musica

Entidades do sistema:
- usuario 
- musica 
- artista 
- album 
- playliste 
- favorito ( favorito user_musica )
  

Modelagem do banco de dados:
creat
updet

Usuario 
- id 
- email 
- senha 
- nome 
- data de nascimento
- cep 
- foto 

Musica
- id 
- id_artista
- album
- ano 
- duração 
- genero 
- nome_musica
- foto 

solo/banda
Artista
- id 
- nome_artista 
- descrição 
- data_formação 
- foto 

Playliste
- id 
- nome_playliste
- user_id
- foto 


# Documentação do Projeto #

# Projeto Seosong Integrado V2

## Documentação Técnica do Sistema

**Versão:** 2.0
**Nome do Projeto:** Seosong Integrado V2
**Tipo de Sistema:** Plataforma Web para Gerenciamento e Reprodução de Músicas

---

# 1. Visão Geral

O **Seosong Integrado V2** é um sistema de gerenciamento de músicas que permite aos usuários pesquisar músicas, criar playlists, favoritar músicas e gerenciar seu perfil.

O objetivo do projeto é oferecer uma plataforma simples, organizada e intuitiva para armazenamento e organização de músicas.

---

# 2. Objetivos

* Organizar músicas em um banco de dados.
* Permitir pesquisas rápidas.
* Gerenciar usuários.
* Criar playlists personalizadas.
* Favoritar músicas.
* Cadastrar artistas, álbuns e músicas.

---

# 3. Funcionalidades

## 3.1 Cadastro de Usuários

Permite criar contas para acesso ao sistema.

### Funcionalidades

* Cadastro
* Login
* Alteração de dados
* Atualização de foto
* Recuperação de senha (futura implementação)

---

## 3.2 Buscador de Músicas

Permite localizar músicas através de filtros.

### Filtros

* Nome da música
* Nome do artista
* Gênero
* Álbum
* Ano

---

## 3.3 Favoritos

Cada usuário pode adicionar músicas aos seus favoritos.

Operações:

* Adicionar favorito
* Remover favorito
* Listar favoritos

---

## 3.4 Playlists

Permite criar listas personalizadas.

Operações:

* Criar playlist
* Editar playlist
* Excluir playlist
* Adicionar músicas
* Remover músicas

---

## 3.5 Cadastro de Músicas

Administradores poderão cadastrar músicas contendo:

* Nome
* Artista
* Álbum
* Gênero
* Ano
* Duração
* Foto

---

## 3.6 Cadastro de Artistas

Permite registrar artistas ou bandas.

Informações:

* Nome
* Descrição
* Data de formação
* Foto

---

## 3.7 Cadastro de Álbuns

Cadastro dos álbuns relacionados aos artistas.

Informações:

* Nome
* Artista
* Ano de lançamento
* Capa

---

# 4. Requisitos Funcionais

## RF01

O sistema deve permitir cadastro de usuários.

## RF02

O sistema deve permitir login.

## RF03

O sistema deve permitir editar perfil.

## RF04

O sistema deve permitir cadastrar músicas.

## RF05

O sistema deve permitir cadastrar artistas.

## RF06

O sistema deve permitir cadastrar álbuns.

## RF07

O sistema deve permitir pesquisar músicas.

## RF08

O sistema deve permitir criar playlists.

## RF09

O sistema deve permitir adicionar músicas às playlists.

## RF10

O sistema deve permitir remover músicas das playlists.

## RF11

O sistema deve permitir favoritar músicas.

## RF12

O sistema deve listar os favoritos do usuário.

---

# 5. Requisitos Não Funcionais

* Interface responsiva
* Banco de dados relacional
* Sistema seguro
* Criptografia de senhas
* Upload de imagens
* Boa performance nas pesquisas
* Compatibilidade com dispositivos móveis

---

# 6. Entidades do Sistema

## Usuário

Representa os usuários cadastrados.

Atributos:

* id
* nome
* email
* senha
* data_nascimento
* cep
* foto

---

## Música

Representa cada música cadastrada.

Atributos:

* id
* nome_musica
* id_artista
* id_album
* genero
* ano
* duracao
* foto

---

## Artista

Representa artistas ou bandas.

Atributos:

* id
* nome_artista
* descricao
* data_formacao
* foto

---

## Álbum

Representa um álbum musical.

Atributos:

* id
* nome_album
* artista_id
* ano_lancamento
* capa

---

## Playlist

Lista personalizada criada por um usuário.

Atributos:

* id
* nome_playlist
* user_id
* foto

---

## Favorito

Relaciona usuários e músicas favoritas.

Atributos:

* id
* usuario_id
* musica_id

---

# 7. Modelagem do Banco de Dados

## Tabela: Usuario

| Campo           | Tipo         |
| --------------- | ------------ |
| id              | INT          |
| nome            | VARCHAR(100) |
| email           | VARCHAR(150) |
| senha           | VARCHAR(255) |
| data_nascimento | DATE         |
| cep             | VARCHAR(10)  |
| foto            | VARCHAR(255) |

---

## Tabela: Artista

| Campo         | Tipo         |
| ------------- | ------------ |
| id            | INT          |
| nome_artista  | VARCHAR(150) |
| descricao     | TEXT         |
| data_formacao | DATE         |
| foto          | VARCHAR(255) |

---

## Tabela: Album

| Campo          | Tipo         |
| -------------- | ------------ |
| id             | INT          |
| nome_album     | VARCHAR(150) |
| artista_id     | INT          |
| ano_lancamento | YEAR         |
| capa           | VARCHAR(255) |

---

## Tabela: Musica

| Campo       | Tipo         |
| ----------- | ------------ |
| id          | INT          |
| artista_id  | INT          |
| album_id    | INT          |
| nome_musica | VARCHAR(150) |
| genero      | VARCHAR(50)  |
| ano         | YEAR         |
| duracao     | TIME         |
| foto        | VARCHAR(255) |

---

## Tabela: Playlist

| Campo         | Tipo         |
| ------------- | ------------ |
| id            | INT          |
| nome_playlist | VARCHAR(100) |
| usuario_id    | INT          |
| foto          | VARCHAR(255) |

---

## Tabela: Playlist_Musica

Tabela responsável pela relação N:N entre playlists e músicas.

| Campo       | Tipo |
| ----------- | ---- |
| playlist_id | INT  |
| musica_id   | INT  |

---

## Tabela: Favorito

| Campo      | Tipo |
| ---------- | ---- |
| id         | INT  |
| usuario_id | INT  |
| musica_id  | INT  |

---

# 8. Relacionamentos

* Um usuário pode criar várias playlists (1:N).
* Uma playlist pode conter várias músicas (N:N).
* Uma música pertence a um artista (N:1).
* Um artista possui vários álbuns (1:N).
* Um álbum possui várias músicas (1:N).
* Um usuário pode favoritar várias músicas (N:N).

---

# 9. Operações CRUD

## Usuário

* Create
* Read
* Update
* Delete

---

## Música

* Create
* Read
* Update
* Delete

---

## Artista

* Create
* Read
* Update
* Delete

---

## Álbum

* Create
* Read
* Update
* Delete

---

## Playlist

* Create
* Read
* Update
* Delete

---

## Favoritos

* Adicionar favorito
* Listar favoritos
* Remover favorito

---

# 10. Fluxo Geral do Sistema

1. Usuário realiza cadastro.
2. Efetua login.
3. Pesquisa músicas.
4. Visualiza artistas e álbuns.
5. Favorita músicas.
6. Cria playlists.
7. Adiciona músicas às playlists.
8. Gerencia seu perfil.

---

# 11. Tecnologias Sugeridas

## Front-end

* HTML5
* CSS3
* JavaScript
* Bootstrap ( Ele é um framework (um conjunto de ferramentas e códigos prontos) de front-end criado com as linguagens HTML, CSS e JavaScript)

## Back-end

* java
* spring boot
* API REST
* 

## Banco de Dados

* PosgrelSQL

## Controle de Versão

* Git
* GitHub

---

# 12. Estrutura de Pastas (Sugestão)

```
seosong/

├── assets/
│   ├── css/
│   ├── js/
│   ├── img/
│
├── controllers/
│
├── models/
│
├── views/
│
├── routes/
│
├── database/
│
├── uploads/
│
├── config/
│
└── index.php
```

---

# 13. Melhorias Futuras

* Reprodução de músicas em streaming.
* Sistema de recomendações.
* Histórico de reprodução.
* Curtidas em playlists.
* Compartilhamento de playlists.
* Seguir artistas.
* Comentários.
* Aplicativo mobile.
* Painel administrativo.
* Upload de músicas.
* Integração com Spotify e YouTube.
* Busca avançada com múltiplos filtros.
* Estatísticas de reprodução.

---

# 14. Conclusão

O **Seosong Integrado V2** é um sistema completo para gerenciamento de músicas, artistas, álbuns e playlists. Sua arquitetura foi planejada para ser escalável, permitindo futuras integrações e funcionalidades mais avançadas, como streaming, recomendações e APIs, mantendo uma estrutura organizada e preparada para crescimento.
