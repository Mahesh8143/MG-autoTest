select JOB_STEP_TMP_PARAM.PARAM_KEY, JOB_STEP_TMP_PARAM.PARAM_VALUE 
from JOB_STEP_TMP_PARAM 
where JOB_STEP_TMP_PARAM.FK_OWNER = ?