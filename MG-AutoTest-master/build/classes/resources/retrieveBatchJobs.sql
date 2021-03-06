select JOB_TEMPLATE.OBJECT_ID, 
       JOB_TEMPLATE.JOB_IDENTIFIER as IDENTIFIER, 
       REFERENCE_DATA.NAME as ACTIVATION_STATUS, 
       JOB_TEMPLATE.PRIORITY 
from JOB_TEMPLATE 
join SCHEDULER ON (JOB_TEMPLATE.FK_DESIRED_SCH = SCHEDULER.OBJECT_ID and SCHEDULER.NAME = ?) 
join REFERENCE_DATA on (JOB_TEMPLATE.FRK_ACTIVATION = REFERENCE_DATA.CODE and JOB_TEMPLATE.FSK_ACTIVATION = REFERENCE_DATA.SUBCLASS and REFERENCE_DATA.CODE = ?)  
order by JOB_TEMPLATE.PRIORITY asc
