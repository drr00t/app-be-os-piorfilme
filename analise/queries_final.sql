select 
	films.producers,
	(select count(*) from films as f_s where f_s.producers = films.producers and f_s.winner = "yes") as wins,
	(
		(select min(year) from films as f_s_nw where f_s_nw.producers = films.producers and f_s_nw.winner = "yes" and f_s_nw.year not in (select min(year) from films as f_s_fw where f_s_fw.producers = films.producers and f_s_fw.winner = "yes")) -
		(select min(year) from films as f_s_fw where f_s_fw.producers = films.producers and f_s_fw.winner = "yes")
	) as wins_interval,
	(select min(year) from films as f_s_fw where f_s_fw.producers = films.producers and f_s_fw.winner = "yes") as first_win,
	
	(select min(year) from films as f_s_nw where f_s_nw.producers = films.producers and f_s_nw.winner = "yes" and f_s_nw.year not in (select min(year) from films as f_s_fw where f_s_fw.producers = films.producers and f_s_fw.winner = "yes")) as next_win
	
from films
GROUP by films.producers
ORDER by wins DESC
