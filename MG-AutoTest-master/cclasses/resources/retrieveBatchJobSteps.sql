select JOB_STEP_TEMPLATE.OBJECT_ID, JOB_STEP_TEMPLATE.SEQUENCE,
       REFERENCE_DATA.CODE as JOB_STEP_TYPE 
from JOB_STEP_TEMPLATE 
join JOB_TEMPLATE on (JOB_TEMPLATE.OBJECT_ID = JOB_STEP_TEMPLATE.FK_JOB and JOB_TEMPLATE.OBJECT_ID = ?)
join REFERENCE_DATA on (JOB_STEP_TEMPLATE.FRK_STEP_TYPE = REFERENCE_DATA.CODE and JOB_STEP_TEMPLATE.FSK_STEP_TYPE = REFERENCE_DATA.SUBCLASS)
order by JOB_STEP_TEMPLATE.SEQUENCE asc