CREATE OR REPLACE FUNCTION SP_PURGE_LICENSE_72_HOURS()
    RETURNS void
    LANGUAGE 'plpgsql'
AS $BODY$
DECLARE
	 REC RECORD;
BEGIN
  /*  EliteCSM NetVertex Module
  EliteCore Technologies Pvt. Ltd. */
  FOR REC IN (SELECT id
  FROM TBLM_LICENSE
  WHERE LAST_UPDATE_TIME < CURRENT_TIMESTAMP - INTERVAL '72 HOURS'
  )
  LOOP
    DELETE FROM TBLM_LICENSE R WHERE R.ID = REC.ID;
  END LOOP;
END;
$BODY$;

DO $$
DECLARE
    jid integer;
    scid integer;
	username varchar;
BEGIN
select user into username; --select current user(Application user)
-- Creating a new job
INSERT INTO pgagent.pga_job(
    jobjclid, jobname, jobdesc, jobhostagent, jobenabled
) VALUES (
    1::integer, 'SP_PURGE_LICENSE_72_HOURS'::text, ''::text, ''::text, true
) RETURNING jobid INTO jid;

-- Steps
-- Inserting a step (jobid: NULL)
INSERT INTO pgagent.pga_jobstep (
    jstjobid, jstname, jstenabled, jstkind,
    jstconnstr, jstdbname, jstonerror,
    jstcode, jstdesc
) VALUES (
    jid, 'SP_PURGE_LICENSE_72_HOURS-Step1'::text, true, 's'::character(1),
    ''::text, 'postgres'::name, 'f'::character(1),
    'set search_path = '|| username ||';
select SP_PURGE_LICENSE_72_HOURS();'::text, ''::text
) ;

-- Schedules
-- Inserting a schedule
INSERT INTO pgagent.pga_schedule(
    jscjobid, jscname, jscdesc, jscenabled,
    jscstart, jscend,    jscminutes, jschours, jscweekdays, jscmonthdays, jscmonths
) VALUES (
    jid, 'Sche1'::text, ''::text, true,
    '2017-07-21 15:52:36+05:30'::timestamp with time zone, '2020-07-21 15:52:32+05:30'::timestamp with time zone,
    -- Minutes
    ARRAY[false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Hours
    ARRAY[true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Week days
    ARRAY[false, false, false, false, false, false, false]::boolean[],
    -- Month days
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false]::boolean[],
    -- Months
    ARRAY[false, false, false, false, false, false, false, false, false, false, false, false]::boolean[]
) RETURNING jscid INTO scid;
END
$$;