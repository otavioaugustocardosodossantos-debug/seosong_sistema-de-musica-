-- Ajustes OPCIONAIS no banco atual (rode no pgAdmin/psql se quiser).

-- 1) Playlists sem dono (criadas antes da correção: o user_id ficava NULL).
--    Elas não aparecem em nenhum perfil. Veja quais são:
SELECT id, name FROM playlist WHERE user_id IS NULL;

--    Opção A: atribuir todas a um usuário (troque o 2 pelo id desejado)
-- UPDATE playlist SET user_id = 2 WHERE user_id IS NULL;

--    Opção B: apagar (primeiro os vínculos com músicas)
-- DELETE FROM playlist_song WHERE playlist_id IN (SELECT id FROM playlist WHERE user_id IS NULL);
-- DELETE FROM playlist WHERE user_id IS NULL;

-- 2) Músicas com ano/duração 0 (o formulário antigo mandava 0 quando o campo ficava vazio)
-- UPDATE song SET year = NULL WHERE year = 0;
-- UPDATE song SET duration = NULL WHERE duration = 0;

-- 3) Senhas: NÃO precisa fazer nada. Ao iniciar, a aplicação converte
--    automaticamente as senhas em texto puro para hash BCrypt.
