31<24prompt script d'intégration des propositions d'admission calculées dans les tables de la prod


prompt ============================================================================================================
prompt
prompt						INTEGRATION DES PROPOSITIONS
prompt
prompt ============================================================================================================

var retour NUMBER;
var mess_err VARCHAR2(4000);
var mess_aff VARCHAR2(4000);
var dummy NUMBER;

DECLARE 
-- les en attente sur un voeu
CURSOR curs IS
SELECT 	a.g_cn_cod,         
		a.g_ti_cod, 				a.g_ta_cod,					a.i_rh_cod,
		a.c_gp_cod,				a.c_gi_cod						
FROM a_adm_prop a
-- Sauf pour les propositions déjà faite ou refusées. SEules les en attentes peuvent être refaites
WHERE NOT EXISTS (SELECT 1 FROM a_adm ad, a_sit_voe sv
			WHERE g_cn_cod=a.g_cn_cod
			AND   g_ta_cod=a.g_ta_cod
			AND   i_rh_cod=a.i_rh_cod
			AND   ad.a_sv_cod=sv.a_sv_cod
			AND   (a_sv_flg_dem=1 OR a_sv_flg_oui=1))
AND   nb_jrs=(SELECT TRUNC(SYSDATE+1) - trunc(to_date(g_pr_val, 'DD/MM:YYYY:HH24')) FROM g_par WHERE g_pr_cod=35)			
;


BEGIN

	FOR c_cddt IN curs
	LOOP -- On fait la proposition ...
		:retour:=pk_admission_public.proposition(	c_cddt.g_cn_cod,		c_cddt.g_ti_cod,			c_cddt.g_ta_cod,	
											c_cddt.i_rh_cod,		c_cddt.c_gp_cod,			c_cddt.c_gi_cod,				
											1,					10,
											447240, 				null, 					0, 
											0, 					1, 						'10.1.0.99',
											0,					NULL,				
											:mess_err, 			:mess_aff);
		IF :retour!=0 THEN EXIT; END IF;				
				
		-- Et on recalcule les flags
		:retour := pk_admission_public.MAJ_flc_adm_cddt(				c_cddt.g_cn_cod,	
											447240, 				null, 					0, 
											0, 					1, 						'10.1.0.99',
											0,					NULL,				
											:mess_err, 			:mess_aff);
		IF :retour!=0 THEN EXIT; END IF;

		commit;

	END LOOP;
	

END;
/




prompt ============================================================================================================
prompt
prompt						FIN INTEGRATION DES PROPOSITIONS
prompt
prompt ============================================================================================================


