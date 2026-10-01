# Correções feitas no SeoSong

## Configuração (faça isso antes de rodar)

A chave da IA e a senha do banco saíram do `application.properties`. Informe de um destes jeitos:

- **Arquivo local (mais fácil no Eclipse):** copie `secrets.properties.example` para `secrets.properties`, na raiz do projeto, e preencha. Esse arquivo está no `.gitignore`.
- **Variáveis de ambiente:** `ANTHROPIC_API_KEY` e, se precisar, `DB_PASSWORD`, `DB_URL`, `DB_USERNAME` e `LLM_MODEL`.

> A chave antiga foi enviada no zip, então **revogue essa chave** no console da Anthropic e gere uma nova.

## Os 6 pontos apontados

| # | Problema | Correção |
|---|----------|----------|
| 1 | `ia.html` chamava `/llm/songs/{id}/detect-genre`, mas o controller usava `/musics/` (dava 404) | O controller agora atende `/llm/songs/...`. O caminho `/musics/...` continua funcionando como alias |
| 2 | `PlaylistService.update` ignorava a `description` | Agora copia a descrição |
| 3 | A senha era salva em texto puro e voltava no JSON | Hash BCrypt (`spring-security-crypto`) e `@JsonProperty(WRITE_ONLY)`. As senhas que já existem são convertidas sozinhas na inicialização (`PasswordMigration`). Um PUT sem senha mantém a senha atual |
| 4 | Erros da IA voltavam como texto com status 200 | `askLlm` lança `LlmException`, que vira HTTP 502 com `{ "error": "..." }`. A mensagem é clara: chave ausente, chave inválida, modelo inexistente, limite de uso ou timeout. O frontend mostra essa mensagem |
| 5 | A chave da API estava no `application.properties` | O valor vem de `${ANTHROPIC_API_KEY}` ou do `secrets.properties` |
| 6 | Pastas `src/target/` e `target/` no zip | Removidas. Foi criado um `.gitignore` |

## Outros problemas encontrados e corrigidos

### Backend
- **As playlists eram criadas sem dono.** `Playlist.user` tinha `@JsonIgnore`, então o `{ "user": { "id": 1 } }` do formulário era descartado. É por isso que as playlists 9 a 13 do banco estão com `user_id` NULL. Agora o campo é aceito na entrada, o JSON devolve `userId` e o usuário é obrigatório na criação.
- **O PUT de playlist apagava o dono**, porque fazia `setUser(null)`. Agora só troca o dono se vier um novo.
- **O CEP nunca era salvo.** O formulário mandava `cep`, mas a entidade usa `zipCode`, e a listagem lia `user.cep`.
- **Buscar por um ID que não existe dava erro 500.** Agora responde 404 com mensagem. Foi criado um `GlobalExceptionHandler` com respostas JSON para 400, 404, 409 e 502.
- **Exclusões quebravam por chave estrangeira:**
  - excluir uma música tira ela antes das playlists e dos favoritos;
  - excluir um usuário apaga as playlists dele;
  - excluir um artista que ainda tem músicas responde 409 com explicação;
  - excluir um ID que não existe responde 404 (antes respondia 204).
- **Um POST com `id` no corpo sobrescrevia um registro existente.** Agora o `id` é ignorado na criação.
- **Música com artista inexistente** dava 500. Agora responde 400 ("Artista #X não existe"), e a resposta já traz o artista completo.
- **Cadastro de usuário:** e-mail e senha são obrigatórios, e um e-mail repetido responde 409.
- **A chamada à IA não tinha timeout** e podia travar a requisição para sempre. Agora tem 10 s para conectar e 60 s para ler.
- **Ajustes na IA:**
  - a playlist por vibe lê os IDs mesmo quando a IA responde "IDs: 3, 7";
  - o gênero é limpo de aspas e ponto final;
  - a descrição é cortada em 500 caracteres, o tamanho da coluna;
  - o gosto do usuário não repete músicas.
- **Entrada inválida em `/llm/vibe-playlist`** (sem `userId`) dava 500. Agora responde 400.

### Frontend
- **Criado o `api.js`**, compartilhado por todas as páginas. Ele traz:
  - a URL da API (usa o próprio endereço do servidor, sem `localhost:8083` fixo);
  - o tratamento de erros, que mostra a mensagem do backend;
  - o escape de HTML;
  - a formatação de datas.
- **As datas apareciam um dia antes.** `new Date("2008-04-27")` é lido como UTC e, no fuso do Brasil, vira 26/04.
- **Textos digitados pelo usuário iam direto para o `innerHTML`.** Um nome com `<` ou aspas quebrava a página (XSS). Agora tudo passa por `esc()`.
- **O `onerror` das imagens quebrava com nomes que têm apóstrofo**, como "D'Angelo". Foi trocado por `data-fallback`.
- **Campos numéricos vazios viravam 0** (ano 0, duração 0). Agora viram `null`.
- **A descrição do artista aceitava 500 caracteres no formulário**, mas a coluna tem 255. O formulário agora limita a 255.

## Opcional
O arquivo `db/limpeza-opcional.sql` tem consultas para:
- dar um dono às playlists que ficaram sem usuário, ou apagá-las;
- trocar os anos e durações 0 por NULL.

## Rodada 2: melhorias pedidas depois dos testes

- **Cadastro de playlist:**
  - o dono é escolhido numa lista de usuários (nome e e-mail), sem precisar decorar o ID;
  - as músicas também são escolhidas numa lista, que só mostra as que ainda não estão na playlist.
- **Cadastro de música:**
  - o artista é escolhido numa lista em ordem alfabética, e há um link para cadastrar um artista novo;
  - depois de salvar, o artista continua selecionado, para cadastrar várias músicas dele em sequência.
- **Favoritos:**
  - a página de músicas mostra para qual usuário você está favoritando, com o total de favoritas e um link para o perfil;
  - aparece um aviso a cada favorito adicionado ou removido;
  - depois de cada clique, o estado do coração vem do servidor (antes a página só "adivinhava").
- **Atalhos no perfil:** "♡ Favoritar músicas" e "+ Nova playlist" já abrem com aquele usuário selecionado. Também dá para usar `musicas.html?userId=X` e `cadastro-playlist.html?userId=X` direto.
