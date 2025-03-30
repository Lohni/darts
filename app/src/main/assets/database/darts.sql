-- Create Game (Classic 501 DO)
insert into game (g_id, g_credat, g_num_sets, g_num_legs, g_game_mode, g_winner) 
VALUES
(0, "2024-01-09", 2, 2, 0, -1);

insert into game_player (gp_game, gp_player) 
VALUES
(0, 2),
(0, -1);


insert into "set" (s_id, s_game, s_ordinal, s_winner) 
VALUES
(0, 0, 0, -1);

insert into leg (l_id, l_set, l_ordinal, l_winner) 
VALUES
(0, 0, 0, -1),
(1, 0, 0, -1),
(2, 0, 0, 2);


insert into throw (t_leg, t_player, t_turn, t_ordinal, t_field, t_field_type, t_game_mode_step, t_score) VALUES
(0, -1, 0, 0, 20, 3, null, 60),
(0, -1, 0, 1, 20, 3, null, 60),
(0, -1, 0, 2, 20, 3, null, 60),
(0, -1, 1, 0, 20, 3, null, 60),
(0, -1, 1, 1, 20, 3, null, 60),
(0, -1, 1, 2, 20, 3, null, 60),
(0, -1, 2, 0, 20, 3, null, 60),
(0, -1, 2, 1, 19, 3, null, 57),
(0, -1, 2, 2, 12, 2, null, 24);


insert into throw (t_leg, t_player, t_turn, t_ordinal, t_field, t_field_type, t_game_mode_step, t_score) VALUES
(1, -1, 0, 0, 20, 3, null, 60),
(1, -1, 0, 1, 20, 3, null, 60),
(1, -1, 0, 2, 20, 3, null, 60),
(1, -1, 1, 0, 20, 3, null, 60),
(1, -1, 1, 1, 20, 3, null, 60),
(1, -1, 1, 2, 20, 3, null, 60),
(1, -1, 2, 0, 20, 3, null, 60),
(1, -1, 2, 1, 19, 3, null, 57),
(1, -1, 2, 2, 12, 2, null, 24);

insert into throw (t_leg, t_player, t_turn, t_ordinal, t_field, t_field_type, t_game_mode_step, t_score) VALUES
(2, 2, 0, 0, 20, 3, null, 60),
(2, 2, 0, 1, 20, 3, null, 60),
(2, 2, 0, 2, 20, 3, null, 60),
(2, 2, 1, 0, 20, 3, null, 60),
(2, 2, 1, 1, 20, 3, null, 60),
(2, 2, 1, 2, 20, 3, null, 60),
(2, 2, 2, 0, 20, 3, null, 60),
(2, 2, 2, 1, 19, 3, null, 57),
(2, 2, 2, 2, 12, 2, null, 24),
(2, -1, 0, 0, 20, 3, null, 60),
(2, -1, 0, 1, 20, 3, null, 60),
(2, -1, 0, 2, 20, 3, null, 60),
(2, -1, 1, 0, 20, 3, null, 60),
(2, -1, 1, 1, 20, 3, null, 60),
(2, -1, 1, 2, 20, 3, null, 60),
(2, -1, 2, 0, 20, 3, null, 60),
(2, -1, 2, 1, 19, 3, null, 57);