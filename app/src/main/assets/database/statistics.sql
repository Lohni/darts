select p_id, count(s_winner) as setsWon, count(l_winner) as legsWon, cast(sum(t_score) as float) / count(t_score)*3 as avg from game
join game_player on gp_game = g_id
join player on p_id = gp_player
join "set" on s_game = g_id
join leg on l_set = s_id
join throw on t_leg = l_id
group by p_id, s_winner, l_winner, t_player;


select gp_player, count(distinct s_winner), count(distinct l_id), cast(sum(t_score) as float) / count(t_score)*3 as avg from game
join game_player on gp_game = g_id
left join "set" on s_game = g_id
left join leg on l_set = s_id and l_winner = gp_player
left join throw on t_leg = l_id
where g_id = 0
group by gp_player;


select s_winner, count(s_winner) from game
join "set" on s_game = g_id
where g_id = 0
group by s_winner;

select l_winner, count(l_winner) from game
join "set" on s_game = g_id
join leg on l_set = s_id
where g_id = 0
group by l_winner;


SELECT
t_player,
(select count(s_id) from "set" where s_game = 0 and s_winner = -1) as setsWon,
(select count(l_id) from leg join "set" on s_game = 0 and s_id = l_set where s_game = 0 and l_winner = -1) as legsWon,
cast(sum(t_score) as float) / count(t_score)*3 as avg
FROM game
join "set" on s_game = g_id
join leg on l_set = s_id
join throw on t_leg = l_id and t_player = -1
where g_id = 0;

SELECT
t_player,
(select count(s_id) from "set" where s_game = 0 and s_winner = 2) as setsWon,
(select count(l_id) from leg join "set" on s_game = 0 and s_id = l_set where s_game = 0 and l_winner = 2) as legsWon,
cast(sum(t_score) as float) / count(t_score)*3 as avg
FROM game
join "set" on s_game = g_id
join leg on l_set = s_id
join throw on t_leg = l_id and t_player = 2
where g_id = 0;

-- game overwiew
SELECT g_id, gm_name, g_num_sets, g_num_legs, count(gp_player), player.*
FROM GAME
JOIN GAME_MODE on gm_id = g_game_mode
JOIN GAME_PLAYER on gp_game = g_id
JOIN PLAYER on g_winner = p_id
GROUP BY g_id
ORDER BY g_id desc;

-- set overview, input: pId, gId, sId
SELECT
t_player,
(select count(l_id) from leg where s_game = 0 and l_winner = 2 and l_set = 0) as legsWon,
cast(sum(t_score) as float) / count(t_score)*3 as avg
FROM game
join "set" on s_game = g_id
join leg on l_set = s_id
join throw on t_leg = l_id and t_player = 2
where g_id = 0 and s_id = 0;

-- leg overview, input: pId, gId, sId, lId
SELECT
player.*,
(gm_start_score - sum(t_score)) as score,
cast(sum(t_score) as float) / count(t_score)*3 as avg
FROM leg
join game on g_id = 0
join game_mode on gm_id = g_game_mode
join throw on t_leg = l_id
join player on p_id = t_player
where l_id = 2
group by t_player
order by score asc;


-- sets with winner and winner avg, input: gId
SELECT
s_ordinal as setId,
s_winner as winner,
cast(sum(t_score) as float) / count(t_score)*3 as avg
FROM game
join "set" on s_game = g_id
join leg on l_set = s_id
join throw on t_leg = l_id and t_player = s_winner
where g_id = 0
group by l_set
order by s_id asc;

-- legs with winner and winner avg, input: gId, sId
SELECT
l_ordinal as legId,
l_winner as winner,
cast(sum(t_score) as float) / count(t_score)*3 as avg
FROM leg
join throw on t_leg = l_id and t_player = l_winner
where l_set = 0
group by l_id
order by l_id asc;

-- turn overview
select t_turn, 501 - sum(t_score) as preTurnScore, throw.* FROM throw
where t_leg = 2
group by t_player, t_turn
order by t_turn asc;


