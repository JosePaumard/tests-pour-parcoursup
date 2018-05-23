SET SERVEROUTPUT ON;

DECLARE

TYPE lig_cla_pdg IS RECORD (
g_ti_cod				g_tri_ins.g_ti_cod%TYPE,
c_gp_cod				c_can_grp.c_gp_cod%TYPE,
c_cg_ran				c_can_grp.c_cg_ran%TYPE,
c_cg_ord_app			NUMBER(6),   --   c_can_grp.c_cg_ord_app%TYPE;
g_cn_cod				c_can_grp.g_cn_cod%TYPE,
g_cn_brs_lyc_cer		NUMBER(1),
i_is_flc_sec			i_ins.i_is_flc_sec%TYPE
);

TYPE tab_cla_pdg IS TABLE OF lig_cla_pdg index by binary_integer;

CURSOR c_curs IS
SELECT * FROM  va_rec_grp 
;

l_c_cg_ord_app			c_can_grp.c_cg_ord_app%TYPE;
l_tab 				tab_cla_pdg;
i					NUMBER;
j					NUMBER;
l_nbr_brs				NUMBER;
l_nbr_res				NUMBER;
l_boucle_traitee		NUMBER;
l_suivant_trouve		NUMBER;

l_brs_trouve			NUMBER;
l_brs_res_trouve		NUMBER;
l_res_trouve			NUMBER;

BEGIN

	FOR c_rec IN c_curs
	LOOP 
		-- On recupere les donnees du classement initial et les flags des candidats dans un tableau
		SELECT * 
		BULK COLLECT INTO l_tab
		FROM va_cla_pdg
		WHERE c_gp_cod=c_rec.c_gp_cod;

		-- On part au numero 1, pour anticiper le taux sur l ordre d appel a venir
		l_c_cg_ord_app:=1;
		-- Le nbr de boursier dans l ordre d appel est egal a zero au depart.
		l_nbr_brs:=0;
		-- le nbr de residents dans l ordre d appel est egal a zero au depart.
		l_nbr_res:=0;
		-- les boursiers doivent toujours etre recherches
		l_brs_trouve:=0;
		-- Les boursiers residents et les residents doivent etre recherches si le flag resident est a oui
		IF c_rec.a_rc_flg_tau_non_res=1
		THEN l_brs_res_trouve:=0;
			l_res_trouve:=0;
		ELSE l_brs_res_trouve:=-1;
			l_res_trouve:=-1;
		END IF;
		
		
		i:=1;	
		-- Pour tous les elements du tableau, on va traiter
		WHILE i <=  l_tab.COUNT
		LOOP l_boucle_traitee:=0;
		
			-- Si les taux de boursiers et de resident ne sont pas respectes et qu il reste des boursiers residents, on va cherche un boursier resident   
			IF ((l_nbr_brs/l_c_cg_ord_app)*100) < c_rec.a_rc_tau_brs_rec AND ((l_nbr_res/l_c_cg_ord_app)*100) <  c_rec.a_rc_tau_res_rec AND l_brs_res_trouve!=-1 AND l_boucle_traitee=0
		     THEN	-- on va cherche un boursier resident non traite
				j:=i;
		          l_brs_res_trouve:=0;
				WHILE j <= l_tab.COUNT AND l_brs_res_trouve=0
		          LOOP -- Des qu on trouve un boursier resident non ordonne, c est ok
					IF l_tab(j).g_cn_brs_lyc_cer=1 AND l_tab(j).i_is_flc_sec=1 AND l_tab(j).c_cg_ord_app IS NULL
		          	THEN l_tab(j).c_cg_ord_app:=l_c_cg_ord_app;
						l_nbr_brs:=l_nbr_brs+1;
		          		l_nbr_res:=l_nbr_res+1;
		          		l_brs_res_trouve:=1;
		          		l_boucle_traitee:=1;
		          	END IF;
		          	j:=j+1;
		          END LOOP;
		          -- Si on n a pas trouve de boursier resident dans la liste, c est qu il n y en a plus
		          IF l_brs_res_trouve=0
		          THEN l_brs_res_trouve:=-1;
				END IF;
			END IF;
	

			-- Si on n a pas traite la ligne ci-dessus et que
			--	le taux de boursier n est pas respecte et qu il reste des boursiers, on va cherche un boursier   
			IF ((l_nbr_brs/l_c_cg_ord_app)*100) < c_rec.a_rc_tau_brs_rec AND l_brs_trouve!=-1 AND l_boucle_traitee=0
		     THEN 
				-- on va cherche un boursier non traite
				j:=i;
		          l_brs_trouve:=0;
				WHILE j <= l_tab.COUNT AND l_brs_trouve=0
		          LOOP -- Des qu on trouve un boursier non ordonne, c est ok
					IF l_tab(j).g_cn_brs_lyc_cer=1 AND l_tab(j).c_cg_ord_app IS NULL
		          	THEN l_tab(j).c_cg_ord_app:=l_c_cg_ord_app;
						l_nbr_brs:=l_nbr_brs+1;
						-- On met a jour le nombre de residents si le candidat l est
						IF l_tab(j).i_is_flc_sec=1 THEN l_nbr_res:=l_nbr_res+1; END IF;
		          		l_brs_trouve:=1;
		          		l_boucle_traitee:=1;
		          	END IF;
		          	j:=j+1;
		          END LOOP;
		          -- Si on n a pas trouve de boursier  dans la liste, c est qu il n y en a plus
		          IF l_brs_trouve=0
		          THEN l_brs_trouve:=-1;
				END IF;
			END IF;




			-- Si on n a pas traite la ligne ci-dessus et que
			-- Si le taux de resident n est pas respecte et qu il reste des residents, on va cherche un resident   
			IF ((l_nbr_res/l_c_cg_ord_app)*100) < c_rec.a_rc_tau_res_rec AND l_res_trouve!=-1 AND l_boucle_traitee=0
		     THEN -- on va cherche un resident non traite
				j:=i;
		          l_res_trouve:=0;
				WHILE j <= l_tab.COUNT AND l_res_trouve=0
		          LOOP -- Des qu on trouve un resident non ordonne, c est ok
					IF l_tab(j).i_is_flc_sec=1 AND l_tab(j).c_cg_ord_app IS NULL
		          	THEN l_tab(j).c_cg_ord_app:=l_c_cg_ord_app;
						l_nbr_res:=l_nbr_res+1;
						-- On met a jour le nombre de boursiers si il l est
						IF l_tab(j).g_cn_brs_lyc_cer=1 THEN l_nbr_brs:=l_nbr_brs+1; END IF;
		          		l_res_trouve:=1;
		          		l_boucle_traitee:=1;
		          	END IF;
		          	j:=j+1;
		          END LOOP;
		          -- Si on n a pas trouve de  resident dans la liste, c est qu il n y en a plus
		          IF l_res_trouve=0
		          THEN l_res_trouve:=-1;
				END IF;

			END IF;
  

		     -- Si on n a pas trouve de candidat ici, on prend celui qui vient sans condtion 
		     IF l_boucle_traitee=0 
			THEN j:=i;
				l_tab(j).c_cg_ord_app:=l_c_cg_ord_app;
				-- On met a jour les nombres de boursiers et residents si il l est
				IF l_tab(j).g_cn_brs_lyc_cer=1 THEN l_nbr_brs:=l_nbr_brs+1;	END IF;
				IF l_tab(j).i_is_flc_sec=1 THEN l_nbr_res:=l_nbr_res+1; END IF;
			END IF;
							
			j:=i;
			l_suivant_trouve:=0;
			-- on calcule la ligne i sur laquelle on doit ensuite travailler (c est a dire qui n est pas affecte d un ordre d appel), car il a pu progresse de 0 a X en une fois
			WHILE j <= l_tab.COUNT AND l_suivant_trouve=0
			LOOP IF l_tab(j).c_cg_ord_app IS NOT NULL
				THEN i:=i+1;
					j:=j+1;
				ELSE	-- on est sur un candidat non ordonne, on sort
					l_suivant_trouve:=1;
				END IF;
			END LOOP;
			
			-- on traite un ordre d appel supplementaire a chaque tour
			l_c_cg_ord_app:=l_c_cg_ord_app+1;
					 
		END LOOP;
			
		-- On affiche les resultats
		i:=1;
		WHILE i <= l_tab.COUNT
		LOOP i:=i+1;
		END LOOP;

		-- On ecrit le resultat pour le groupe a partir du tableau en memoire		
		FORALL j IN l_tab.FIRST..l_tab.LAST
    			UPDATE c_can_grp cg
				SET cg.c_cg_ord_app = l_tab(j).c_cg_ord_app
			WHERE cg.g_cn_cod = l_tab(j).g_cn_cod
			AND   cg.c_gp_cod = l_tab(j).c_gp_cod
			AND   cg.c_cg_ran = l_tab(j).c_cg_ran;

		COMMIT; -- On enregistre a chaque groupe
	END LOOP;
	
END;
/

